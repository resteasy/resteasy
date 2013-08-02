package se.unlogic.eagledns;

/**
 * Interface that enables {@link ZoneProvider}'s to reload the zone cache in Eagle DNS without using the remote management interface ({@link EagleManager}).
 * 
 * @author Robert "Unlogic" Olofsson (unlogic@unlogic.se)
 * @author Michael Neale, Red Hat (JBoss division)
 * 
 */
public interface ZoneChangeCallback {

	/**
	 * Calling this method causes Eagle DNS to reload all it's zone from the registered zone providers
	 */
	void zoneDataChanged();

}
