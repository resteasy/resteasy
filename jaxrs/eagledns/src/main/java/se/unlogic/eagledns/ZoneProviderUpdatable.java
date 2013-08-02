package se.unlogic.eagledns;


/**
 * Interface that tells Eagle DNS that a {@link ZoneProvider} can trigger a zone reload.
 * 
 * @author Robert "Unlogic" Olofsson (unlogic@unlogic.se)
 * @author Michael Neale, Red Hat (JBoss division)
 * 
 */
public interface ZoneProviderUpdatable {

	/**
	 * This method is automatically called by Eagle DNS when the {@link ZoneProvider} has been instantiated, before the {@link ZoneProvider#init(String) init()} method is called.
	 * 
	 * @see ZoneChangeCallback
	 * 
	 * @param zoneChangeCallback Callback handle
	 */
	void setChangeListener(ZoneChangeCallback zoneChangeCallback);

}
