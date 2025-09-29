const { getDCRAvailableEvents } = require('./API-reader');
const EventStates = require('./constants');
const { setOnButtonPress } = require('./input-hardware-controller');
const { executeConsume } = require('./EventController');
const output = require('./output-hardware-controller');

let stateList = [EventStates.STATE_NOT_PENDING_EXCLUDED, EventStates.STATE_NOT_PENDING_EXCLUDED, EventStates.STATE_NOT_PENDING_EXCLUDED];

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
  
    output.turnOn(0, 0);
    output.turnOff(1, 0);
  
  for (let i = 0; i < 3; i++) {
    console.log(`State of ${i}: ${stateList[i]}`);
    if (stateList[i] === EventStates.STATE_NOT_PENDING_INCLUDED) {
      output.led[0][i].setValue(0);
      output.led[1][i].setValue(1);
    } else if (stateList[i] === EventStates.STATE_NOT_PENDING_EXCLUDED) {
      output.led[0][i].setValue(0);
      output.led[1][i].setValue(0);
    } else if (stateList[i] === EventStates.STATE_PENDING_INCLUDED) {
      output.led[0][i].setValue(1);
      output.led[1][i].setValue(1);
    } else if (stateList[i] === EventStates.STATE_PENDING_EXCLUDED) {
      output.led[0][i].setValue(1);
      output.led[1][i].setValue(0);
    } else {
      console.log('Unknown state');
    }
  }
}

async function main() {
    setOnButtonPress(() => {executeConsume();});
    while (true) {
        updateLEDs();
        //await updateStates();
        await wait(1000);
    }
}

main();


process.on('exit', (code) => {
    console.log(`About to exit with code: ${code}`);
    output.cleanup();
});



