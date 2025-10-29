## File Locations
Inside a new terminal, files for running the Pi correctly should be found by entering:
```bash
cd Documentos/Tardis/Tardis-Demo-Pis
```
By doing so, two sub-folders are then available:
- Hardware 
- Runtime

### Runtime
In order to run the Tardis Runtime application, one should change the current directory and run the runtime command:
```bash
cd Runtime
```
Example command for `p-1-1`
```
java -jar target/babel-backend.jar interface=wlan0 role=P id=1 cid=1
```

### Hardware Controll
To run the Hardware controll software, change the directory into `Hardware/Python` and run `python3 Main_Demo.py`

```bash
cd Hardware/Python/
```
```bash
python3 Main_Demo.py
```

## Hardware Setup
![Raspberry Map](./Hardware/Raspberry_Pi_5_GPIO-Pinout.png)
Acording to the image, the software maps the LED pins to GPIO pins `6, 13, 19, 26`. All the LEDs must be connected to a built in GND pin.

The Push buttons on the other hand, are assigned to GPIO Pins `24, 17, 27, 22`, and should be connected to a 3.3V Pin.



## Session information
In order to properly connect to the raspberry remotely, an SSH connection must be established like so:
```
sudo ssh pi-1@p-3-1
```
