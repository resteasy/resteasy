package se.unlogic.eagledns;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface EagleLogin extends Remote{

	EagleManager login(String password) throws RemoteException;
}
