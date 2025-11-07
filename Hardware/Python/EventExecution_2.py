import aiohttp

######################################
#
# EVENT EXECUTION FUNCTIONS
# FOR CHOREOGRAPHY 2
#
######################################

def URL(id):
    return f"http://localhost:8080/rest/dcr/events/input/{id}/"

async def execute(URL, BODY):
    print("Trying to execute: "+URL)
    async with aiohttp.ClientSession() as session:
        async with session.put(URL, json=BODY) as response:
            resp_data = await response.text()
            print(f"PUT request sent. Response: {resp_data}")

async def executeEvent(id):
    if(id == ""):
        print("No event to execute.")
        return
    else:
        payload = {
            "eventID": f"{id}",
            "value": {
                "type": "Unit",
                "value": ""
            }
        }
        await execute(URL(id), payload)



