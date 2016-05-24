package se.unlogic.eagledns;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;


public class UDPSocketMonitor extends Thread {

	private Logger log = Logger.getLogger(this.getClass());

	private final EagleDNS eagleDNS;
	private final InetAddress addr;
	private final int port;
	private static final short udpLength = 512;
	private final DatagramSocket socket;

	public UDPSocketMonitor(EagleDNS eagleDNS, final InetAddress addr, final int port) throws SocketException {
		super();
		this.eagleDNS = eagleDNS;
		this.addr = addr;
		this.port = port;

		socket = new DatagramSocket(port, addr);

		this.setDaemon(true);
		this.start();
	}

	@Override
	public void run() {

		log.debug("Starting UDP socket monitor on address " + this.getAddressAndPort());

		while (!this.eagleDNS.isShutdown()) {

			try {

				byte[] in = new byte[udpLength];
				DatagramPacket indp = new DatagramPacket(in, in.length);

				indp.setLength(in.length);
				socket.receive(indp);

				log.debug("UDP connection from " + indp.getSocketAddress());

				if(!this.eagleDNS.isShutdown()){

					this.eagleDNS.getUdpThreadPool().execute(new UDPConnection(eagleDNS, socket, indp));
				}


			} catch (SocketException e) {

				//This is usally thrown on shutdown
				log.debug("SocketException thrown from UDP socket on address " + this.getAddressAndPort() + ", " + e);

			} catch (IOException e) {

				log.info("IOException thrown by UDP socket on address " + this.getAddressAndPort() + ", " + e);
			}
		}

		log.debug("UDP socket monitor on address " + getAddressAndPort() + " shutdown");
	}

	public void closeSocket() throws IOException{

		log.debug("Closing TCP socket monitor on address " + getAddressAndPort() + "...");

		this.socket.close();
	}

	public String getAddressAndPort(){

		return addr.getHostAddress() + ":" + port;
	}
}
