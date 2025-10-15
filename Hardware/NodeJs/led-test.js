const controller = require('./output-hardware-controller');

async function wait(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

async function main(){
  let i = 0;
  let j = 0;
  while (true) {
    console.log(`Turning on LED at (${i}, ${j})`);
    await controller.turnOn(0, 0);
    await wait(1000);
    await controller.turnOff(0, 0);

    j = (j + 1) % 3;
    if (j === 0) {
      i = (i + 1) % 2;
    }

  }
}

main();

process.on('SIGINT', () => {
  controller.cleanup();
  console.log("Clean exit.");
  process.exit();
});