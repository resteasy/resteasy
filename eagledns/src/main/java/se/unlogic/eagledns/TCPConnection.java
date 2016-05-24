package se.unlogic.eagledns;

import org.apache.log4j.Logger;
import org.xbill.DNS.Message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;


public class TCPConnection implements Runnable {

	private static Logger log = Logger.getLogger(TCPConnection.class);

	private EagleDNS eagleDNS;
	private Socket socket;

	public TCPConnection(EagleDNS eagleDNS, Socket socket) {
		super();
		this.eagleDNS = eagleDNS;
		this.socket = socket;
	}

	public void run() {

		try{

			try {
				int inLength;
				DataInputStream dataIn;
				DataOutputStream dataOut;
				byte[] in;

				InputStream is = socket.getInputStream();
				dataIn = new DataInputStream(is);
				inLength = dataIn.readUnsignedShort();
				in = new byte[inLength];
				dataIn.readFully(in);

				Message query;
				byte[] response = null;
				try {
					query = new Message(in);

					log.debug("TCP query " + EagleDNS.toString(query.getQuestion()) + " from " + socket.getRemoteSocketAddress());

					response = this.eagleDNS.generateReply(query, in, in.length, socket);

					if (response == null) {
						return;
					}
				} catch (IOException e) {
					response = this.eagleDNS.formerrMessage(in);
				}
				dataOut = new DataOutputStream(socket.getOutputStream());
				dataOut.writeShort(response.length);
				dataOut.write(response);
			} catch (IOException e) {

				log.debug("Error sending TCP response to " + socket.getRemoteSocketAddress() + ":" + socket.getPort() + ", " + e);

			} finally {
				try {
					socket.close();
				} catch (IOException e) {
				}
			}

		}catch(Throwable e){

			log.warn("Error processing TCP connection from " + socket.getRemoteSocketAddress() + ":" + socket.getPort() + ", " + e);
		}
	}
}
