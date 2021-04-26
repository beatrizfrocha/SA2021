import pymongo
import os
from dotenv import load_dotenv
from bson.json_util import dumps

load_dotenv()

MONGODB_URI = os.environ["MONGODB_URI"]

client = pymongo.MongoClient(MONGODB_URI)
db = client.test

data = db.incidents.find({})

list_incidents = list(data)

json_data = dumps(list_incidents, indent=4) 

with open('data.json', 'w') as file:
    file.write(json_data)