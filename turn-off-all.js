const controller = require('./output-hardware-controller');
const EventStates = require('./constants');

for (let i = 0; i < 3; i++) {
     controller.setState(i, EventStates.STATE_NOT_PENDING_EXCLUDED);
    // controller.setState(i, EventStates.STATE_NOT_PENDING_INCLUDED);
}