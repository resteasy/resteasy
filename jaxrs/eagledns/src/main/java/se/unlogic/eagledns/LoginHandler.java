package se.unlogic.eagledns;

import org.apache.log4j.Logger;

import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;


public class LoginHandler implements EagleLogin {

	private Logger log = Logger.getLogger(this.getClass());

	private EagleManager eagleManager;
	private String password;

	public LoginHandler(EagleManager eagleManager, String password) {
		super();
		this.eagleManager = eagleManager;
		this.password = password;
	}

	public EagleManager login(String password) {

		if(password != null && password.equalsIgnoreCase(this.password)){

			try {
				log.info("Remote login from " + UnicastRemoteObject.getClientHost());
			} catch (ServerNotActiveException e) {}

			return eagleManager;

		}

		try {
			log.warn("Failed login attempt from " + UnicastRemoteObject.getClientHost());
		} catch (ServerNotActiveException e) {}

		return null;
	}

}
