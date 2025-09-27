const { getDCRAvailableEvents } = require('./API-reader');
const EventStates = require('./constants');
const { setOnButtonPress } = require('./input-hardware-controller');
const { executeConsume } = require('./EventController');
const output = require('./output-hardware-controller');

const stateList = [EventStates.STATE_NOT_PENDING_EXCLUDED, EventStates.STATE_NOT_PENDING_EXCLUDED, EventStates.STATE_NOT_PENDING_EXCLUDED];

async function wait(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}


async function updateStates() {
    await getDCRAvailableEvents("p-1-1").then( (data) => {


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
    }).catch((err) => {
        console.error('Error:', err);
    });

}

function updateLEDs(){

  
  for (let i = 0; i < 3; i++) {
    const idx = parseInt(i, 10);
    if (stateList[idx] === EventStates.STATE_NOT_PENDING_INCLUDED) {
      output.turnOff(0, idx);
      output.turnOn(1, idx);
    } else if (stateList[idx] === EventStates.STATE_NOT_PENDING_EXCLUDED) {
      output.turnOff(0, idx);
      output.turnOff(1, idx);
    } else if (stateList[idx] === EventStates.STATE_PENDING_INCLUDED) {
      output.turnOn(0, idx);
      output.turnOn(1, idx);
    } else if (stateList[idx] === EventStates.STATE_PENDING_EXCLUDED) {
      output.turnOn(0, idx);
      output.turnOff(1, idx);
    } else {
      console.log('Unknown state');
    }
  }
}

async function main() {
    setOnButtonPress(() => {executeConsume();});
    while (true) {
        updateLEDs();
        await updateStates();
        await wait(1000);
    }
}

main();


process.on('exit', (code) => {
    console.log(`About to exit with code: ${code}`);
    output.cleanup();
});



