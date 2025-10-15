import asyncio
import websockets
import json
import signal
import sys
import aiohttp
import threading
import LedControll_Demo as controller
import ButtonControll as button_control
from EventExecution import executeConsume

BUTTON_PINS = [21]
CHIP_NUMBER = 0
# Events tracked
CONSUME = "consume"
REPLY_FORECAST = "r4c_replyForecast"
ACCEPT = "accept"
REPLY_CONSUME = "csm_reply"

class MainApp:

    def __init__(self, controller, button_control):
        self.controller = controller
        self.button_control = button_control

        self.last_consume_id = ""
        self.last_reply_id = ""
        self.last_accept_id = ""
        self.last_reply_consume_id = ""

    def getLastConsumeID(self):
        return self.last_consume_id
    def getLastReplyID(self):
        return self.last_reply_id
    def getLastAcceptID(self):
        return self.last_accept_id
    def getLastReplyConsumeID(self):
        return self.last_reply_consume_id

    async def trigger_response(self):
        URL = "http://localhost:8080/rest/dcr/events/enable"  # Replace with your desired URL

        async with aiohttp.ClientSession() as session:
            async with session.get(URL) as response:
                data = await response.text()
                print("Triggered response")

    def event_update_callback(self, Json):
        try:
            data = json.loads(Json)
            print(f"Received JSON: {data}")
            events = data.get("events", [])
            consume = False
            reply_forecast = False
            accept = False
            reply_consume = False

            for event in events:
                if (event.get("label") == self.CONSUME and event.get("marking", {}).get("isIncluded")):
                    consume = True
                    self.last_consume_id = self.searchIDByLabel(self.CONSUME, data)
                else:
                    self.last_consume_id = ""

                if (event.get("label") == self.REPLY_FORECAST and event.get("marking", {}).get("isIncluded")):
                    reply_forecast = True
                    self.last_reply_id = self.searchIDByLabel(self.REPLY_FORECAST, data)
                else:
                    self.last_reply_id = ""

                if (event.get("label") == self.ACCEPT and event.get("marking", {}).get("isIncluded")):
                    accept = True
                    self.last_accept_id = self.searchIDByLabel(self.ACCEPT, data)
                else:
                    self.last_accept_id = ""

                if (event.get("label") == self.REPLY_CONSUME and event.get("marking", {}).get("isIncluded")):
                    reply_consume = True
                    self.last_reply_consume_id = self.searchIDByLabel(self.REPLY_CONSUME, data)
                else:
                    self.last_reply_consume_id = ""

            self.controller.turn_off_all()
            if consume:
                self.controller.turn_on(0)
            if reply_forecast:
                self.controller.turn_on(1)
            if accept:
                self.controller.turn_on(2)
            if reply_consume:
                self.controller.turn_on(3)

        except Exception as e:
            print(f"Error parsing JSON or counting events: {e}")


    async def listen_websocket(self, uri):
        """
        Connect to a WebSocket server and print incoming messages
        """
        try:
            async with websockets.connect(uri) as websocket:
                print(f"Connected to {uri}")
                print("Listening for messages... (Press Ctrl+C to exit)")
                await self.trigger_response()

                while True:
                    try:
                        # Wait for a message (with timeout to allow graceful shutdown)
                        message = await asyncio.wait_for(websocket.recv(), timeout=1.0)

                        # Try to parse as JSON, if fails treat as plain text
                        try:
                            parsed_message = json.loads(message)
                            print(f"📨 JSON update received")
                            self.event_update_callback(message)
                        except json.JSONDecodeError:
                            print(f"📨 Text message: {message}")

                    except asyncio.TimeoutError:
                        # Timeout allows checking for keyboard interrupt
                        continue

        except websockets.exceptions.ConnectionClosed:
            print("Connection closed by server")
            self.controller.cleanup()
        except Exception as e:
            print(f"Error: {e}")

    def searchIDByLabel(self, label, data):
        try:
            # data can be dict or JSON string
            if isinstance(data, str):
                data = json.loads(data)
            events = data.get("events", [])
            for event in events:
                if event.get("label") == label:
                    return event.get("id")
            print(f"Event with label '{label}' not found.")
            return None
        except Exception as e:
            print(f"Error parsing JSON or searching for label: {e}")
            return None


########################       MAIN      ##########################


async def main():
    uri = "ws://localhost:8080/dcr" 

    MainApp_instance = MainApp(controller, button_control)

    def run_websocket():
        asyncio.run(MainApp_instance.listen_websocket(uri))

    buttonMonitor = button_control.ButtonMonitor(CHIP_NUMBER , BUTTON_PINS[0])
    buttonMonitor.set_on_button_press(lambda: asyncio.run(executeConsume(MainApp_instance.getLastConsumeID())))

    thread = threading.Thread(target=run_websocket)
    while True:
        thread.start()
        thread.join()
        print("Reconnecting to WebSocket...")
        await asyncio.sleep(1)  # Wait before reconnecting


###################################################################

def signal_handler(signum, frame):
    print("\nDisconnecting...")
    sys.exit(0)

if __name__ == "__main__":
    # Install signal handler for graceful shutdown
    signal.signal(signal.SIGINT, signal_handler)
    
    try:
        asyncio.run(main())
    except KeyboardInterrupt:
        print("\nExiting...")
    finally:
        controller.cleanup()
        button_control.cleanup()
        sys.exit(0)