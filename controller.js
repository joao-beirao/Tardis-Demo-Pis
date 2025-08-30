const { Chip, Line } = require('node-libgpiod');

const chip = new Chip(0);

// 3 LEDS on pins 17, 27, 22
const led = [new Line(chip, 17), new Line(chip, 27), new Line(chip, 22)];  // GPIO17, GPIO27, GPIO22 (BCM numbering)
for (let i = 0; i < led.length; i++) {
    led[i].requestOutputMode();
}

function setLed(index, value) {
  led[index].setValue(value);
}

function blinkLed(index, times, interval) {
    let count = 0;
    const blink = setInterval(() => {
        setLed(index, count % 2);
        count++;
        if (count >= times * 2) {
            clearInterval(blink);
            setLed(index, 0);
        }
    }, interval);
}

function cleanup() {
  for (let i = 0; i < led.length; i++) {
    setLed(i, 0);
    led[i].release();
  }
}

module.exports = { setLed, cleanup , blinkLed };