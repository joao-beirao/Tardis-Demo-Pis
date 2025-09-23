const { Chip, Line } = require('node-libgpiod');
const chip = new Chip(0);

/*
 * Supports 3 buttons on pins 21, 20, 16
 * Adjust BCM numbers to match your wiring
 */
const buttons = [
  new Line(chip, 21),
  new Line(chip, 20),
  new Line(chip, 16),
];

// Store current button states
// 0 = released, 1 = pressed
let buttonStates = [0, 0, 0];

// Setup input mode + event listeners
for (let i = 0; i < buttons.length; i++) {
  buttons[i].requestBothEdges(); // detect both rising and falling
  buttons[i].on('change', (value) => {
    buttonStates[i] = value;
    console.log(`Button ${i} changed: ${value ? 'Pressed' : 'Released'}`);
  });
}

// * Read current value of a button
function getButton(i) {
  return buttons[i].getValue();
}

// * Cleanup on exit
function cleanup() {
  for (let i = 0; i < buttons.length; i++) {
    buttons[i].release();
  }
  chip.close();
}

module.exports = { getButton, buttonStates, cleanup };
