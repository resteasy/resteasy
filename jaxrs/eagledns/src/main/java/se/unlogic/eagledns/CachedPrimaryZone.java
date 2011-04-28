package se.unlogic.eagledns;

import org.xbill.DNS.Zone;

public class CachedPrimaryZone {

	protected Zone zone;
	protected ZoneProvider zoneProvider;

	public CachedPrimaryZone(Zone zone, ZoneProvider zoneProvider) {

		super();
		this.zone = zone;
		this.zoneProvider = zoneProvider;
	}

	public Zone getZone() {

		return zone;
	}

	public void setZone(Zone zone) {

		this.zone = zone;
	}

	public ZoneProvider getZoneProvider() {

		return zoneProvider;
	}
}
