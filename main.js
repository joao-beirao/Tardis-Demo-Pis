const { getDCRAvailableEvents } = require('./API-reader');
const EventStates = require('./constants');
const { setOnButtonPress } = require('./input-hardware-controller');
const { executeConsume } = require('./EventController');
const { Chip, Line } = require('node-libgpiod');

const chip = new Chip(4);

async function wait(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

let stateList = [EventStates.STATE_NOT_PENDING_EXCLUDED, EventStates.STATE_NOT_PENDING_EXCLUDED, EventStates.STATE_NOT_PENDING_EXCLUDED];

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
  if (ledStates[i][j] !== 1) {
    led[i][j].setValue(1);
    ledStates[i][j] = 1;
  }
}

// * Change Led State
function turnOff(i, j) {
  if (ledStates[i][j] !== 0) {
    led[i][j].setValue(0);
    ledStates[i][j] = 0;
  }
}

// * Set state based on EventStates
async function setState(index, state) {
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


async function update() {
    await getDCRAvailableEvents("p-1-1").then( async (data) => {

        console.log(data);

        data.forEach((event, i) => {
            if (event[0] && event[1]) {
                stateList[i] = EventStates.STATE_PENDING_INCLUDED;
            } else if (event[0] && !event[1]) {
                stateList[i] = EventStates.STATE_NOT_PENDING_INCLUDED;
            } else if (!event[0] && event[1]) {
                stateList[i] = EventStates.STATE_PENDING_EXCLUDED;
            } else {
                stateList[i] = EventStates.STATE_NOT_PENDING_EXCLUDED;
            }
        });

        for (let i = 0; i < 3; i++) {
          await setState(i, stateList[i]);
        }

    }); 
}

async function main() {
    setOnButtonPress(() => {executeConsume();});
    while (true) {
        await update();
        await wait(1000);
    }
}

main();




