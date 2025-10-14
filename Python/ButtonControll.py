import gpiod
import time
import threading
from typing import Callable, Optional

# Chip and line configuration
CHIP_NUMBER = 0
BUTTON_PIN = 21
class ButtonMonitor:
    def __init__(self, chip_number: int = CHIP_NUMBER, button_pin: int = BUTTON_PIN):
        self.chip_number = chip_number
        self.button_pin = button_pin
        self.button_state = 0
        self.chip = None
        self.button_line = None
        self.monitoring = False
        self.monitor_thread = None
        self.callback = None
        
        self._initialize_gpio()
    
    def _initialize_gpio(self):
        try:
            self.chip = gpiod.Chip(f'gpiochip{self.chip_number}')
            
            # Get button line and request input mode
            self.button_line = self.chip.get_line(self.button_pin)
            self.button_line.request(consumer="button_monitor", type=gpiod.LINE_REQ_DIR_IN)
            
            # Read initial state
            self.button_state = self.button_line.get_value()
            print(f"Button monitor initialized on pin {self.button_pin}, initial state: {self.button_state}")
            
        except Exception as e:
            print(f"Error initializing GPIO: {e}")
            raise
    
    def set_on_button_press(self, callback: Callable):
        self.callback = callback
        if not self.monitoring:
            self.monitoring = True
            self.monitor_thread = threading.Thread(target=self._monitor_loop, daemon=True)
            self.monitor_thread.start()
    

    def _monitor_loop(self):
        print("Starting button monitoring...")
        while self.monitoring:
            try:
                value = self.button_line.get_value()

                if self.button_state != value:
                    print(f"Button state changed: {value}")
                    self.button_state = value
                    
                    if value != 1 and self.callback:
                        print("PRESSED - Calling callback")
                        self.callback()
                
                time.sleep(0.1)  # 100ms delay like the JS code
                
            except Exception as e:
                print(f"Error in monitor loop: {e}")
                break
    
    def cleanup(self):
        """Clean up GPIO resources"""
        print("Cleaning up button monitor...")
        self.monitoring = False
        
        if self.monitor_thread and self.monitor_thread.is_alive():
            self.monitor_thread.join(timeout=1.0)
        
        if self.button_line:
            try:
                self.button_line.release()
            except Exception as e:
                print(f"Error releasing button line: {e}")
        
        if self.chip:
            try:
                self.chip.close()
            except Exception as e:
                print(f"Error closing chip: {e}")

# Module-level functions for direct export (matching your JS module)
_button_monitor = None

def set_on_button_press(callback: Callable):
    """Module-level function matching your JS export"""
    global _button_monitor
    if _button_monitor is None:
        _button_monitor = ButtonMonitor()
    _button_monitor.set_on_button_press(callback)

def cleanup():
    """Module-level cleanup function"""
    global _button_monitor
    if _button_monitor:
        _button_monitor.cleanup()
        _button_monitor = None

# Example usage
if __name__ == "__main__":
    def button_pressed():
        print("🎯 Button pressed callback executed!")
    
    # Using the class-based approach
    monitor = ButtonMonitor()
    monitor.set_on_button_press(button_pressed)
    
    print("Button monitor running. Press Ctrl+C to exit.")
    
    try:
        # Keep the main thread alive
        while True:
            time.sleep(1)
    except KeyboardInterrupt:
        print("\nExiting...")
    finally:
        monitor.cleanup()
    
    # Alternative: using module functions
    # set_on_button_press(button_pressed)
    # try:
    #     while True:
    #         time.sleep(1)
    # except KeyboardInterrupt:
    #     cleanup()