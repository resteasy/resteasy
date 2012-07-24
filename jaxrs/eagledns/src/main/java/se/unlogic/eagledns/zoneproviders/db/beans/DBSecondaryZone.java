package se.unlogic.eagledns.zoneproviders.db.beans;

import org.xbill.DNS.TextParseException;
import se.unlogic.eagledns.SecondaryZone;


public class DBSecondaryZone extends SecondaryZone {

	private Integer zoneID;
	
	public DBSecondaryZone(Integer zoneID, String zoneName, String remoteServerAddress, String dclass) throws TextParseException {

		super(zoneName, remoteServerAddress, dclass);
		this.zoneID = zoneID;
	}

	
	public Integer getZoneID() {
	
		return zoneID;
	}
}
