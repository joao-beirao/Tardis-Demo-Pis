const WebSocket  = require('ws');
const controller = require('./controller');
const net = require('net');

const JAVA_APP_PORT = 9001;
const PROXY_PORT = 9000;
const JAVA_HOST = '127.0.0.1';

// Proxy server
const server = net.createServer((clientSocket) => {
  console.log("Client connected");

  // Connect to real app
  const serverSocket = net.connect(JAVA_APP_PORT, JAVA_HOST);

  // Forward client → server
  clientSocket.on('data', (data) => {
    console.log("Client → Server:", data.toString());

    //controller.blinkLed(0, 6, 100);

    serverSocket.write(data);
  });


  // Forward server → client
  serverSocket.on('data', (data) => {
    console.log("Server → Client:", data.toString());
    clientSocket.write(data);
  });

  clientSocket.on('end', () => {
    console.log("Client disconnected");
    serverSocket.end();
  });
});

server.listen(PROXY_PORT, () => {
  console.log(`Proxy running on port ${PROXY_PORT}, forwarding to ${JAVA_HOST}:${JAVA_APP_PORT}`);
});
