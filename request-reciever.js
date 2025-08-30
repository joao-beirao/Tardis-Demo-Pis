const controller = require('./controller');

const pcap = require('pcap');
const session = pcap.createSession('wlan0', 'tcp port 9000');

function filterPacket(packet) {
  const ip = packet.payload.payload;
  if(ip != null && ip != undefined) {
    if (ip.saddr && ip.daddr) {
      const tcp = ip.payload;
      if (tcp && tcp.data) {
        if (tcp.data.length > 92) {
          return true;
        }
      }
    }
  }
  return false;
}






session.on('packet', (rawPacket) => {
  const packet = pcap.decode.packet(rawPacket);

  // Example: Extract and log only source/destination IP and TCP payload length
  if (filterPacket(packet)) {
    const ip = packet.payload.payload;
    const tcp = ip.payload;
    console.log(`From ${ip.saddr.addr.join('.')} to ${ip.daddr.addr.join('.')}, payload length: ${tcp.data.length}`);
  }

});