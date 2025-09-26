const controller = require('./output-hardware-controller');
const EventStates = require('./constants');

for (let i = 0; i < 2; i++) {
    for (let j = 0; j < 3; j++) {
        controller.turnOn(i, j);
    }
}

    controller.cleanup();
