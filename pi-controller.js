const gpiod = require('gpiod');

// open the gpiochip device
const chip = gpiod.open('/dev/gpiochip0');

// get line 17 (BCM GPIO17 = pin 11)
const line = chip.getLine(17);

// request the line as output, default LOW
line.requestOutputMode();

// turn LED ON
line.setValue(1);
console.log("LED is ON. Press Ctrl+C to exit.");

// cleanup on exit
process.on('SIGINT', () => {
  line.setValue(0); // LED off
  line.release();
  chip.close();
  console.log("\nLED is OFF. Exiting...");
  process.exit();
});
