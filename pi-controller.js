const { Chip, Line } = require('node-libgpiod');
const express = require('express');

const app = express();

// Keep references alive globally
const chip = new Chip(0);        // gpiochip0
const led = new Line(chip, 17);  // GPIO17 (BCM numbering)

led.requestOutputMode();


app.get('/on', (_req, res) => {
  led.setValue(1);
  res.send("LED is ON");
});


app.get('/off', (_req, res) => {
  led.setValue(0);
  res.send("LED is OFF");
});


process.on('SIGINT', () => {
  led.setValue(0);   // Turn LED off before exit
  led.release();
  chip.close();
  console.log("\nLED OFF, exiting...");
  process.exit();
});


app.listen(3000, () => {
  console.log("Server running at http://<pi-ip>:3000");
});
