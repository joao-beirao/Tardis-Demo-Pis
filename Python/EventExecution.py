import aiohttp

async def executeEvent(URL, BODY):
    async with aiohttp.ClientSession() as session:
        async with session.put(URL, json=BODY) as response:
            resp_data = await response.text()
            print(f"PUT request sent. Response: {resp_data}")

async def executeConsume(id):
    URL = f"http://p-1-1:8080/rest/dcr/events/input/{id}/"
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
    await executeEvent(URL, payload)

