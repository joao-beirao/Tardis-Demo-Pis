import asyncio
import signal
import sys
import LedControll as controller

async def wait(ms):
    """Wait for specified milliseconds"""
    await asyncio.sleep(ms / 1000)

async def main():
    i = 0
    j = 0
    while True:
        print(f"Turning on LED at ({i}, {j})")
        
        controller.turn_on(i, j)
        await wait(1000)
        controller.turn_off(i, j)
        
        # Update indices
        j = (j + 1) % 3
        if j == 0:
            i = (i + 1) % 2
        
def cleanup(signum=None, frame=None):
    """Cleanup on exit"""
    print("Clean exit.")
    controller.cleanup()
    sys.exit(0)

# Register signal handlers
signal.signal(signal.SIGINT, cleanup)

if __name__ == "__main__":
    try:
        # Run the async main function
        asyncio.run(main())
    except KeyboardInterrupt:
        cleanup()