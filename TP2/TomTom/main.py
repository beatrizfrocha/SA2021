import requests
import json
from tomtomAPIkey import api_key

r = requests.get(f"https://api.tomtom.com/traffic/services/5/incidentDetails?bbox=4.8854592519716675%2C52.36934334773164%2C4.897883244144765%2C52.37496348620152&fields=%7Bincidents%7Btype%2Cgeometry%7Btype%2Ccoordinates%7D%2Cproperties%7Bid%2CiconCategory%2CmagnitudeOfDelay%2Cevents%7Bdescription%2Ccode%7D%2CstartTime%2CendTime%2Cfrom%2Cto%2Clength%2Cdelay%2CroadNumbers%2Caci%7BprobabilityOfOccurrence%2CnumberOfReports%2ClastReportTime%7D%7D%7D%7D&key={api_key}")

data = r.json()
with open('data.json', 'w') as f:
    json.dump(data, f,indent=4)