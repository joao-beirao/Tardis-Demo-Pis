const { Chip, Line } = require('node-libgpiod');
const chip = new Chip(0);

const buttons = [
  new Line(chip, 21),
];

let buttonStates = [0, 0, 0];

// Request input mode
for (let i = 0; i < buttons.length; i++) {
  buttons[i].requestInputMode();
}

// Poll values every 100 ms
setInterval(() => {
  for (let i = 0; i < buttons.length; i++) {
    const value = buttons[i].getValue();
    if (buttonStates[i] !== value) {
      buttonStates[i] = value;
      console.log(value);
    }
  }
}, 100);

function cleanup() {
  for (let i = 0; i < buttons.length; i++) {
    buttons[i].release();
  }
  chip.close();
}

process.on('SIGINT', () => {
  cleanup();
  console.log('Clean exit');
  process.exit();
});
