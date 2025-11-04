import LedControll_Demo as controller
import ButtonControll as button_control
import asyncio
import sys


BUTTON_PINS = [24, 22, 27, 17]
CHIP_NUMBER = 0

async def main():

    buttonMonitor1 = button_control.ButtonMonitor(CHIP_NUMBER , BUTTON_PINS[0])
    buttonMonitor1.set_on_button_press(lambda: controller.turn_on(0))

    buttonMonitor2 = button_control.ButtonMonitor(CHIP_NUMBER , BUTTON_PINS[1])
    buttonMonitor2.set_on_button_press(lambda: controller.turn_on(1))

    buttonMonitor3 = button_control.ButtonMonitor(CHIP_NUMBER , BUTTON_PINS[2])
    buttonMonitor3.set_on_button_press(lambda: controller.turn_on(2))

    buttonMonitor4 = button_control.ButtonMonitor(CHIP_NUMBER , BUTTON_PINS[3])
    buttonMonitor4.set_on_button_press(lambda: controller.turn_on(3))


    while True:
        await asyncio.sleep(1)  # Wait before reconnecting
        controller.turn_off_all()


if __name__ == "__main__":


    try:
        # if you changed main signature to accept uri: asyncio.run(main(args.uri))
        asyncio.run(main())
    except KeyboardInterrupt:
        print("\nExiting (keyboard)...")
    finally:
        controller.cleanup()
        button_control.cleanup()
        sys.exit(0)
    print("\nExiting...")

    