import pymongo
import os
import time
import logging
import pandas as pd
from dotenv import load_dotenv

load_dotenv()

logging.basicConfig(level=logging.INFO, format='%(asctime)s :: %(levelname)s :: %(message)s')
log = logging.getLogger(__name__)
log.setLevel(logging.DEBUG)

TOMTOM_API_KEY = os.environ["TOMTOM_API_KEY"]
MONGODB_URI = os.environ["MONGODB_URI"]
INTERVAL = 60 # in seconds
ids = [] # incidents already inserted

client = pymongo.MongoClient(MONGODB_URI)
db = client.test

def convert_to_df():
    incidents = []
    events = []
    coordinates = []
    for incident in db.incidents.find({}):
        incident_part_1 = { key: incident[key] for key in ['_id', 'type'] }
        incident_part_2 = { f'properties_{key}': incident['properties'][key] for key in ['id', 'iconCategory', 'magnitudeOfDelay', 'startTime', 'endTime', 'from', 'to', 'length', 'delay','aci']}
        incident_part_3 = { f'geometry_{key}': incident['geometry'][key] for key in ['type']}
        incident_part_1.update(incident_part_2)
        incident_part_1.update(incident_part_3)
        incidents.append(incident_part_1)

        for event in incident['properties']['events']:
            event_part_1 = { 'incident_id': incident['_id'] }
            event_part_2 = { f'event_{key}': event[key] for key in ['code', 'description', 'iconCategory'] }
            event_part_1.update(event_part_2)
            events.append(event_part_1)

        for coordinate in incident['geometry']['coordinates']:
            coordinate_part_1 = { 'incident_id': incident['_id'] }
            coordinate_part_2 = { 'lon': coordinate[0], 'lat': coordinate[1]}
            coordinate_part_1.update(coordinate_part_2)
            coordinates.append(coordinate_part_1)

    return { 'incidents': pd.DataFrame.from_records(incidents), 'events': pd.DataFrame.from_records(events), 'coordinates': pd.DataFrame.from_records(coordinates) }

if __name__ == '__main__':
    dataframes = convert_to_df()

    for key, value in dataframes.items():
        value.to_csv(f'data/{key}.csv', index=False)