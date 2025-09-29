const controller = require('./output-hardware-controller');
const turnOff = require('./turn-off-all');

async function wait(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

async function main(){
  let i = 0;
  let j = 0;
  while (true) {
    turnOff();
    controller.turnOn(i, j);
    i = (i + 1) % 2;
    if (i === 0) {
      j = (j + 1) % 2;
    }
    await wait(1000);
  }
}

main();

