const { Chip, Line } = require('node-libgpiod');

const chip = new Chip(0);

/*
*  This code supports 
*  3 LEDS on pins 17, 27, 22
*  3 LEDS on pins 5, 6, 13
*/
const led = [
  [new Line(chip, 17), new Line(chip, 27), new Line(chip, 22)], //PENDING
  [new Line(chip, 5), new Line(chip, 6), new Line(chip, 13)],   //INCLUDED
];  // GPIO17, GPIO27, GPIO22 (BCM numbering)
for (let i = 0; i < led.length; i++) {
    led[i].requestOutputMode();
}


// * Change Led State
function setLed(index, value) {
  led[index].setValue(value);
}

function setState(index, state) {
  switch (state) {
      case STATE_NOT_PENDING_INCLUDED:
          led[0][index].setValue(0);
          led[1][index].setValue(1);
          break;
      case STATE_NOT_PENDING_EXCLUDED:
          led[0][index].setValue(0);
          led[1][index].setValue(0);
          break;
      case STATE_PENDING_INCLUDED:
          led[0][index].setValue(1);
          led[1][index].setValue(1);            
          break;
      case STATE_PENDING_EXCLUDED:
          led[0][index].setValue(1);
          led[1][index].setValue(0);
          break;
      default:
          console.log('Unknown state');
  }
}



// !! Not Working !?
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


// * Cleanup on exit
function cleanup() {
  for (let i = 0; i < led.length; i++) {
    setLed(i, 0);
    led[i].release();
  }
}


module.exports = { setLed, cleanup , blinkLed, setState };