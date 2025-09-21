const controller = require('./output-hardware-controller');
const { getDCRAvailableEvents } = require('./API-reader');
const EventStates = require('./constants');

let stateList = [EventStates.STATE_NOT_PENDING_EXCLUDED, EventStates.STATE_NOT_PENDING_EXCLUDED, EventStates.STATE_NOT_PENDING_EXCLUDED];

let APIRRead = await getDCRAvailableEvents("p-1-1");
console.log(APIRRead);
/*
APIRRead.forEach((event, i) => {
    if (event[0] && event[1]) {
        stateList[i] = EventStates.STATE_PENDING_INCLUDED;
    } else if (event[0] && !event[1]) {
        stateList[i] = EventStates.STATE_PENDING_EXCLUDED;
    } else if (!event[0] && event[1]) {
        stateList[i] = EventStates.STATE_NOT_PENDING_INCLUDED;
    } else {
        stateList[i] = EventStates.STATE_NOT_PENDING_EXCLUDED;
    }
});
*/
for (let i = 0; i < 3; i++) {
     controller.setState(i, stateList[i]);
}

