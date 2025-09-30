
### Necessary packages:

``` 
sudo apt install npm gpiod libgpiod2 libgpiod-dev libnode-dev node-gyp build-essential libpcap-dev
```

For **npm** :

``` bash
npm init -y
npm install node-libgpiod express onoff ws net pcap
```
---

### To Run

```
node pi-controller.js
```
which will proceed to open port 3000, reachable from: `http://pi-ip:3000`.


> **Ex:** [http://p-1-1:3000/on](http://p-1-1:3000/on).

