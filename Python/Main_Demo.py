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

# Events tracked
CONSUME = "consume"
REPLY_FORECAST = ""
ACCEPT = ""
REPLY_CONSUME = ""


async def trigger_response():
    URL = "http://localhost:8080/rest/dcr/events/enable"  # Replace with your desired URL

    async with aiohttp.ClientSession() as session:
        async with session.get(URL) as response:
            data = await response.text()
            print("Triggered response")


def event_update_callback(Json):
    try:
        data = json.loads(Json)
        print(f"Received JSON: {data}")
        events = data.get("events", [])
        consume = False
        reply_forecast = False
        accept = False
        reply_consume = False

        for event in events:
            if (event.get("label") == CONSUME and event.get("marking", {}).get("isIncluded")):
                consume = True
            if (event.get("label") == REPLY_FORECAST and event.get("marking", {}).get("isIncluded")):
                reply_forecast = True
            if (event.get("label") == ACCEPT and event.get("marking", {}).get("isIncluded")):
                accept = True
            if (event.get("label") == REPLY_CONSUME and event.get("marking", {}).get("isIncluded")):
                reply_consume = True
        controller.turn_off_all()
        if consume:
            controller.turn_on(0)
        if reply_forecast:
            controller.turn_on(1)
        if accept:
            controller.turn_on(2)
        if reply_consume:
            controller.turn_on(3)
           
    except Exception as e:
        print(f"Error parsing JSON or counting events: {e}")



async def listen_websocket(uri):
    """
    Connect to a WebSocket server and print incoming messages
    """
    try:
        async with websockets.connect(uri) as websocket:
            print(f"Connected to {uri}")
            print("Listening for messages... (Press Ctrl+C to exit)")
            await trigger_response()

            while True:
                try:
                    # Wait for a message (with timeout to allow graceful shutdown)
                    message = await asyncio.wait_for(websocket.recv(), timeout=1.0)
                    
                    # Try to parse as JSON, if fails treat as plain text
                    try:
                        parsed_message = json.loads(message)
                        print(f"📨 JSON update received")
                        event_update_callback(message)
                    except json.JSONDecodeError:
                        print(f"📨 Text message: {message}")
                        
                except asyncio.TimeoutError:
                    # Timeout allows checking for keyboard interrupt
                    continue
                    
    except websockets.exceptions.ConnectionClosed:
        print("Connection closed by server")
    except Exception as e:
        print(f"Error: {e}")


async def main():
    uri = "ws://localhost:8080/dcr" 

    def run_websocket():
        asyncio.run(listen_websocket(uri))

    monitor = button_control.ButtonMonitor()
    monitor.set_on_button_press(lambda: asyncio.run(executeConsume("_csm_2")))
    
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