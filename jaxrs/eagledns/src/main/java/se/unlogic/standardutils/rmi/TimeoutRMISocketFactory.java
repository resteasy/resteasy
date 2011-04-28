package se.unlogic.standardutils.rmi;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.server.RMISocketFactory;

public class TimeoutRMISocketFactory extends RMISocketFactory{

	protected int readTimeout;
	protected int connectionTimeout;
	
	public TimeoutRMISocketFactory(int readTimeout, int connectionTimeout) {

		super();
		this.readTimeout = readTimeout;
		this.connectionTimeout = connectionTimeout;
	}	
	
	@Override
	public Socket createSocket(String host, int port) throws IOException {

		Socket socket = new Socket();
		socket.setSoTimeout(readTimeout);
		socket.setSoLinger(false, 0);
		socket.connect(new InetSocketAddress(host, port), connectionTimeout);
		return socket;
	}

	@Override
	public ServerSocket createServerSocket(int port) throws IOException {

		return new ServerSocket(port);
	}
}