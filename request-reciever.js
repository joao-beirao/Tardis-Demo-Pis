const WebSocket  = require('ws');
const Gpio  = require('onoff');

const controller = require('./controller');



// Connect to WebSocket server
const ws = new WebSocket("ws://p-1-1:8080");
// Handle connection open
ws.on("open", () => {
  console.log("Connected to WebSocket server");
});


// Handle messages
ws.on("message", (data) => {
  console.log("Server says:", data.data);
  try {
    controller.blinkLed(0);

    /*
    const json = JSON.parse(data);
    if (Array.isArray(json.strings)) {
      // Turn off all LEDs first
      Object.keys(leds).forEach((c) => setLed(c, 0));
      // Light up based on strings
      if (json.strings.includes("error")) setLed("red", 1);
      if (json.strings.includes("ok")) setLed("green", 1);
      if (json.strings.includes("info")) setLed("blue", 1);
    }
      */

  } catch (err) {
    console.error("Invalid message:", err);
  }
});

// Handle errors
ws.on("error", (err) => {
  console.error("WebSocket error:", err);
});

// Cleanup GPIO on exit
process.on("SIGINT", () => {
  controller.cleanup();
  process.exit();
});