package se.unlogic.eagledns;

import org.apache.log4j.Logger;
import se.unlogic.standardutils.settings.XMLSettingNode;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class EagleManagerClient {

	public static EagleManager getManager(String host, int port, String password) throws RemoteException, NotBoundException {

		Registry registry = LocateRegistry.getRegistry(host,port);

		EagleLogin eagleLogin = (EagleLogin) registry.lookup("eagleLogin");

		return eagleLogin.login(password);
	}



	public static void main(String[] args) {

		Logger LOG = Logger.getLogger(EagleManagerClient.class);

		if(args.length != 3 || (!args[2].equals("reload") && !args[2].equals("shutdown"))){

			LOG.info("Usage EagleManagerClient config host command");
			LOG.info("Valid commands are: reload, shutdown");
			return;
		}

		XMLSettingNode configFile;

		try {
			configFile = new XMLSettingNode(args[0]);

		} catch (Exception e) {

			LOG.info("Unable to open config file " + args[0] + "!");
			return;
		}

		String password = configFile.getString("/Config/System/RemoteManagementPassword");

		if(password == null){

			LOG.info("No remote management password found in config!");
			return;
		}

		Integer port = configFile.getInteger("/Config/System/RemoteManagementPort");

		if(port == null){

			LOG.info("No remote management port found in config!");
			return;
		}

		try {
			EagleManager eagleManager = getManager(args[1], port, password);

			if(eagleManager == null){

				LOG.info("Invalid password!");

			}else{

				if(args[2].equals("reload")){

					eagleManager.reloadZones();
					LOG.info("Zones reloaded");

				}else{

					eagleManager.shutdown();
					LOG.info("Shutdown command sent");
				}
			}

		} catch (RemoteException e) {

			LOG.info("Unable to connect " + e);

		} catch (NotBoundException e) {

			LOG.info("Unable to connect " + e);
		}
	}
}
