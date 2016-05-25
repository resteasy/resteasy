package se.unlogic.eagledns.zoneproviders.file;

import org.apache.log4j.Logger;
import org.xbill.DNS.Name;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Zone;
import se.unlogic.eagledns.SecondaryZone;
import se.unlogic.eagledns.ZoneChangeCallback;
import se.unlogic.eagledns.ZoneProvider;
import se.unlogic.eagledns.ZoneProviderUpdatable;
import se.unlogic.standardutils.numbers.NumberUtils;
import se.unlogic.standardutils.timer.RunnableTimerTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;


/**
 * This class loads primary zones from zone files in the file system.
 * The zone files have to formated accordingly to RFC 1035 (http://tools.ietf.org/html/rfc1035)
 * and RFC 1034 (http://tools.ietf.org/html/rfc1034).
 * 
 * @author Robert "Unlogic" Olofsson
 * @author Michael Neale, Red Hat (JBoss division)
 *
 */
public class FileZoneProvider implements ZoneProvider, ZoneProviderUpdatable, Runnable {

	private final Logger log = Logger.getLogger(this.getClass());

	private String name;
	private String zoneFileDirectory;

	private boolean autoReloadZones;
	private Long pollingInterval;

	private Map<String, Long> lastFileList = new HashMap<String, Long>();

	private ZoneChangeCallback changeCallback;

	private Timer watcher;

	public void init(String name) {

		this.name = name;

		if(autoReloadZones && pollingInterval != null){

			watcher = new Timer(true);
			watcher.schedule(new RunnableTimerTask(this), 5000, pollingInterval);
		}
	}

	public void run() {

		if (changeCallback != null && hasDirectoryChanged()){

			log.info("Changes in directory " + zoneFileDirectory + " detected");

			changeCallback.zoneDataChanged();
		}
	}

	private boolean hasDirectoryChanged() {
		File folder = new File(this.zoneFileDirectory);
		File[] files = folder.listFiles();
		if (files.length != lastFileList.size()) {
			return true;
		}
		for (File f : folder.listFiles()) {
			if (!lastFileList.containsKey(f.getName())) {
				return true;
			}
			if (f.lastModified() > lastFileList.get(f.getName())) {
				return true;
			}
		}

		return false;
	}


	/** Refresh our list of zone files for watching */
	private void updateZoneFiles(File[] files) {
		lastFileList = new HashMap<String, Long>();
		for (File f : files) {
			lastFileList.put(f.getName(), f.lastModified());
		}
	}

	public Collection<Zone> getPrimaryZones() {

		File zoneDir = new File(this.zoneFileDirectory);

		if(!zoneDir.exists() || !zoneDir.isDirectory()){

			log.error("Zone file directory specified for FileZoneProvider " + name + " does not exist!");
			return null;

		}else if(!zoneDir.canRead()){

			log.error("Zone file directory specified for FileZoneProvider " + name + " is not readable!");
			return null;
		}

		File[] files = zoneDir.listFiles();
		updateZoneFiles(files);

		if(files == null || files.length == 0){

			log.info("No zone files found for FileZoneProvider " + name + " in directory " + zoneDir.getPath());
			return null;
		}

		ArrayList<Zone> zones = new ArrayList<Zone>(files.length);

		for(File zoneFile : files){

			if(!zoneFile.canRead()){
				log.error("FileZoneProvider " + name + " unable to access zone file " + zoneFile );
				continue;
			}

			Name origin;
			try {

				origin = Name.fromString(zoneFile.getName(), Name.root);
				Zone zone = new Zone(origin, zoneFile.getPath());

				log.debug("FileZoneProvider " + name + " successfully parsed zone file " + zoneFile.getName());

				zones.add(zone);

			} catch (TextParseException e) {

				log.error("FileZoneProvider " + name + " unable to parse zone file " + zoneFile.getName(),e);

			} catch (IOException e) {

				log.error("Unable to parse zone file " + zoneFile + " in FileZoneProvider " + name,e);
			}
		}

		if(!zones.isEmpty()){

			return zones;
		}

		return null;
	}

	public void unload() {

	}


	public String getZoneFileDirectory() {
		return zoneFileDirectory;
	}


	public void setZoneFileDirectory(String zoneFileDirectory) {

		this.zoneFileDirectory = zoneFileDirectory;

		log.debug("zoneFileDirectory set to " + zoneFileDirectory);
	}

	public Collection<SecondaryZone> getSecondaryZones() {

		//Not supported
		return null;
	}

	public void zoneUpdated(SecondaryZone secondaryZone) {

		//Not supported
	}

	public void zoneChecked(SecondaryZone secondaryZone) {

		//Not supported
	}

	public void setChangeListener(ZoneChangeCallback ev) {
		this.changeCallback = ev;
	}


	public void setAutoReloadZones(String autoReloadZones) {
		this.autoReloadZones = Boolean.parseBoolean(autoReloadZones);
	}


	public void setPollingInterval(String pollingInterval) {

		Long value = NumberUtils.toLong(pollingInterval);

		if(value != null && value > 0){

			this.pollingInterval = value;

		}else{

			log.warn("Invalid polling interval specified: " + pollingInterval);
		}
	}
}
