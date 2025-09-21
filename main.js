const controller = require('./output-hardware-controller');
const { getDCRAvailableEvents } = require('./API-reader');
const EventStates = require('./constants');


const PORT = 8080;

getDCRAvailableEvents("p-1-1");

controller.setState(0, EventStates.STATE_PENDING_INCLUDED);

