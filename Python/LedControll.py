import gpiod
import signal
import sys


"""
This code supports:
3 LEDS on pins 17, 27, 22 (PENDING)
3 LEDS on pins 5, 6, 13 (INCLUDED)
"""
# GPIO line offsets (pin numbers)
led_lines = [
    [17, 27, 22],  # PENDING
    [5, 6, 13],    # INCLUDED
]

led = [[None, None, None], [None, None, None]]
ledStates = [[0, 0, 0], [0, 0, 0]]

# Initialize GPIO lines
try:
    chip = gpiod.Chip('gpiochip0') 
    
    for i in range(2):
        for j in range(3):
            led[i][j] = chip.get_line(led_lines[i][j])
            led[i][j].request(consumer="led_controller", type=gpiod.LINE_REQ_DIR_OUT)
except Exception as e:
    print(f"Error initializing GPIO: {e}")
    sys.exit(1)


def turn_on(i, j):
    """Change LED State to ON"""
    if ledStates[i][j] != 1:
        try:
            led[i][j].set_value(1)
            ledStates[i][j] = 1
        except Exception as err:
            print(f"Error turning on LED: {err}")


def turn_off(i, j):
    """Change LED State to OFF"""
    if ledStates[i][j] != 0:
        try:
            led[i][j].set_value(0)
            ledStates[i][j] = 0
        except Exception as err:
            print(f"Error turning off LED: {err}")

def turn_off_all():
    for i in range(led.__len__()):
        for j in range(led[i].__len__()):
            turn_off(i, j)

def turn_on_all():
    for i in range(led.__len__()):
        for j in range(led[i].__len__()):
            turn_on(i, j)

def cleanup(signum=None, frame=None):
    print("Cleaning up GPIO...")
    for i in range(len(led)):
        for j in range(len(led[i])):
            if led[i][j] is not None:
                try:
                    led[i][j].set_value(0)  # Turn off LED
                    led[i][j].release()
                except Exception as e:
                    print(f"Error cleaning up LED [{i}][{j}]: {e}")
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