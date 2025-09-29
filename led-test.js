const controller = require('./output-hardware-controller');

async function wait(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

async function main(){
  let i = 0;
  let j = 0;
  while (true) {
    controller.turnOff(i, j);
    i = (i + 1) % 2;
    if (i === 0) {
      j = (j + 1) % 2;
    }
    controller.turnOn(i, j);
    await wait(1000);
  }
}

main();

