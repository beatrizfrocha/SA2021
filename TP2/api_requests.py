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
INTERVAL = 60

client = pymongo.MongoClient(MONGODB_URI)
db = client.test

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
    
    db.incidents.insert_many(data['incidents'])
    log.info("Data inserted successfully")

if __name__ == '__main__':
    while True:
        data = get_data()
        save_data(data)
        time.sleep(INTERVAL)

# datas
# cloud
# endpoints
# tratamento de dados
# correr de x em x tempo (repetidos)