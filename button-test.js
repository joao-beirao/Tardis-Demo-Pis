const buttons = require('./intput-hardware-controller');

// Example: check button states every second
setInterval(() => {
  console.log("Button states:", buttons.buttonStates);
}, 1000);

process.on('SIGINT', () => {
  buttons.cleanup();
  console.log("Clean exit.");
  process.exit();
});