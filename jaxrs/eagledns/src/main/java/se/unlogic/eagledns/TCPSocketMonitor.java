package se.unlogic.eagledns;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;


public class TCPSocketMonitor extends Thread {

	private Logger log = Logger.getLogger(this.getClass());

	private final EagleDNS eagleDNS;
	private final InetAddress addr;
	private final int port;
	private final ServerSocket serverSocket;

	public TCPSocketMonitor(EagleDNS eagleDNS, final InetAddress addr, final int port) throws IOException {
		super();
		this.eagleDNS = eagleDNS;
		this.addr = addr;
		this.port = port;

		serverSocket = new ServerSocket(port, 128, addr);

		this.setDaemon(true);
		this.start();
	}

	@Override
	public void run() {

		log.debug("Starting TCP socket monitor on address " + getAddressAndPort());

		while (!this.eagleDNS.isShutdown()) {

			try {

				final Socket socket = serverSocket.accept();

				log.debug("TCP connection from " + socket.getRemoteSocketAddress());

				this.eagleDNS.getTcpThreadPool().execute(new TCPConnection(eagleDNS, socket));

			} catch (SocketException e) {

				//This is usally thrown on shutdown
				log.debug("SocketException thrown from TCP socket on address " + getAddressAndPort() + ", " + e);

			} catch (IOException e) {

				log.info("IOException thrown by TCP socket on address " + getAddressAndPort() + ", " + e);
			}
		}

		log.debug("TCP socket monitor on address " + getAddressAndPort() + " shutdown");
	}


	public InetAddress getAddr() {

		return addr;
	}


	public int getPort() {

		return port;
	}


	public ServerSocket getServerSocket() {

		return serverSocket;
	}

	public void closeSocket() throws IOException{

		log.debug("Closing TCP socket monitor on address " + getAddressAndPort() + "...");

		this.serverSocket.close();
	}

	public String getAddressAndPort(){

		return addr.getHostAddress() + ":" + port;
	}
}
