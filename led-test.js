const controller = require('./output-hardware-controller');

async function wait(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

async function main(){
  let i = 0;
  let j = 0;
  while (true) {
    await controller.turnOff(i, j);
    i = (i + 1) % 2;
    if (i === 0) {
      j = (j + 1) % 3;
    }
    console.log(`Turning on LED at (${i}, ${j})`);
    await controller.turnOn(i, j);
    await wait(1000);
  }
}

main();

process.on('SIGINT', () => {
  controller.cleanup();
  console.log("Clean exit.");
  process.exit();
});