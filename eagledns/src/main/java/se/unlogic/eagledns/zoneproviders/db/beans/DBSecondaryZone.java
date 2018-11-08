package se.unlogic.eagledns.zoneproviders.db.beans;

import org.xbill.DNS.TextParseException;
import se.unlogic.eagledns.SecondaryZone;


public class DBSecondaryZone extends SecondaryZone {

   private Integer zoneID;

   public DBSecondaryZone(final Integer zoneID, final String zoneName, final String remoteServerAddress, final String dclass) throws TextParseException {

      super(zoneName, remoteServerAddress, dclass);
      this.zoneID = zoneID;
   }


   public Integer getZoneID() {

      return zoneID;
   }
}
