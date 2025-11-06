import asyncio
import websockets
import json
import signal
import sys
import aiohttp
import threading
import LedControll_Demo as controller
import ButtonControll as button_control
import argparse
from EventExecution_2 import executeEvent
CHIP_NUMBER = 0
BUTTON_PINS = [24, 22, 27, 17]


# Events tracked Choreography #2
# P
CONSUME = "consume"
DECISION = "decision"
REPLY_F_CAST = "reply_fcast"
REPLY_CONSUME = "csm_reply"
# CO
CONSUME_CO = "Consume_CO"
ACCOUNTING = "Accounting"
REPLY_CO = "Reply_CO"
REQUEST_FORECAST = "request_fcast"

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
    def getLastRequestForecastID(self):
        return self.last_request_forecast_id
    def getLastAccountingID(self):
        return self.last_accounting_id
    def getLastReplyCOID(self):
        return self.last_reply_co_id
    def getLastConsumeCOID(self):
        return self.last_consume_co_id

    async def trigger_response(self):
        URL = "http://localhost:8080/rest/dcr/events/enable"  # Replace with your desired URL

        async with aiohttp.ClientSession() as session:
            async with session.get(URL) as response:
                data = await response.text()
                print("Triggered response")


    # Defines the new Ids based on the received JSON from the WebSocket
    def event_update_callback(self, Json):
        try:
            data = json.loads(Json)
            print(f"Received JSON: {data}")
            events = data.get("events", [])
            # Choreography #2
            # P
            consume = False
            reply_forecast = False
            accept = False
            reply_consume = False
            self.last_consume_id = ""
            self.last_reply_id = ""
            self.last_accept_id = ""
            self.last_reply_consume_id = "" 
            # CO
            request_forecast = False
            accounting = False
            consume_co = False
            reply_co = False
            self.last_reply_co_id = ""
            self.last_consume_co_id = ""
            self.last_request_forecast_id = ""
            self.last_accounting_id = ""

            for event in events:
                # P
                if (event.get("label") == CONSUME and event.get("marking", {}).get("isIncluded")):
                    consume = True
                    self.last_consume_id =  event.get("id")

                if (event.get("label") == REPLY_F_CAST and event.get("marking", {}).get("isIncluded")):
                    reply_forecast = True
                    self.last_reply_id = event.get("id")

                if (event.get("label") == DECISION and event.get("marking", {}).get("isIncluded")):
                    accept = True
                    self.last_accept_id = event.get("id")

                if (event.get("label") == REPLY_CONSUME and event.get("marking", {}).get("isIncluded")):
                    reply_consume = True
                    self.last_reply_consume_id = event.get("id")

                # CO
                if (event.get("label") == REQUEST_FORECAST and event.get("marking", {}).get("isIncluded")):
                    request_forecast = True
                    self.last_request_forecast_id = event.get("id")

                if (event.get("label") == ACCOUNTING and event.get("marking", {}).get("isIncluded")):
                    accounting = True
                    self.last_accounting_id = event.get("id")

                if(event.get("label") == CONSUME_CO and event.get("marking", {}).get("isIncluded")):
                    consume_co = True
                    self.last_consume_co_id = event.get("id")

                if(event.get("label") == REPLY_CO and event.get("marking", {}).get("isIncluded")):
                    reply_co = True
                    self.last_reply_co_id = event.get("id")

                print("New CONSUME ID: "+ self.last_consume_id)
                print("New REPLY ID: "+ self.last_reply_id)
                print("New ACCEPT ID: "+ self.last_accept_id)
                print("New REPLY_CONSUME ID: "+ self.last_reply_consume_id)
                print("New REQUEST_FORECAST ID: "+ self.last_request_forecast_id)
                print("New ACCOUNTING ID: "+ self.last_accounting_id)
                print("New CONSUME_CO ID: "+ self.last_consume_co_id)
                print("New REPLY_CO ID: "+ self.last_reply_co_id)

            self.controller.turn_off_all()
            # P
            if consume:
                self.controller.turn_on(0)
            if reply_forecast:
                self.controller.turn_on(1)
            if accept:
                self.controller.turn_on(2)
            if reply_consume:
                self.controller.turn_on(3)
            # CO
            if request_forecast:
                self.controller.turn_on(3)
            if accounting:
                self.controller.turn_on(2)
            if reply_co:
                self.controller.turn_on(1)
            if consume_co:
                self.controller.turn_on(0)


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



########################       MAIN      ##########################


async def main(ROLE):
    uri = "ws://localhost:8080/dcr" 

    MainApp_instance = MainApp(controller, button_control)

    def run_websocket():
        asyncio.run(MainApp_instance.listen_websocket(uri))

    if ROLE == "P":
        buttonMonitor1 = button_control.ButtonMonitor(CHIP_NUMBER , BUTTON_PINS[0])
        buttonMonitor1.set_on_button_press(lambda: asyncio.run(executeEvent(MainApp_instance.getLastConsumeID() )))

        buttonMonitor2 = button_control.ButtonMonitor(CHIP_NUMBER , BUTTON_PINS[1])
        buttonMonitor2.set_on_button_press(lambda: asyncio.run(executeEvent(MainApp_instance.getLastReplyID() )))

        buttonMonitor3 = button_control.ButtonMonitor(CHIP_NUMBER , BUTTON_PINS[2])
        buttonMonitor3.set_on_button_press(lambda: asyncio.run(executeEvent(MainApp_instance.getLastAcceptID())))

        buttonMonitor4 = button_control.ButtonMonitor(CHIP_NUMBER , BUTTON_PINS[3])
        buttonMonitor4.set_on_button_press(lambda: asyncio.run(executeEvent(MainApp_instance.getLastReplyConsumeID())))
    if ROLE == "CO":

        buttonMonitor1 = button_control.ButtonMonitor(CHIP_NUMBER , BUTTON_PINS[0])
        buttonMonitor1.set_on_button_press(lambda: asyncio.run(executeEvent(MainApp_instance.getLastConsumeCOID() )))
    
        buttonMonitor2 = button_control.ButtonMonitor(CHIP_NUMBER , BUTTON_PINS[1])
        buttonMonitor2.set_on_button_press(lambda: asyncio.run(executeEvent(MainApp_instance.getLastReplyCOID() )))

        buttonMonitor3 = button_control.ButtonMonitor(CHIP_NUMBER , BUTTON_PINS[2])
        buttonMonitor3.set_on_button_press(lambda: asyncio.run(executeEvent(MainApp_instance.getLastAccountingID())))

        buttonMonitor4 = button_control.ButtonMonitor(CHIP_NUMBER , BUTTON_PINS[3])
        buttonMonitor4.set_on_button_press(lambda: asyncio.run(executeEvent(MainApp_instance.getLastRequestForecastID())))



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

    parser = argparse.ArgumentParser(description="Tardis demo hardware")
    parser.add_argument("--type", default="P", help="Type of device")
    args = parser.parse_args()

    # make args available to the rest of the program (or pass into main)
    DEVICE_TYPE = args.type

    # Install signal handler for graceful shutdown
    signal.signal(signal.SIGINT, signal_handler)

    try:
        # if you changed main signature to accept uri: asyncio.run(main(args.uri))
        asyncio.run(main(args.type))
    except KeyboardInterrupt:
        print("\nExiting (keyboard)...")
    finally:
        controller.cleanup()
        button_control.cleanup()
        sys.exit(0)
    print("\nExiting...")
