const controller = require('./output-hardware-controller');
const { getDCRAvailableEvents } = require('./API-reader');
const EventStates = require('./constants');

function wait(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

let stateList = [EventStates.STATE_NOT_PENDING_EXCLUDED, EventStates.STATE_NOT_PENDING_EXCLUDED, EventStates.STATE_NOT_PENDING_EXCLUDED];

async function update() {
    getDCRAvailableEvents("p-1-1").then((data) => {

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
        controller.setState(i, stateList[i]);
        }

    }); 
}

while (true) {
    update();
    await wait(5000);
}





