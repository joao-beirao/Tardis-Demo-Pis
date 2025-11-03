import json
import aiohttp

def URL(id):
    return f"http://localhost:8080/rest/dcr/events/input/{id}/"

async def executeEvent(URL, BODY):
    print("Trying to execute: "+URL)
    async with aiohttp.ClientSession() as session:
        async with session.put(URL, json=BODY) as response:
            resp_data = await response.text()
            print(f"PUT request sent. Response: {resp_data}")

async def executeReplyConsume(id):
    payload = {
        "eventID": f"{id}",
        "value": {
            "type": "Record",
            "value": {
                "cost": {
                    "type": "Number",
                    "value": 123
                },
                "kw": {
                    "type": "Number",
                    "value": 123
                }
            }
        }
    }
    await executeEvent(URL(id), payload)


async def executeConsume(id):
    payload = {
        "eventID": f"{id}",
        "value": {
            "type": "Record",
            "value": {
                "kw": {
                    "type": "Number",
                    "value": 123
                }
            }
        }
    }
    await executeEvent(URL(id), payload)


async def executeAccept(id):
    payload = {
        "eventID": f"{id}",
        "value": {
            "type": "Number",
            "value": 123 #No Visualizer manda null
        }
    }
    await executeEvent(URL(id), payload)


async def executeReply(id):
    payload = {
        "eventID": f"{id}",
        "value": {
            "type": "Number",
            "value": 123 #No Visualizer manda null
        }
    }
    await executeEvent(URL(id), payload)

async def executeRequestForecast(id):
    payload = {
        "eventID": f"{id}",
        "value": {
            "type": "Unit",
            "value": ""
        }
    }
    await executeEvent(URL(id), payload)

async def executeAccounting(id):
    payload = {
        "eventID": f"{id}",
        "value": {
            "type": "Number",
            "value": "123"
        }
    }
    await executeEvent(URL(id), payload)

#def searchIDByLabel(label, Json):
#    try:
#        data = json.loads(Json)
#        events = data.get("events", [])
#        for event in events:
#            if event.get("label") == label:
#                print("Event "+label+" has now id: "+event.get("id"))
#                return event.get("id")
#        print(f"Event with label '{label}' not found.")
#        return None
#    except Exception as e:
#        print(f"Error parsing JSON or searching for label: {e}")
#        return None