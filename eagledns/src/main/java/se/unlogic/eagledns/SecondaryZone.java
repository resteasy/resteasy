package se.unlogic.eagledns;

import org.xbill.DNS.Name;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Zone;

import java.sql.Timestamp;

/**
 * This class is used to hold data secondary zones when they are loaded from ZoneProviders. The actual Zone field may be left blank if the ZoneProvider has no previously stored copy of the zoneBackup.
 * 
 * @author Robert "Unlogic" Olofsson
 * 
 */
public class SecondaryZone {

	private Name zoneName;
	private String remoteServerAddress;
	private String dclass;
	private Timestamp downloaded;
	private Zone zoneCopy;

	public SecondaryZone(String zoneName, String remoteServerAddress, String dclass) throws TextParseException {

		super();
		this.zoneName = Name.fromString(zoneName, Name.root);
		this.remoteServerAddress = remoteServerAddress;
		this.dclass = dclass;
	}

	public SecondaryZone(String zoneName, String remoteServerAddress, String dclass, Timestamp zoneDownloaded, Zone zone) throws TextParseException {

		this.zoneName = Name.fromString(zoneName, Name.root);
		this.remoteServerAddress = remoteServerAddress;
		this.dclass = dclass;
		this.zoneCopy = zone;
		this.downloaded = zoneDownloaded;
	}

	public Name getZoneName() {

		return zoneName;
	}

	public void setZoneName(Name zoneName) {

		this.zoneName = zoneName;
	}

	public String getRemoteServerAddress() {

		return remoteServerAddress;
	}

	public void setRemoteServerAddress(String remoteServerIP) {

		this.remoteServerAddress = remoteServerIP;
	}

	public Zone getZoneCopy() {

		return zoneCopy;
	}

	public void setZoneCopy(Zone zone) {

		this.zoneCopy = zone;
	}

	public String getDclass() {
		return dclass;
	}

	public void setDclass(String dclass) {
		this.dclass = dclass;
	}

	
	public Timestamp getDownloaded() {
	
		return downloaded;
	}

	
	public void setDownloaded(Timestamp zoneDownloaded) {
	
		this.downloaded = zoneDownloaded;
	}
}
