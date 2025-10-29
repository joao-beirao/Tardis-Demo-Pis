import gpiod
import signal
import sys


"""
This code supports:
4 LEDS on pins 4, 17, 27, 22
"""
# GPIO line offsets (pin numbers)
led_lines = [ 6,13,19,26]

led = [None, None, None, None]
ledStates = [0, 0, 0, 0]

# Initialize GPIO lines
try:
    chip = gpiod.Chip('gpiochip0') 

    for i in range(4):
        led[i] = chip.get_line(led_lines[i])
        led[i].request(consumer="led_controller", type=gpiod.LINE_REQ_DIR_OUT)

except Exception as e:
    print(f"Error initializing GPIO: {e}")
    sys.exit(1)


def turn_on(i):
    """Change LED State to ON"""
    if ledStates[i] != 1:
        try:
            led[i].set_value(1)
            ledStates[i] = 1
        except Exception as err:
            print(f"Error turning on LED: {err}")


def turn_off(i ):
    """Change LED State to OFF"""
    if ledStates[i] != 0:
        try:
            led[i].set_value(0)
            ledStates[i] = 0
        except Exception as err:
            print(f"Error turning off LED: {err}")

def turn_off_all():
    for i in range(led.__len__()):
        turn_off(i)

def turn_on_all():
    for i in range(led.__len__()):
        turn_on(i)

def cleanup(signum=None, frame=None):
    print("Cleaning up GPIO...")
    for i in range(len(led)):
        if led[i] is not None:
            try:
                led[i].set_value(0)  # Turn off LED
                led[i].release()
            except Exception as e:
                print(f"Error cleaning up LED [{i}]: {e}")
    if 'chip' in locals():
        chip.close()
    if signum:
        sys.exit(0)

# Register cleanup handlers
signal.signal(signal.SIGINT, cleanup)
signal.signal(signal.SIGTERM, cleanup)

if __name__ == "__main__":
    print("LED Controller initialized")
    # Keep the script running
    try:
        signal.pause()
    except KeyboardInterrupt:
        cleanup()