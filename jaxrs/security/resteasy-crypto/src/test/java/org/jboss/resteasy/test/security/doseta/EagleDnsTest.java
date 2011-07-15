package org.jboss.resteasy.test.security.doseta;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import se.unlogic.eagledns.EagleDNS;

import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.net.URL;
import java.util.Hashtable;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class EagleDnsTest
{
   private static Logger log = Logger.getLogger(EagleDnsTest.class);
   private static EagleDNS dns;

   @Test
   public void testBoot() throws Exception
   {

      log.info("HELLO!!!!!!");
      final URL path = Thread.currentThread().getContextClassLoader().getResource("dns/conf/config.xml");

      dns = new EagleDNS();
      dns.setConfigClassPath("dns/conf/config.xml");
      dns.start();
      checkDNS("mail._domainKey.samplezone.org");

      //if (true) throw new Exception("failed");


      dns.shutdown();
   }

   public void checkDNS(String domain) throws Exception
   {
      log.info("IN CHECK DNS!!!!");
      Hashtable<String, String> env = new Hashtable<String, String>();
      env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
      env.put("java.naming.provider.url", "dns://localhost:6363");
      DirContext dnsContext = new InitialDirContext(env);

      log.info("Check domain: " + domain);
      Attributes attrs1 = dnsContext.getAttributes(domain, new String[]{"TXT"});
      log.info("Attributes size: " + attrs1.size());
      Assert.assertTrue(attrs1 != null);
      Assert.assertTrue(attrs1.size() > 0);
      javax.naming.directory.Attribute txtrecord = attrs1.get("txt");
      log.info("TEXT RECORD: " + txtrecord.get());
   }
}
