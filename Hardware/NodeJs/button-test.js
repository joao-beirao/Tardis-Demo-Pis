const buttons = require('./input-hardware-controller');
const turnOn = require('./turn-on-all');
const turnOff = require('./turn-off-all');

async function wait(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

buttons.setOnButtonPress(async () => {
  console.log("Button was pressed! Turning off all LEDs.");
  await turnOn();
  await wait(1000);
  await turnOff();
});

process.on('SIGINT', () => {
  buttons.cleanup();
  console.log("Clean exit.");
  process.exit();
});