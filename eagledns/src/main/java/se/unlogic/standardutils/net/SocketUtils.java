/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

public class SocketUtils {

	public static Socket getSocket(String host, int port, int timeout) throws IOException{
		SocketAddress sockaddr = new InetSocketAddress(host, port);
		Socket socket = new Socket();
		socket.connect(sockaddr, timeout);

		return socket;
	}

	public static boolean isValidInetAddress(String address){

		try {

			InetAddress.getByName(address);

			return true;
			
		} catch (UnknownHostException e) {

			return false;
		}
	}

	public static boolean isPortAvailable(int port) {  

		try {  
			ServerSocket srv = new ServerSocket(port);  
			srv.close();  
			srv = null;
			return true;  
		} catch (IOException e) {  
			return false;  
		}  
	}

	public static void closeSocket(Socket socket) {

		if(socket != null){
			
			try {
				socket.close();
			} catch (IOException e) {}
		}
	}  
}
