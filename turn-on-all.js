const controller = require('./output-hardware-controller');
const EventStates = require('./constants');

for (let i = 0; i < 3; i++) {
    for (let j = 0; j < 2; j++) {
        controller.turnOn(i, j);
    }
}

    controller.cleanup();
