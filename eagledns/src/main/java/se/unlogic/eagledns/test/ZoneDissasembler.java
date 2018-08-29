package se.unlogic.eagledns.test;

import org.apache.log4j.Logger;
import org.xbill.DNS.Master;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.TextParseException;

import java.io.File;
import java.io.IOException;


public class ZoneDissasembler {

	public static void main(String[] args) throws TextParseException, IOException {

		File zoneFile = new File("zones/unlogic.se");

		Master master = new Master(zoneFile.getPath(),Name.fromString(zoneFile.getName(), Name.root));

		Record record = master._nextRecord();

		Logger LOG = Logger.getLogger(ZoneDissasembler.class);

		while(record != null){

			LOG.info("Class: " + record.getClass());
			LOG.info("Name: " + record.getName());
			LOG.info("toString: " + record.toString());

			record = master._nextRecord();
		}
	}

}
