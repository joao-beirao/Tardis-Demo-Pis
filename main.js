const { getDCRAvailableEvents } = require('./API-reader');
const EventStates = require('./constants');
const { setOnButtonPress } = require('./input-hardware-controller');
const { executeConsume } = require('./EventController');
const output = require('./output-hardware-controller');

let stateList = [EventStates.STATE_NOT_PENDING_EXCLUDED, EventStates.STATE_NOT_PENDING_EXCLUDED, EventStates.STATE_NOT_PENDING_EXCLUDED];

async function wait(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}


async function update() {
    await getDCRAvailableEvents("p-1-1").then( (data) => {

        console.log(data);

        data.forEach((event, i) => {
            if (event[0] && event[1]) {
              output.turnOff(0, i);
              output.turnOn(1, i);
            } else if (event[0] && !event[1]) {
                output.turnOff(0, i);
                output.turnOff(1, i);
            } else if (!event[0] && event[1]) {
                output.turnOn(0, i);
                output.turnOff(1, i);
            } else {
                output.turnOff(0, i);
                output.turnOff(1, i);
            }
        });


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




