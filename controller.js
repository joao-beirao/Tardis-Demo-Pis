const { Chip, Line } = require('node-libgpiod');

const chip = new Chip(0);

// 3 LEDS on pins 17, 27, 22
const led = [new Line(chip, 17), new Line(chip, 27), new Line(chip, 22)];  // GPIO17, GPIO27, GPIO22 (BCM numbering)
for (let i = 0; i < led.length; i++) {
    led[i].requestOutputMode();
}

function setLed(index, value) {
    if(value){
        led[index].setValue(1);
    }else{
        led[index].setValue(0);
    }
}

function cleanup() {
  for (let i = 0; i < led.length; i++) {
    setLed(i, 0);
    led[i].release();
  }
  chip.close();
}

module.exports = { setLed, cleanup };