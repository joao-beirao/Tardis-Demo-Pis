const buttons = require('./intput-hardware-controller');

buttons.setOnButtonPress(() => {console.log("Button was pressed! Turning off all LEDs."); } );

process.on('SIGINT', () => {
  buttons.cleanup();
  console.log("Clean exit.");
  process.exit();
});