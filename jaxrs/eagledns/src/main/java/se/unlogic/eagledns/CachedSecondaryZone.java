package se.unlogic.eagledns;

import org.apache.log4j.Logger;
import org.xbill.DNS.DClass;
import org.xbill.DNS.Record;
import org.xbill.DNS.Zone;
import org.xbill.DNS.ZoneTransferException;
import org.xbill.DNS.ZoneTransferIn;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;


public class CachedSecondaryZone {

	private Logger log = Logger.getLogger(this.getClass());
	protected ZoneProvider zoneProvider;
	private SecondaryZone secondaryZone;

	public CachedSecondaryZone(ZoneProvider zoneProvider, SecondaryZone secondaryZone) {

		this.zoneProvider = zoneProvider;
		this.secondaryZone = secondaryZone;
		//this.update();

		if(this.secondaryZone.getZoneCopy() != null){

			log.info("Using stored zone data for sedondary zone " + this.secondaryZone.getZoneName());
		}
	}


	public SecondaryZone getSecondaryZone() {

		return secondaryZone;
	}


	public void setSecondaryZone(SecondaryZone secondaryZone) {

		this.secondaryZone = secondaryZone;
	}

	/**
	 * Updates this secondary zone from the primary zone
	 * @param axfrTimeout
	 */
	public void update(int axfrTimeout) {


		try {
			ZoneTransferIn xfrin = ZoneTransferIn.newAXFR(this.secondaryZone.getZoneName(), this.secondaryZone.getRemoteServerAddress(), null);
			xfrin.setDClass(DClass.value(this.secondaryZone.getDclass()));
			xfrin.setTimeout(axfrTimeout);

			List<?> records = xfrin.run();

			if (!xfrin.isAXFR()) {

				log.warn("Unable to transfer zone " + this.secondaryZone.getZoneName() + " from server " + this.secondaryZone.getRemoteServerAddress() + ", response is not a valid AXFR!");

				return;
			}

			Zone axfrZone = new Zone(this.secondaryZone.getZoneName(),records.toArray(new Record[records.size()]));

			log.debug("Zone " + this.secondaryZone.getZoneName() + " successfully transfered from server " + this.secondaryZone.getRemoteServerAddress());

			if(!axfrZone.getSOA().getName().equals(this.secondaryZone.getZoneName())){

				log.warn("Invalid AXFR zone name in response when updating secondary zone " + this.secondaryZone.getZoneName() + ". Got zone name " + axfrZone.getSOA().getName() + " in respons.");
			}

			if(this.secondaryZone.getZoneCopy() == null || this.secondaryZone.getZoneCopy().getSOA().getSerial() != axfrZone.getSOA().getSerial()){

				this.secondaryZone.setZoneCopy(axfrZone);
				this.secondaryZone.setDownloaded(new Timestamp(System.currentTimeMillis()));
				this.zoneProvider.zoneUpdated(this.secondaryZone);

				log.info("Zone " + this.secondaryZone.getZoneName() + " successfully updated from server " + this.secondaryZone.getRemoteServerAddress());
			}else{

				log.info("Zone " + this.secondaryZone.getZoneName() + " is already up to date with serial " + axfrZone.getSOA().getSerial());
				this.zoneProvider.zoneChecked(secondaryZone);
			}

		} catch (IOException e) {

			log.warn("Unable to transfer zone " + this.secondaryZone.getZoneName() + " from server " + this.secondaryZone.getRemoteServerAddress() + ", " + e);

			checkExpired();

		} catch (ZoneTransferException e) {

			log.warn("Unable to transfer zone " + this.secondaryZone.getZoneName() + " from server " + this.secondaryZone.getRemoteServerAddress() + ", " + e);

			checkExpired();

		}catch (RuntimeException e) {

			log.warn("Unable to transfer zone " + this.secondaryZone.getZoneName() + " from server " + this.secondaryZone.getRemoteServerAddress() + ", " + e);

			checkExpired();

		}finally{

			this.secondaryZone.setDownloaded(new Timestamp(System.currentTimeMillis()));
		}
	}


	private void checkExpired() {

		if(this.secondaryZone.getZoneCopy() != null && (System.currentTimeMillis() - this.secondaryZone.getDownloaded().getTime()) > (this.secondaryZone.getZoneCopy().getSOA().getExpire() * 1000)){

			log.warn("AXFR copy of secondary zone " + secondaryZone.getZoneName() + " has expired, deleting zone data...");

			this.secondaryZone.setZoneCopy(null);
			this.secondaryZone.setDownloaded(null);
			this.zoneProvider.zoneUpdated(this.secondaryZone);
		}
	}
}
