package se.unlogic.standardutils.rmi;

import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;


public class RMIUtils {

	public static String getClientHost(){
		
		try {
			return UnicastRemoteObject.getClientHost();
		} catch (ServerNotActiveException e) {
			throw new RuntimeException(e);
		}
	}
}
