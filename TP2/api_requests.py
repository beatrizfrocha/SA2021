import requests
import json
import pymongo
import os
import time
import logging
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

def init_ids():
    for incident in db.incidents.find({}):
        id = incident['properties']['id']
        ids.append(id)

    print("IDS:", len(ids))

def get_data():
    base_url = "https://api.tomtom.com/traffic/services/5"
    fields = "{incidents{type,geometry{type,coordinates},properties{id,iconCategory,magnitudeOfDelay,events{description,code,iconCategory},startTime,endTime,from,to,length,delay,roadNumbers,aci{probabilityOfOccurrence,numberOfReports,lastReportTime}}}}"    
    bbox = "-9.263105,38.591305,-9.035139,38.812612"
    
    url = f"{base_url}/incidentDetails?language=en-GB&bbox={bbox}&fields={fields}&key={TOMTOM_API_KEY}"

    log.debug(f"GET request to {url}")
    
    return requests.get(url)

def save_data(response):

    if response.status_code != 200:
        log.error(f"Error [{response.status_code}]: {response}")
        return

    log.debug(f"Converting response to JSON")
    data = response.json()

    inserted = 0

    for incident in data['incidents']:
        if incident['properties']['id'] not in ids:
            db.incidents.insert_one(incident)
            ids.append(incident['properties']['id'])
            inserted += 1
        
    log.info(f"Inserted {inserted} new incidents successfully")

if __name__ == '__main__':

    init_ids()
    
    while True:
        try:
            data = get_data()
            save_data(data)
        
        except Exception as msg:
            log.error(msg)
        
        time.sleep(INTERVAL)