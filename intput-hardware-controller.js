const { Chip, Line } = require('node-libgpiod');
const chip = new Chip(0);

const button = new Line(chip, 21);

let buttonState = 0;

// Request input mode
button.requestInputMode();

setInterval(() => {
  const value = button.getValue();
  if (buttonState !== value) {
    buttonState = value;
    if (buttonState != 0) {
      console.log("PRESSED");
    }
  }
}, 100);

function cleanup() {
  button.release();
  chip.close();
}

process.on('SIGINT', () => {
  cleanup();
  console.log('Clean exit');
  process.exit();
});
