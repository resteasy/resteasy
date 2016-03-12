package org.jboss.resteasy.test.nextgen.producers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.PrintWriter;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import junit.framework.Assert;

/**
 * RESTEASY-1134
 * 
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date March 12, 2016
 */
public class MissingProducerTest
{
   protected static ResteasyDeployment deployment;
   protected static Dispatcher dispatcher;
   protected static PrintStream out_orig;
   protected static PrintStream err_orig;
   protected static ByteArrayOutputStream baos;
   
   @BeforeClass
   public static void before() throws Exception
   {
      System.out.println(new File("target/classes/META-INF/services/").mkdirs());
      File providers = new File("target/classes/META-INF/services/javax.ws.rs.ext.Providers");
      System.out.println(providers.createNewFile());
      PrintWriter writer = new PrintWriter(providers);
      writer.print("org.jboss.resteasy.Missing");
      writer.flush();
      writer.close();
      out_orig = System.out;
      err_orig = System.err;
      baos = new ByteArrayOutputStream();
      PrintStream print_tmp = new PrintStream(baos);
      System.setOut(print_tmp);
      System.setErr(print_tmp);
      deployment = EmbeddedContainer.start();
      dispatcher = deployment.getDispatcher();
   }
   
   @AfterClass
   public static void after() throws Exception
   {
      new File("target/classes/META-INF/services/javax.ws.rs.ext.Providers").delete();
      new File("target/classes/META-INF/services").delete();
      new File("target/classes/META-INF").delete();
      EmbeddedContainer.stop();
      System.setOut(out_orig);
      System.setErr(err_orig);
      dispatcher = null;
      deployment = null;
   }
   
   
   @Test
   public void testMissingProducer()
   {
      String output = new String(baos.toByteArray());
      Assert.assertTrue(output.contains("RESTEASY002120: ClassNotFoundException: "));
      Assert.assertTrue(output.contains("Unable to load builtin provider org.jboss.resteasy.Missing from "));
      Assert.assertTrue(output.contains("resteasy-jaxrs-testsuite/target/classes/META-INF/services/javax.ws.rs.ext.Providers"));
   }
}
