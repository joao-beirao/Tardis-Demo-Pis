const { Chip, Line } = require('node-libgpiod');
const chip = new Chip(0);

// Example pins for 3 buttons
const buttons = [
  new Line(chip, 21),
  new Line(chip, 20),
  new Line(chip, 16),
];

let buttonStates = [0, 0, 0];

for (let i = 0; i < buttons.length; i++) {
  // Request line as input with both edge detection
  buttons[i].requestInputMode({ edge: 'both' });

  // Subscribe to changes
  buttons[i].on('change', (value) => {
    buttonStates[i] = value;
    console.log(`Button ${i} changed: ${value ? 'Pressed' : 'Released'}`);
  });
}

function getButton(i) {
  return buttons[i].getValue();
}

function cleanup() {
  for (let i = 0; i < buttons.length; i++) {
    buttons[i].release();
  }
  chip.close();
}

module.exports = { getButton, buttonStates, cleanup };
