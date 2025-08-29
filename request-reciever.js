import WebSocket from "ws";
import { Gpio } from "onoff";
// Configure GPIO pins for LEDs (adjust BCM pin numbers to your wiring)
const leds = {
  red: new Gpio(17, "out"),
  green: new Gpio(27, "out"),
  blue: new Gpio(22, "out"),
};
function setLed(color, state) {
  if (leds[color]) {
    leds[color].writeSync(state ? 1 : 0);
  }
}
// Connect to WebSocket server
const ws = new WebSocket("ws://your-websocket-server:port");
// Handle connection open
ws.on("open", () => {
  console.log("Connected to WebSocket server");
});
// Handle messages
ws.on("message", (data) => {
  try {
    const json = JSON.parse(data);
    if (Array.isArray(json.strings)) {
      // Turn off all LEDs first
      Object.keys(leds).forEach((c) => setLed(c, 0));
      // Light up based on strings
      if (json.strings.includes("error")) setLed("red", 1);
      if (json.strings.includes("ok")) setLed("green", 1);
      if (json.strings.includes("info")) setLed("blue", 1);
    }
  } catch (err) {
    console.error("Invalid message:", err);
  }
});
// Handle errors
ws.on("error", (err) => {
  console.error("WebSocket error:", err);
});;
// Cleanup GPIO on exit
process.on("SIGINT", () => {
  Object.values(leds).forEach((led) => {
    led.writeSync(0);
    led.unexport();
  });
  process.exit();
});