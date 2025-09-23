const buttons = require('./intput-hardware-controller');



process.on('SIGINT', () => {
  buttons.cleanup();
  console.log("Clean exit.");
  process.exit();
});