const { Chip, Line } = require('node-libgpiod');
const EventStates = require('./constants');
const chip = new Chip(0);

/*
*  This code supports 
*  3 LEDS on pins 17, 27, 22
*  3 LEDS on pins 5, 6, 13
*/
const led = [
  [new Line(chip, 17), new Line(chip, 27), new Line(chip, 22)], //PENDING
  [new Line(chip, 5), new Line(chip, 6), new Line(chip, 13)],   //INCLUDED
];  // GPIO17, GPIO27, GPIO22 (BCM numbering)
for (let i = 0; i < led.length; i++) {
  for (let j = 0; j < led[i].length; j++) {
    led[i][j].requestOutputMode();
  }
}


// * Change Led State
function turnOn(i, j) {
  if (led[i][j].getValue() != 1) {
    led[i][j].setValue(1);
  }
}

// * Change Led State
function turnOff(i, j) {
  if (led[i][j].getValue() != 0) {
    led[i][j].setValue(0);
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



// !! Not Working !?
function blinkLed(index, times, interval) {
    let count = 0;
    const blink = setInterval(() => {
        setLed(index, count % 2);
        count++;
        if (count >= times * 2) {
            clearInterval(blink);
            setLed(index, 0);
        }
    }, interval);
}


// * Cleanup on exit
function cleanup() {
  for (let i = 0; i < led.length; i++) {
    setLed(i, 0);
    led[i].release();
  }
}


module.exports = { cleanup , blinkLed, setState };