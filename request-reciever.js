const controller = require('./controller');

const pcap = require('pcap');
const session = pcap.createSession('wlan0', 'tcp port 9000');

session.on('packet', (rawPacket) => {
  const packet = pcap.decode.packet(rawPacket);

  // Example: Extract and log only source/destination IP and TCP payload length
  
  const ip = packet.payload.payload;
  if(ip != null && ip != undefined) {
    const tcp = ip.payload;

    if (ip && tcp && ip.saddr && ip.daddr) {
      console.log(`From ${ip.saddr.addr.join('.')} to ${ip.daddr.addr.join('.')}, payload length: ${tcp.data ? tcp.data.length : 0}`);
    }
  }
});