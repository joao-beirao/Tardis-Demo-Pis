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

function blinkLed(index) {
    let TIMES = 3;
    let count = 0;
    let INTERVAL = 500;
    const blink = setInterval(() => {
        setLed(index, count % 2);
        count++;
        if (count >= 3 * 2) {
            clearInterval(blink);
            setLed(index, 0);
        }
    }, INTERVAL);
}

function cleanup() {
  for (let i = 0; i < led.length; i++) {
    setLed(i, 0);
    led[i].release();
  }
}

module.exports = { setLed, cleanup };