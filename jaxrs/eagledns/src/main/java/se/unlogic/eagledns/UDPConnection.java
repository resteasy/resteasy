package se.unlogic.eagledns;

import org.apache.log4j.Logger;
import org.xbill.DNS.Message;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;


public class UDPConnection implements Runnable {

	private static final Logger log = Logger.getLogger(UDPConnection.class);

	private final EagleDNS eagleDNS;
	private final DatagramSocket socket;
	private final DatagramPacket inDataPacket;

	public UDPConnection(EagleDNS eagleDNS, DatagramSocket socket, DatagramPacket inDataPacket) {
		super();
		this.eagleDNS = eagleDNS;
		this.socket = socket;
		this.inDataPacket = inDataPacket;
	}

	public void run() {

		try{

			byte[] response = null;

			try {
				Message query = new Message(inDataPacket.getData());

				log.debug("UDP query " + EagleDNS.toString(query.getQuestion()) + " from " + inDataPacket.getSocketAddress());

				response = this.eagleDNS.generateReply(query, inDataPacket.getData(), inDataPacket.getLength(), null);

				if (response == null) {
					return;
				}
			} catch (IOException e) {
				response = this.eagleDNS.formerrMessage(inDataPacket.getData());
			}

			DatagramPacket outdp = new DatagramPacket(response, response.length, inDataPacket.getAddress(), inDataPacket.getPort());

			outdp.setData(response);
			outdp.setLength(response.length);
			outdp.setAddress(inDataPacket.getAddress());
			outdp.setPort(inDataPacket.getPort());

			try {
				socket.send(outdp);

			} catch (IOException e) {

				log.debug("Error sending UDP response to " + inDataPacket.getAddress() + ", " + e);
			}

		}catch(Throwable e){

			log.warn("Error processing UDP connection from " + inDataPacket.getSocketAddress() + ", " + e);
		}
	}
}
