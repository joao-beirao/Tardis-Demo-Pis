const express = require('express');


const app = express();


app.get('/on', (_req, res) => {
    setLed(0, 1);
    res.send("LED is ON");
});


app.get('/off', (_req, res) => {
  setLed(0, 0);
  res.send("LED is OFF");
});


process.on('SIGINT', () => {
  setLed(0, 0);   // Turn LED off before exit
  led.release();
  for (let i = 0; i < led.length; i++) {
    led[i].release();
  }
  chip.close();
  console.log("\nLED OFF, exiting...");
  process.exit();
});


app.listen(3000, () => {
  console.log("Server running at http://<pi-ip>:3000");
});
