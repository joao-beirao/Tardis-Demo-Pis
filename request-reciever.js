const WebSocket  = require('ws');
const controller = require('./controller');
const net = require('net');

const JAVA_APP_PORT = 9000;
const PROXY_PORT = 9001;
const JAVA_HOST = '127.0.0.1';

// Proxy server
const server = net.createServer((clientSocket) => {
  console.log("Incoming connection to proxy");

  // Connect to Java app
  const javaSocket = net.connect(JAVA_APP_PORT, JAVA_HOST);

  // client -> java
  clientSocket.pipe(javaSocket);
  // java -> client
  javaSocket.pipe(clientSocket);

  // Error handling
  clientSocket.on('error', (err) => console.error("Client socket error:", err));
  javaSocket.on('error', (err) => console.error("Java socket error:", err));
});

server.listen(PROXY_PORT, () => {
  console.log(`Proxy listening on port ${PROXY_PORT}, forwarding to ${JAVA_HOST}:${JAVA_APP_PORT}`);
});