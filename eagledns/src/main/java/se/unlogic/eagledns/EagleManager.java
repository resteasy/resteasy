package se.unlogic.eagledns;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface EagleManager extends Remote{

	void shutdown() throws RemoteException;

	void reloadZones() throws RemoteException;

}