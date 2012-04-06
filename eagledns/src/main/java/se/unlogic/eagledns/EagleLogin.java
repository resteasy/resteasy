package se.unlogic.eagledns;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface EagleLogin extends Remote{

	public EagleManager login(String password) throws RemoteException;
}
