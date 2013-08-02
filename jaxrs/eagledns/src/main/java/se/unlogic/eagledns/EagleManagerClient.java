package se.unlogic.eagledns;

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

		if(args.length != 3 || (!args[2].equals("reload") && !args[2].equals("shutdown"))){

			System.out.println("Usage EagleManagerClient config host command");
			System.out.println("Valid commands are: reload, shutdown");
			return;
		}

		XMLSettingNode configFile;

		try {
			configFile = new XMLSettingNode(args[0]);

		} catch (Exception e) {

			System.out.println("Unable to open config file " + args[0] + "!");
			return;
		}

		String password = configFile.getString("/Config/System/RemoteManagementPassword");

		if(password == null){

			System.out.println("No remote management password found in config!");
			return;
		}

		Integer port = configFile.getInteger("/Config/System/RemoteManagementPort");

		if(port == null){

			System.out.println("No remote management port found in config!");
			return;
		}

		try {
			EagleManager eagleManager = getManager(args[1], port, password);

			if(eagleManager == null){

				System.out.println("Invalid password!");

			}else{

				if(args[2].equals("reload")){

					eagleManager.reloadZones();
					System.out.println("Zones reloaded");

				}else{

					eagleManager.shutdown();
					System.out.println("Shutdown command sent");
				}
			}

		} catch (RemoteException e) {

			System.out.println("Unable to connect " + e);

		} catch (NotBoundException e) {

			System.out.println("Unable to connect " + e);
		}
	}
}
