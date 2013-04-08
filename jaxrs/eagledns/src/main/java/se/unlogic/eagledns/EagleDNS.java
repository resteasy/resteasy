package se.unlogic.eagledns;

import org.apache.log4j.Logger;
import org.xbill.DNS.Address;
import org.xbill.DNS.CNAMERecord;
import org.xbill.DNS.DClass;
import org.xbill.DNS.DNAMERecord;
import org.xbill.DNS.ExtendedFlags;
import org.xbill.DNS.Flags;
import org.xbill.DNS.Header;
import org.xbill.DNS.Message;
import org.xbill.DNS.NSRecord;
import org.xbill.DNS.Name;
import org.xbill.DNS.NameTooLongException;
import org.xbill.DNS.OPTRecord;
import org.xbill.DNS.Opcode;
import org.xbill.DNS.RRset;
import org.xbill.DNS.Rcode;
import org.xbill.DNS.Record;
import org.xbill.DNS.Section;
import org.xbill.DNS.SetResponse;
import org.xbill.DNS.TSIG;
import org.xbill.DNS.TSIGRecord;
import org.xbill.DNS.Type;
import org.xbill.DNS.Zone;
import se.unlogic.standardutils.reflection.ReflectionUtils;
import se.unlogic.standardutils.settings.SettingNode;
import se.unlogic.standardutils.settings.XMLSettingNode;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.time.MillisecondTimeUnits;
import se.unlogic.standardutils.timer.RunnableTimerTask;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * EagleDNS copyright Robert "Unlogic" Olofsson (unlogic@unlogic.se)
 * 
 * Based on the jnamed class from the dnsjava project (http://www.dnsjava.org/) copyright (c) 1999-2004 Brian Wellington (bwelling@xbill.org)
 * 
 * @author Robert "Unlogic" Olofsson
 * @author Michael Neale, Red Hat (JBoss division)
 */

public class EagleDNS implements Runnable, EagleManager {

	public static final String VERSION = "Eagle DNS 1.0";

	static final int FLAG_DNSSECOK = 1;
	static final int FLAG_SIGONLY = 2;

	private final Logger log = Logger.getLogger(this.getClass());

	private final ConcurrentHashMap<Name, CachedPrimaryZone> primaryZoneMap = new ConcurrentHashMap<Name, CachedPrimaryZone>();
	private final ConcurrentHashMap<Name, CachedSecondaryZone> secondaryZoneMap = new ConcurrentHashMap<Name, CachedSecondaryZone>();
	private final HashMap<Name, TSIG> TSIGs = new HashMap<Name, TSIG>();

	private final HashMap<String, ZoneProvider> zoneProviders = new HashMap<String, ZoneProvider>();

	private int tcpThreadPoolSize = 20;
	private int udpThreadPoolSize = 20;

	private int tcpThreadPoolShutdownTimeout = 60;
	private int udpThreadPoolShutdownTimeout = 60;

	private ArrayList<TCPSocketMonitor> tcpMonitorThreads = new ArrayList<TCPSocketMonitor>();
	private ArrayList<UDPSocketMonitor> udpMonitorThreads = new ArrayList<UDPSocketMonitor>();

	private ThreadPoolExecutor tcpThreadPool;
	private ThreadPoolExecutor udpThreadPool;

	private int axfrTimeout = 60;

	private String remotePassword;
	private Integer remotePort;
	private LoginHandler loginHandler;

	private Timer secondaryZoneUpdateTimer;
	private RunnableTimerTask timerTask;
   private String configFilePath;

	private boolean shutdown = false;

   public EagleDNS() throws UnknownHostException {

   }

   public void setConfigFilePath(String configFilePath)
   {
      this.configFilePath = configFilePath;
   }

   public void setConfigClassPath(String path)
   {
      URL url = Thread.currentThread().getContextClassLoader().getResource(path);
      this.configFilePath = url.getPath();
   }

   public void start() throws UnknownHostException {

      //URL logurl = Thread.currentThread().getContextClassLoader().getResource("log4j.xml");
      //DOMConfigurator.configure(logurl);
      //DOMConfigurator.configure("conf/log4j.xml");

		log.info(VERSION + " starting...");

		XMLSettingNode configFile;

		try {
			log.debug("Parsing config file..." + configFilePath);
			configFile = new XMLSettingNode(configFilePath);

		} catch (Exception e) {

			log.fatal("Unable to open config file " + configFilePath + ", aborting startup!");
			System.out.println("Unable to open config file " + configFilePath + ", aborting startup!");
			return;
		}

		List<Integer> ports = configFile.getIntegers("/Config/System/Port");

		if (ports.isEmpty()) {

			log.debug("No ports found in config file " + configFilePath + ", using default port 53");
			ports.add(new Integer(53));
		}

		List<InetAddress> addresses = new ArrayList<InetAddress>();
		List<String> addressStrings = configFile.getStrings("/Config/System/Address");

		if (addressStrings == null || addressStrings.isEmpty()) {

			log.debug("No addresses found in config, listening on all addresses (0.0.0.0)");
			addresses.add(Address.getByAddress("0.0.0.0"));

		} else {

			for (String addressString : addressStrings) {

				try {

					addresses.add(Address.getByAddress(addressString));

				} catch (UnknownHostException e) {

					log.error("Invalid address " + addressString + " specified in config file, skipping address " + e);
				}
			}

			if (addresses.isEmpty()) {

				log.fatal("None of the " + addressStrings.size() + " addresses specified in the config file are valid, aborting startup!\n" + "Correct the addresses or remove them from the config file if you want to listen on all interfaces.");
            return;
			}
		}

		Integer tcpThreadPoolSize = configFile.getInteger("/Config/System/TCPThreadPoolSize");

		if (tcpThreadPoolSize != null) {

			log.debug("Setting TCP thread pool size to " + tcpThreadPoolSize);
			this.tcpThreadPoolSize = tcpThreadPoolSize;
		}

		Integer tcpThreadPoolShutdownTimeout = configFile.getInteger("/Config/System/TCPThreadPoolShutdownTimeout");

		if (tcpThreadPoolShutdownTimeout != null) {

			log.debug("Setting TCP thread pool shutdown timeout to " + tcpThreadPoolSize + " seconds");
			this.tcpThreadPoolShutdownTimeout = tcpThreadPoolShutdownTimeout;
		}

		Integer udpThreadPoolSize = configFile.getInteger("/Config/System/UDPThreadPoolSize");

		if (udpThreadPoolSize != null) {

			log.debug("Setting UDP thread pool size to " + udpThreadPoolSize);
			this.udpThreadPoolSize = udpThreadPoolSize;
		}

		Integer udpThreadPoolShutdownTimeout = configFile.getInteger("/Config/System/UDPThreadPoolShutdownTimeout");

		if (udpThreadPoolShutdownTimeout != null) {

			log.debug("Setting UDP thread pool shutdown timeout to " + udpThreadPoolSize + " seconds");
			this.udpThreadPoolShutdownTimeout = udpThreadPoolShutdownTimeout;
		}

		this.remotePassword = configFile.getString("/Config/System/RemoteManagementPassword");

		log.debug("Remote management password set to " + remotePassword);

		this.remotePort = configFile.getInteger("/Config/System/RemoteManagementPort");

		log.debug("Remote management port set to " + remotePort);

		Integer axfrTimeout = configFile.getInteger("/Config/System/AXFRTimeout");

		if (axfrTimeout != null) {

			log.debug("Setting AXFR timeout to " + axfrTimeout);
			this.axfrTimeout = axfrTimeout;
		}

		// TODO TSIG stuff

		List<XMLSettingNode> zoneProviderElements = configFile.getSettings("/Config/ZoneProviders/ZoneProvider");

		for (XMLSettingNode settingNode : zoneProviderElements) {

			String name = settingNode.getString("Name");

			if (StringUtils.isEmpty(name)) {

				log.error("ZoneProvider element with no name set found in config, ignoring element.");
				continue;
			}

			String className = settingNode.getString("Class");

			if (StringUtils.isEmpty(className)) {

				log.error("ZoneProvider element with no class set found in config, ignoring element.");
				continue;
			}

			try {

				log.debug("Instantiating zone provider " + name + " (" + className + ")");

				ZoneProvider zoneProvider = (ZoneProvider) Class.forName(className).newInstance();

				log.debug("Zone provider " + name + " successfully instantiated");

				List<XMLSettingNode> propertyElements = settingNode.getSettings("Properties/Property");

				for (SettingNode propertyElement : propertyElements) {

					String propertyName = propertyElement.getString("@name");

					if (StringUtils.isEmpty(propertyName)) {

						log.error("Property element with no name set found in config, ignoring element");
						continue;
					}

					String value = propertyElement.getString(".");

					log.debug("Found value " + value + " for property " + propertyName);

					try {
						Method method = zoneProvider.getClass().getMethod("set" + StringUtils.toFirstLetterUppercase(propertyName), String.class);

						ReflectionUtils.fixMethodAccess(method);

						log.debug("Setting property " + propertyName);

						try {

							method.invoke(zoneProvider, value);

						} catch (IllegalArgumentException e) {

							log.error("Unable to set property " + propertyName + " on zone provider " + name + " (" + className + ")", e);

						} catch (InvocationTargetException e) {

							log.error("Unable to set property " + propertyName + " on zone provider " + name + " (" + className + ")", e);
						}

					} catch (SecurityException e) {

						log.error("Unable to find matching setter method for property " + propertyName + " in zone provider " + name + " (" + className + ")", e);

					} catch (NoSuchMethodException e) {

						log.error("Unable to find matching setter method for property " + propertyName + " in zone provider " + name + " (" + className + ")", e);
					}
				}

				try {

					if (zoneProvider instanceof ZoneProviderUpdatable) {
						((ZoneProviderUpdatable) zoneProvider).setChangeListener(new ZoneChangeCallback() {
							public void zoneDataChanged() {
								reloadZones();
							}
						});
					}

					zoneProvider.init(name);

					log.debug("Zone provider " + name + " (" + className + ") successfully initialized!");

					this.zoneProviders.put(name, zoneProvider);

				} catch (Throwable e) {

					log.error("Error initializing zone provider " + name + " (" + className + ")", e);
				}

			} catch (InstantiationException e) {

				log.error("Unable to create instance of class " + className + " for zone provider " + name, e);

			} catch (IllegalAccessException e) {

				log.error("Unable to create instance of class " + className + " for zone provider " + name, e);

			} catch (ClassNotFoundException e) {

				log.error("Unable to create instance of class " + className + " for zone provider " + name, e);
			}
		}

		if (zoneProviders.isEmpty()) {
			log.fatal("No zone providers found or started, aborting startup!");
			return;
		}

		this.reloadZones();

		if(remotePassword == null || remotePort == null){

			log.debug("Remote managed port and/or password not set, remote managent will not be available.");

		}else{

			log.debug("Starting remote interface on port " + remotePort);

			this.loginHandler = new LoginHandler(this, this.remotePassword);

			try {
				EagleLogin eagleLogin = (EagleLogin) UnicastRemoteObject.exportObject(loginHandler,remotePort);
				UnicastRemoteObject.exportObject(this,remotePort);

				Registry registry = LocateRegistry.createRegistry(remotePort);

				registry.bind("eagleLogin", eagleLogin);

			} catch (AccessException e) {

				log.fatal("Unable to start remote manangement interface, aborting startup!",e);
				return;

			} catch (RemoteException e) {

				log.fatal("Unable to start remote manangement interface, aborting startup!",e);
				return;

			} catch (AlreadyBoundException e) {

				log.fatal("Unable to start remote manangement interface, aborting startup!",e);
				return;
			}
		}

		log.debug("Initializing TCP thread pool...");
		this.tcpThreadPool = new ThreadPoolExecutor(this.tcpThreadPoolSize, this.tcpThreadPoolSize, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

		log.debug("Initializing UDP thread pool...");
		this.udpThreadPool = new ThreadPoolExecutor(this.udpThreadPoolSize, this.udpThreadPoolSize, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

		Iterator<InetAddress> iaddr = addresses.iterator();
		while (iaddr.hasNext()) {
			InetAddress addr = iaddr.next();
			Iterator<Integer> iport = ports.iterator();
			while (iport.hasNext()) {
				int port = iport.next().intValue();

				try {
					this.udpMonitorThreads.add(new UDPSocketMonitor(this, addr, port));
				} catch (SocketException e) {
					log.error("Unable to open UDP server socket on address " + addr + ":" + port + ", " + e);
				}

				try {
					this.tcpMonitorThreads.add(new TCPSocketMonitor(this, addr, port));
				} catch (IOException e) {
					log.error("Unable to open TCP server socket on address " + addr + ":" + port + ", " + e);
				}
			}
		}

		if (this.tcpMonitorThreads.isEmpty() && this.udpMonitorThreads.isEmpty()) {

			log.fatal("Not bound on any sockets, aborting startup!");
			return;
		}

		log.debug("Starting secondary zone update timer...");
		this.timerTask = new RunnableTimerTask(this);
		this.secondaryZoneUpdateTimer = new Timer();
		this.secondaryZoneUpdateTimer.schedule(timerTask, MillisecondTimeUnits.SECOND * 60, MillisecondTimeUnits.SECOND * 60);

		log.info(VERSION + " started with " + this.primaryZoneMap.size() + " primary zones and " + this.secondaryZoneMap.size() + " secondary zones");
	}

   protected CountDownLatch shutdownLatch = new CountDownLatch(1);

	public synchronized void shutdown() {

		new Thread(){@Override
			public void run(){

			//RMI thread workaround
			actualShutdown();

		}}.start();
      try
      {
         log.debug("Awaiting shutdown latch");
         shutdownLatch.await();
      }
      catch (InterruptedException e)
      {
         throw new RuntimeException(e);
      }
   }

	void actualShutdown(){

      log.debug("In shutdown...");
		if (shutdown == false) {

         try
         {
            log.info("Shutting down " + VERSION + "...");

            shutdown = true;

            log.debug("Stopping secondary zone update timer...");
            timerTask.cancel();
            secondaryZoneUpdateTimer.cancel();

            log.debug("Stopping TCP thread pool...");
            tcpThreadPool.shutdown();

            try {
               tcpThreadPool.awaitTermination(tcpThreadPoolShutdownTimeout, TimeUnit.SECONDS);

            } catch (InterruptedException e1) {

               log.error("Timeout waiting " + tcpThreadPoolShutdownTimeout + " seconds for TCP thread pool to shutdown, forcing thread pool shutdown...");
               tcpThreadPool.shutdownNow();
            }

            log.debug("Stopping UDP thread pool...");
            udpThreadPool.shutdown();

            try {
               udpThreadPool.awaitTermination(udpThreadPoolShutdownTimeout, TimeUnit.SECONDS);

            } catch (InterruptedException e1) {

               log.error("Timeout waiting " + udpThreadPoolShutdownTimeout + " seconds for UDP thread pool to shutdown, forcing thread pool shutdown...");
               udpThreadPool.shutdownNow();
            }

            log.debug("Stopping sockets...");
            for (UDPSocketMonitor monitor : udpMonitorThreads)
            {
               try
               {
                  monitor.closeSocket();
               }
               catch (IOException e)
               {

               }
            }

            for (TCPSocketMonitor monitor : tcpMonitorThreads)
            {
               try
               {
                  monitor.closeSocket();
               }
               catch (IOException e)
               {

               }
            }

            log.info(VERSION + " stopped");
         }
         finally
         {
            shutdownLatch.countDown();
         }

         //System.exit(0);
		}
	}

	public synchronized void reloadZones() {

		this.primaryZoneMap.clear();
		this.secondaryZoneMap.clear();

		for (Entry<String, ZoneProvider> zoneProviderEntry : this.zoneProviders.entrySet()) {

			log.debug("Getting primary zones from zone provider " + zoneProviderEntry.getKey());

			Collection<Zone> primaryZones;

			try {
				primaryZones = zoneProviderEntry.getValue().getPrimaryZones();

			} catch (Throwable e) {

				log.error("Error getting primary zones from zone provider " + zoneProviderEntry.getKey(), e);
				continue;
			}

			if (primaryZones != null) {

				for (Zone zone : primaryZones) {

					log.debug("Got zone " + zone.getOrigin());

					this.primaryZoneMap.put(zone.getOrigin(), new CachedPrimaryZone(zone, zoneProviderEntry.getValue()));
				}
			}

			log.debug("Getting secondary zones from zone provider " + zoneProviderEntry.getKey());

			Collection<SecondaryZone> secondaryZones;

			try {
				secondaryZones = zoneProviderEntry.getValue().getSecondaryZones();

			} catch (Throwable e) {

				log.error("Error getting secondary zones from zone provider " + zoneProviderEntry.getKey(), e);
				continue;
			}

			if (secondaryZones != null) {

				for (SecondaryZone zone : secondaryZones) {

					log.debug("Got zone " + zone.getZoneName() + " (" + zone.getRemoteServerAddress() + ")");

					CachedSecondaryZone cachedSecondaryZone = new CachedSecondaryZone(zoneProviderEntry.getValue(), zone);

					this.secondaryZoneMap.put(cachedSecondaryZone.getSecondaryZone().getZoneName(), cachedSecondaryZone);
				}
			}
		}
	}

	// @SuppressWarnings("unused")
	// private void addPrimaryZone(String zname, String zonefile) throws IOException {
	// Name origin = null;
	// if (zname != null) {
	// origin = Name.fromString(zname, Name.root);
	// }
	// Zone newzone = new Zone(origin, zonefile);
	// primaryZoneMap.put(newzone.getOrigin(), newzone);
	// }
	//
	// @SuppressWarnings("unused")
	// private void addSecondaryZone(String zone, String remote) throws IOException, ZoneTransferException {
	// Name zname = Name.fromString(zone, Name.root);
	// Zone newzone = new Zone(zname, DClass.IN, remote);
	// primaryZoneMap.put(zname, newzone);
	// }

	@SuppressWarnings("unused")
	private void addTSIG(String algstr, String namestr, String key) throws IOException {
		Name name = Name.fromString(namestr, Name.root);
		TSIGs.put(name, new TSIG(algstr, namestr, key));
	}

	private Zone findBestZone(Name name) {

		Zone foundzone = getZone(name);

		if (foundzone != null) {
			return foundzone;
		}

		int labels = name.labels();

		for (int i = 1; i < labels; i++) {

			Name tname = new Name(name, i);
			foundzone = getZone(tname);

			if (foundzone != null) {
				return foundzone;
			}
		}

		return null;
	}

	private Zone getZone(Name name) {

		CachedPrimaryZone cachedPrimaryZone = this.primaryZoneMap.get(name);

		if (cachedPrimaryZone != null) {
			return cachedPrimaryZone.getZone();
		}

		CachedSecondaryZone cachedSecondaryZone = this.secondaryZoneMap.get(name);

		if (cachedSecondaryZone != null && cachedSecondaryZone.getSecondaryZone().getZoneCopy() != null) {

			return cachedSecondaryZone.getSecondaryZone().getZoneCopy();
		}

		return null;
	}

	private RRset findExactMatch(Name name, int type, int dclass, boolean glue) {
		Zone zone = findBestZone(name);

		if (zone != null) {
			return zone.findExactMatch(name, type);
		}

		return null;
	}

	private void addRRset(Name name, Message response, RRset rrset, int section, int flags) {
		for (int s = 1; s <= section; s++) {
			if (response.findRRset(name, rrset.getType(), s)) {
				return;
			}
		}
		if ((flags & FLAG_SIGONLY) == 0) {
			Iterator<?> it = rrset.rrs();
			while (it.hasNext()) {
				Record r = (Record) it.next();
				if (r.getName().isWild() && !name.isWild()) {
					r = r.withName(name);
				}
				response.addRecord(r, section);
			}
		}
		if ((flags & (FLAG_SIGONLY | FLAG_DNSSECOK)) != 0) {
			Iterator<?> it = rrset.sigs();
			while (it.hasNext()) {
				Record r = (Record) it.next();
				if (r.getName().isWild() && !name.isWild()) {
					r = r.withName(name);
				}
				response.addRecord(r, section);
			}
		}
	}

	private final void addSOA(Message response, Zone zone) {
		response.addRecord(zone.getSOA(), Section.AUTHORITY);
	}

	private final void addNS(Message response, Zone zone, int flags) {
		RRset nsRecords = zone.getNS();
		addRRset(nsRecords.getName(), response, nsRecords, Section.AUTHORITY, flags);
	}

	private void addGlue(Message response, Name name, int flags) {
		RRset a = findExactMatch(name, Type.A, DClass.IN, true);
		if (a == null) {
			return;
		}
		addRRset(name, response, a, Section.ADDITIONAL, flags);
	}

	private void addAdditional2(Message response, int section, int flags) {
		Record[] records = response.getSectionArray(section);
		for (Record r : records) {
			Name glueName = r.getAdditionalName();
			if (glueName != null) {
				addGlue(response, glueName, flags);
			}
		}
	}

	private final void addAdditional(Message response, int flags) {
		addAdditional2(response, Section.ANSWER, flags);
		addAdditional2(response, Section.AUTHORITY, flags);
	}

	private byte addAnswer(Message response, Name name, int type, int dclass, int iterations, int flags) {
		SetResponse sr;
		byte rcode = Rcode.NOERROR;

		if (iterations > 6) {
			return Rcode.NOERROR;
		}

		if (type == Type.SIG || type == Type.RRSIG) {
			type = Type.ANY;
			flags |= FLAG_SIGONLY;
		}

		Zone zone = findBestZone(name);
		if (zone != null) {
			sr = zone.findRecords(name, type);

			if (sr.isNXDOMAIN()) {
				response.getHeader().setRcode(Rcode.NXDOMAIN);
				if (zone != null) {
					addSOA(response, zone);
					if (iterations == 0) {
						response.getHeader().setFlag(Flags.AA);
					}
				}
				rcode = Rcode.NXDOMAIN;
			} else if (sr.isNXRRSET()) {
				if (zone != null) {
					addSOA(response, zone);
					if (iterations == 0) {
						response.getHeader().setFlag(Flags.AA);
					}
				}
			} else if (sr.isDelegation()) {
				RRset nsRecords = sr.getNS();
				addRRset(nsRecords.getName(), response, nsRecords, Section.AUTHORITY, flags);
			} else if (sr.isCNAME()) {
				CNAMERecord cname = sr.getCNAME();
				RRset rrset = new RRset(cname);
				addRRset(name, response, rrset, Section.ANSWER, flags);
				if (zone != null && iterations == 0) {
					response.getHeader().setFlag(Flags.AA);
				}
				rcode = addAnswer(response, cname.getTarget(), type, dclass, iterations + 1, flags);
			} else if (sr.isDNAME()) {
				DNAMERecord dname = sr.getDNAME();
				RRset rrset = new RRset(dname);
				addRRset(name, response, rrset, Section.ANSWER, flags);
				Name newname;
				try {
					newname = name.fromDNAME(dname);
				} catch (NameTooLongException e) {
					return Rcode.YXDOMAIN;
				}
				rrset = new RRset(new CNAMERecord(name, dclass, 0, newname));
				addRRset(name, response, rrset, Section.ANSWER, flags);
				if (zone != null && iterations == 0) {
					response.getHeader().setFlag(Flags.AA);
				}
				rcode = addAnswer(response, newname, type, dclass, iterations + 1, flags);
			} else if (sr.isSuccessful()) {
				RRset[] rrsets = sr.answers();
				for (RRset rrset : rrsets) {
					addRRset(name, response, rrset, Section.ANSWER, flags);
				}
				if (zone != null) {
					addNS(response, zone, flags);
					if (iterations == 0) {
						response.getHeader().setFlag(Flags.AA);
					}
				}
			}
		}

		return rcode;
	}

	private byte[] doAXFR(Name name, Message query, TSIG tsig, TSIGRecord qtsig, Socket s) {

		boolean first = true;

		Zone zone = this.findBestZone(name);

		if (zone == null) {

			return errorMessage(query, Rcode.REFUSED);

		}

		// Check that the IP requesting the AXFR is present as a NS in this zone
		boolean axfrAllowed = false;

		Iterator<?> nsIterator = zone.getNS().rrs();

		while (nsIterator.hasNext()) {

			NSRecord record = (NSRecord) nsIterator.next();

			try {
				String nsIP = InetAddress.getByName(record.getTarget().toString()).getHostAddress();

				if (s.getInetAddress().getHostAddress().equals(nsIP)) {

					axfrAllowed = true;
					break;
				}

			} catch (UnknownHostException e) {

				log.warn("Unable to resolve hostname of nameserver " + record.getTarget() + " in zone " + zone.getOrigin() + " while processing AXFR request from " + s.getRemoteSocketAddress());
			}
		}

		if (!axfrAllowed) {
			log.warn("AXFR request of zone " + zone.getOrigin() + " from " + s.getRemoteSocketAddress() + " refused!");
			return errorMessage(query, Rcode.REFUSED);
		}

		Iterator<?> it = zone.AXFR();

		try {
			DataOutputStream dataOut;
			dataOut = new DataOutputStream(s.getOutputStream());
			int id = query.getHeader().getID();
			while (it.hasNext()) {
				RRset rrset = (RRset) it.next();
				Message response = new Message(id);
				Header header = response.getHeader();
				header.setFlag(Flags.QR);
				header.setFlag(Flags.AA);
				addRRset(rrset.getName(), response, rrset, Section.ANSWER, FLAG_DNSSECOK);
				if (tsig != null) {
					tsig.applyStream(response, qtsig, first);
					qtsig = response.getTSIG();
				}
				first = false;
				byte[] out = response.toWire();
				dataOut.writeShort(out.length);
				dataOut.write(out);
			}
		} catch (IOException ex) {
			log.warn("AXFR failed", ex);
		}
		try {
			s.close();
		} catch (IOException ex) {
		}
		return null;
	}

	/*
	 * Note: a null return value means that the caller doesn't need to do
	 * anything.  Currently this only happens if this is an AXFR request over
	 * TCP.
	 */
	byte[] generateReply(Message query, byte[] in, int length, Socket socket) throws IOException {
		Header header;
		// boolean badversion;
		int maxLength;
		int flags = 0;

		header = query.getHeader();
		if (header.getFlag(Flags.QR)) {
			return null;
		}
		if (header.getRcode() != Rcode.NOERROR) {
			return errorMessage(query, Rcode.FORMERR);
		}
		if (header.getOpcode() != Opcode.QUERY) {
			return errorMessage(query, Rcode.NOTIMP);
		}

		Record queryRecord = query.getQuestion();

		TSIGRecord queryTSIG = query.getTSIG();
		TSIG tsig = null;
		if (queryTSIG != null) {
			tsig = TSIGs.get(queryTSIG.getName());
			if (tsig == null || tsig.verify(query, in, length, null) != Rcode.NOERROR) {
				return formerrMessage(in);
			}
		}

		OPTRecord queryOPT = query.getOPT();
		if (queryOPT != null && queryOPT.getVersion() > 0) {
			// badversion = true;
		}

		if (socket != null) {
			maxLength = 65535;
		} else if (queryOPT != null) {
			maxLength = Math.max(queryOPT.getPayloadSize(), 512);
		} else {
			maxLength = 512;
		}

		if (queryOPT != null && (queryOPT.getFlags() & ExtendedFlags.DO) != 0) {
			flags = FLAG_DNSSECOK;
		}

		Message response = new Message(query.getHeader().getID());
		response.getHeader().setFlag(Flags.QR);
		if (query.getHeader().getFlag(Flags.RD)) {
			response.getHeader().setFlag(Flags.RD);
		}
		response.addRecord(queryRecord, Section.QUESTION);

		Name name = queryRecord.getName();
		int type = queryRecord.getType();
		int dclass = queryRecord.getDClass();
		if (type == Type.AXFR && socket != null) {
			return doAXFR(name, query, tsig, queryTSIG, socket);
		}
		if (!Type.isRR(type) && type != Type.ANY) {
			return errorMessage(query, Rcode.NOTIMP);
		}

		byte rcode = addAnswer(response, name, type, dclass, 0, flags);
		if (rcode != Rcode.NOERROR && rcode != Rcode.NXDOMAIN) {
			return errorMessage(query, rcode);
		}

		addAdditional(response, flags);

		if (queryOPT != null) {
			int optflags = (flags == FLAG_DNSSECOK) ? ExtendedFlags.DO : 0;
			OPTRecord opt = new OPTRecord((short) 4096, rcode, (byte) 0, optflags);
			response.addRecord(opt, Section.ADDITIONAL);
		}

		response.setTSIG(tsig, Rcode.NOERROR, queryTSIG);
		return response.toWire(maxLength);
	}

	private byte[] buildErrorMessage(Header header, int rcode, Record question) {
		Message response = new Message();
		response.setHeader(header);
		for (int i = 0; i < 4; i++) {
			response.removeAllRecords(i);
		}
		if (rcode == Rcode.SERVFAIL) {
			response.addRecord(question, Section.QUESTION);
		}
		header.setRcode(rcode);
		return response.toWire();
	}

	byte[] formerrMessage(byte[] in) {
		Header header;
		try {
			header = new Header(in);
		} catch (IOException e) {
			return null;
		}
		return buildErrorMessage(header, Rcode.FORMERR, null);
	}

	private byte[] errorMessage(Message query, int rcode) {
		return buildErrorMessage(query.getHeader(), rcode, query.getQuestion());
	}

	protected void UDPClient(DatagramSocket socket, DatagramPacket inDataPacket) {

	}

	public static String toString(Record record) {

		if (record == null) {

			return null;
		}

		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append(record.getName());

		stringBuilder.append(" ");

		stringBuilder.append(record.getTTL());

		stringBuilder.append(" ");

		stringBuilder.append(DClass.string(record.getDClass()));

		stringBuilder.append(" ");

		stringBuilder.append(Type.string(record.getType()));

		String rdata = record.rdataToString();

		if (!rdata.equals("")) {
			stringBuilder.append(" ");
			stringBuilder.append(rdata);
		}

		return stringBuilder.toString();
	}

	public void run() {

		log.debug("Checking secondary zones...");

		for(CachedSecondaryZone cachedSecondaryZone : this.secondaryZoneMap.values()){

			SecondaryZone secondaryZone = cachedSecondaryZone.getSecondaryZone();

			if(secondaryZone.getZoneCopy() == null || secondaryZone.getDownloaded() == null || (System.currentTimeMillis() - secondaryZone.getDownloaded().getTime()) > (secondaryZone.getZoneCopy().getSOA().getRefresh() * 1000)){

				cachedSecondaryZone.update(this.axfrTimeout);
			}
		}
	}

	protected ThreadPoolExecutor getTcpThreadPool() {

		return tcpThreadPool;
	}

	protected ThreadPoolExecutor getUdpThreadPool() {

		return udpThreadPool;
	}

	public boolean isShutdown() {

		return shutdown;
	}
}
