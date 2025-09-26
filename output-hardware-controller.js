const { Chip, Line } = require('node-libgpiod');
const EventStates = require('./constants');
const chip = new Chip(4);

/*
*  This code supports 
*  3 LEDS on pins 17, 27, 22
*  3 LEDS on pins 5, 6, 13
*/
const led = [
  [new Line(chip, 17), new Line(chip, 27), new Line(chip, 22)], //PENDING
  [new Line(chip, 5), new Line(chip, 6), new Line(chip, 13)],   //INCLUDED
];

let ledStates = [
  [0, 0, 0],
  [0, 0, 0],
];

for (let i = 0; i < led.length; i++) {
  for (let j = 0; j < led[i].length; j++) {
    led[i][j].requestOutputMode();
  }
}


// * Change Led State
function turnOn(i, j) {
  if (ledStates[i][j] != 1) {
    led[i][j].setValue(1);
    ledStates[i][j] = 1;
  }
}

// * Change Led State
function turnOff(i, j) {
  if (ledStates[i][j] != 0) {
    led[i][j].setValue(0);
    ledStates[i][j] = 0;
  }
}

// * Set state based on EventStates
function setState(index, state) {
  switch (state) {
      case EventStates.STATE_NOT_PENDING_INCLUDED:
          turnOff(0, index);
          turnOn(1, index);
          break;
      case EventStates.STATE_NOT_PENDING_EXCLUDED:
          turnOff(0, index);
          turnOff(1, index);
          break;
      case EventStates.STATE_PENDING_INCLUDED:
          turnOn(0, index);
          turnOn(1, index);
          break;
      case EventStates.STATE_PENDING_EXCLUDED:
          turnOn(0, index);
          turnOff(1, index);
          break;
      default:
          console.log('Unknown state');
  }
}


// * Cleanup on exit
function cleanup() {
  for (let i = 0; i < led.length; i++) {
    for (let j = 0; j < led[i].length; j++) {
      turnOff(i, j);
      led[i][j].release();
    }
  }
}


module.exports = { cleanup, setState, turnOff, turnOn };