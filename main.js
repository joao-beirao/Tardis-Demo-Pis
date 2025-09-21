const controller = require('./output-hardware-controller');
const { getDCRAvailableEvents } = require('./API-reader');
const EventStates = require('./constants');


const PORT = 8080;

getDCRAvailableEvents("p-1-1");

for (let i = 0; i < 3; i++) {
     controller.setState(i, EventStates.STATE_PENDING_INCLUDED);
    // controller.setState(i, EventStates.STATE_NOT_PENDING_INCLUDED);
}

