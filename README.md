
### Necessary packages:

``` 
sudo apt install gpiod libgpiod2 libgpiod-dev libnode-dev node-gyp build-essential
```

For **npm** :

``` bash
npm init -y
npm install node-libgpiod
npm install express
npm install onoff
npm install ws
```
---

### To Run

```
node pi-controller.js
```
which will proceed to open port 3000, reachable from: `http://pi-ip:3000`.


> **Ex:** [http://p-1-1:3000/on](http://p-1-1:3000/on).

