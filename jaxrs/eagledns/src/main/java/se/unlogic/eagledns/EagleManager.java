package se.unlogic.eagledns;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface EagleManager extends Remote{

	public void shutdown() throws RemoteException;

	public void reloadZones() throws RemoteException;

}