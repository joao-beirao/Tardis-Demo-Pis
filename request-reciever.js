const controller = require('./controller');

const pcap = require('pcap');
const session = pcap.createSession('wlan0', 'tcp port 9000');

function filterPacket(packet) {
  const ip = packet.payload.payload;
  if (ip && ip.saddr && ip.daddr) {
    const tcp = ip.payload;
    if (tcp && tcp.data) {
      return {
        src: ip.saddr.addr.join('.'),
        dst: ip.daddr.addr.join('.'),
        payloadLength: tcp.data.length
      };
    }
  }
  return null;
}






session.on('packet', (rawPacket) => {
  const packet = pcap.decode.packet(rawPacket);

  // Example: Extract and log only source/destination IP and TCP payload length
  
  const ip = packet.payload.payload;
  if(ip != null && ip != undefined) {
    const tcp = ip.payload;

    if (ip && tcp && ip.saddr && ip.daddr && tcp.data) {
      if(tcp.data.length > 92) {
      console.log(`From ${ip.saddr.addr.join('.')} to ${ip.daddr.addr.join('.')}, payload length: ${tcp.data ? tcp.data.length : 0}`);
      }
    }
  }
});