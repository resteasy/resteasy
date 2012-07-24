package se.unlogic.eagledns.utils;

import org.xbill.DNS.Zone;
import se.unlogic.eagledns.zoneproviders.db.beans.DBRecord;
import se.unlogic.eagledns.zoneproviders.db.beans.DBZone;
import se.unlogic.eagledns.zoneproviders.file.FileZoneProvider;
import se.unlogic.standardutils.dao.AnnotatedDAO;
import se.unlogic.standardutils.dao.SimpleAnnotatedDAOFactory;
import se.unlogic.standardutils.dao.SimpleDataSource;
import se.unlogic.standardutils.dao.TransactionHandler;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collection;


public class PrimaryZones2DB {

	public static void main(String[] args) throws Throwable{

		if(args.length != 5){

			System.out.println("Usage: PrimaryZones2DB zonedir driver url username password");

		}else{

			importZones(args[0], args[1], args[2], args[3], args[4]);
		}
	}

	public static void importZones(String directory, String driver, String url, String username, String password) throws Throwable {

		FileZoneProvider fileZoneProvider = new FileZoneProvider();

		fileZoneProvider.setZoneFileDirectory(directory);

		Collection<Zone> zones = fileZoneProvider.getPrimaryZones();

		ArrayList<DBZone> dbZones = new ArrayList<DBZone>();

		for(Zone zone : zones){

			System.out.println("Converting zone " + zone.getSOA().getName().toString() + "...");

			dbZones.add(new DBZone(zone,false));
		}

		DataSource dataSource = new SimpleDataSource(driver, url, username, password);

		SimpleAnnotatedDAOFactory annotatedDAOFactory = new SimpleAnnotatedDAOFactory();
		AnnotatedDAO<DBZone> zoneDAO  = new AnnotatedDAO<DBZone>(dataSource,DBZone.class, annotatedDAOFactory);
		AnnotatedDAO<DBRecord> recordDAO  = new AnnotatedDAO<DBRecord>(dataSource,DBRecord.class, annotatedDAOFactory);

		TransactionHandler transactionHandler = zoneDAO.createTransaction();

		try{

			for(DBZone zone : dbZones){

				System.out.println("Storing zone " + zone + "...");

				zoneDAO.add(zone, transactionHandler, null);

				for(DBRecord dbRecord : zone.getRecords()){

					System.out.println("Storing record " + dbRecord + "...");

					dbRecord.setZone(zone);

					recordDAO.add(dbRecord, transactionHandler, null);
				}
			}

			transactionHandler.commit();

		}catch(Throwable e){

			transactionHandler.abort();
			
			throw e;
		}
	}
}
