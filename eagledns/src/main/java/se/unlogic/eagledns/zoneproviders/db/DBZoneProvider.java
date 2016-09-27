package se.unlogic.eagledns.zoneproviders.db;

import org.apache.log4j.Logger;
import org.xbill.DNS.Zone;
import se.unlogic.eagledns.SecondaryZone;
import se.unlogic.eagledns.ZoneProvider;
import se.unlogic.eagledns.zoneproviders.db.beans.DBRecord;
import se.unlogic.eagledns.zoneproviders.db.beans.DBSecondaryZone;
import se.unlogic.eagledns.zoneproviders.db.beans.DBZone;
import se.unlogic.standardutils.dao.AnnotatedDAO;
import se.unlogic.standardutils.dao.HighLevelQuery;
import se.unlogic.standardutils.dao.QueryParameterFactory;
import se.unlogic.standardutils.dao.SimpleAnnotatedDAOFactory;
import se.unlogic.standardutils.dao.SimpleDataSource;
import se.unlogic.standardutils.dao.TransactionHandler;
import se.unlogic.standardutils.reflection.ReflectionUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DBZoneProvider implements ZoneProvider {

	private static final Field RECORD_RELATION = ReflectionUtils.getField(DBZone.class, "records");

	private Logger log = Logger.getLogger(this.getClass());

	private String name;
	private String driver;
	private String url;
	private String username;
	private String password;

	private SimpleAnnotatedDAOFactory annotatedDAOFactory;
	private AnnotatedDAO<DBZone> zoneDAO;
	private AnnotatedDAO<DBRecord> recordDAO;
	private HighLevelQuery<DBZone> primaryZoneQuery;
	private HighLevelQuery<DBZone> secondaryZoneQuery;
	private QueryParameterFactory<DBZone, Integer> zoneIDQueryParameterFactory;
	private QueryParameterFactory<DBRecord, DBZone> recordZoneQueryParameterFactory;

	public void init(String name) throws ClassNotFoundException {
		this.name = name;

		DataSource dataSource;

		try {
			dataSource = new SimpleDataSource(driver, url, username, password);

		} catch (ClassNotFoundException e) {

			log.error("Unable to load JDBC driver " + driver, e);

			throw e;
		}

		this.annotatedDAOFactory = new SimpleAnnotatedDAOFactory();

		this.zoneDAO = new AnnotatedDAO<DBZone>(dataSource,DBZone.class, annotatedDAOFactory);
		this.recordDAO = new AnnotatedDAO<DBRecord>(dataSource,DBRecord.class, annotatedDAOFactory);

		QueryParameterFactory<DBZone, Boolean> zoneTypeParamFactory = zoneDAO.getParamFactory("secondary", boolean.class);

		this.primaryZoneQuery = new HighLevelQuery<DBZone>(zoneTypeParamFactory.getParameter(false),RECORD_RELATION);
		this.secondaryZoneQuery = new HighLevelQuery<DBZone>(zoneTypeParamFactory.getParameter(true),RECORD_RELATION);
		
		this.zoneIDQueryParameterFactory = zoneDAO.getParamFactory("zoneID", Integer.class);
		this.recordZoneQueryParameterFactory = recordDAO.getParamFactory("zone", DBZone.class);
	}

	public Collection<Zone> getPrimaryZones() {

		try {
			List<DBZone> dbZones = this.zoneDAO.getAll(primaryZoneQuery);

			if(dbZones != null){

				ArrayList<Zone> zones = new ArrayList<Zone>(dbZones.size());

				for(DBZone dbZone : dbZones){

					try {
						zones.add(dbZone.toZone());

					} catch (IOException e) {

						log.error("Unable to parse zone " + dbZone.getName(),e);
					}
				}

				return zones;
			}

		} catch (SQLException e) {

			log.error("Error getting primary zones from DB zone provider " + name,e);
		}

		return null;
	}

	public Collection<SecondaryZone> getSecondaryZones() {

		try {
			List<DBZone> dbZones = this.zoneDAO.getAll(this.secondaryZoneQuery);

			if(dbZones != null){

				ArrayList<SecondaryZone> zones = new ArrayList<SecondaryZone>(dbZones.size());

				for(DBZone dbZone : dbZones){

					try {
						DBSecondaryZone secondaryZone = new DBSecondaryZone(dbZone.getZoneID() ,dbZone.getName(), dbZone.getPrimaryDNS(), dbZone.getDclass());

						if(dbZone.getRecords() != null){
							secondaryZone.setZoneCopy(dbZone.toZone());
							secondaryZone.setDownloaded(dbZone.getDownloaded());
						}

						zones.add(secondaryZone);

					} catch (IOException e) {

						log.error("Unable to parse zone " + dbZone.getName(),e);
					}
				}

				return zones;
			}

		} catch (SQLException e) {

			log.error("Error getting secondary zones from DB zone provider " + name,e);
		}

		return null;
	}

	public void zoneUpdated(SecondaryZone zone) {

		if(!(zone instanceof DBSecondaryZone)){

			log.warn(zone.getClass() + " is not an instance of " + DBSecondaryZone.class + ", ignoring zone update");

			return;
		}

		Integer zoneID = ((DBSecondaryZone)zone).getZoneID();

		TransactionHandler transactionHandler = null;

		try {
			transactionHandler = zoneDAO.createTransaction();

			DBZone dbZone = this.zoneDAO.get(new HighLevelQuery<DBZone>(this.zoneIDQueryParameterFactory.getParameter(zoneID),(Field)null),transactionHandler);


			if(dbZone == null){

				log.warn("Unable to find secondary zone with zoneID " + zoneID + " in DB, ignoring zone update");

				return;
			}

			dbZone.parse(zone.getZoneCopy(), true);

			zoneDAO.update(dbZone,transactionHandler, null);

			recordDAO.delete(new HighLevelQuery<DBRecord>(recordZoneQueryParameterFactory.getParameter(dbZone),(Field)null), transactionHandler);

			if(dbZone.getRecords() != null){

				for(DBRecord dbRecord : dbZone.getRecords()){

					dbRecord.setZone(dbZone);

					this.recordDAO.add(dbRecord, transactionHandler, null);
				}
			}

			transactionHandler.commit();

			log.debug("Changes in seconday zone " + dbZone + " saved");

		} catch (SQLException e) {

			log.error("Unable to save changes in secondary zone " + zone.getZoneName(), e);
			TransactionHandler.autoClose(transactionHandler);
		}
	}

	public void zoneChecked(SecondaryZone zone) {

		if(!(zone instanceof DBSecondaryZone)){

			log.warn(zone.getClass() + " is not an instance of " + DBSecondaryZone.class + ", ignoring zone check");

			return;
		}

		Integer zoneID = ((DBSecondaryZone)zone).getZoneID();

		TransactionHandler transactionHandler = null;

		try {
			transactionHandler = zoneDAO.createTransaction();

			DBZone dbZone = this.zoneDAO.get(new HighLevelQuery<DBZone>(this.zoneIDQueryParameterFactory.getParameter(zoneID), (Field)null),transactionHandler);

			if(dbZone == null){

				log.warn("Unable to find secondary zone with zoneID " + zoneID + " in DB, ignoring zone update");

				return;
			}

			dbZone.parse(zone.getZoneCopy(), true);

			zoneDAO.update(dbZone,transactionHandler, null);

			transactionHandler.commit();

			log.debug("Changes in seconday zone " + dbZone + " saved");

		} catch (SQLException e) {

			log.error("Unable to save changes in secondary zone " + zone.getZoneName(), e);
			TransactionHandler.autoClose(transactionHandler);
		}
	}

	public void unload() {

		//Nothing to do here...
	}

	public void setDriver(String driver) {

		this.driver = driver;
	}

	public void setUsername(String username) {

		this.username = username;
	}

	public void setPassword(String password) {

		this.password = password;
	}

	public void setUrl(String url) {

		this.url = url;
	}
}
