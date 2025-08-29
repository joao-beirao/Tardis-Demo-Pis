const express = require('express');
const controller = require('./controller');


const app = express();


app.get('/on', (_req, res) => {
    controller.setLed(0, 1);
    res.send("LED is ON");
});


app.get('/off', (_req, res) => {
  controller.setLed(0, 0);
  res.send("LED is OFF");
});


process.on('SIGINT', () => {
  controller.cleanup();
  console.log("\nLED OFF, exiting...");
  process.exit();
});


app.listen(3000, () => {
  console.log("Server running at http://<pi-ip>:3000");
});

