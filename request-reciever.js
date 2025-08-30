const controller = require('./controller');

const pcap = require('pcap');
const session = pcap.createSession('eth0', 'tcp port 9000');

session.on('packet', (rawPacket) => {
  const packet = pcap.decode.packet(rawPacket);
  console.log("Got packet:", JSON.stringify(packet, null, 2));
});