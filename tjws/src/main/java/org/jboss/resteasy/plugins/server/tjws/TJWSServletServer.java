package org.jboss.resteasy.plugins.server.tjws;

import java.io.File;
import java.util.Hashtable;
import java.util.Properties;

import javax.servlet.http.HttpServlet;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;

import Acme.Serve.SSLAcceptor;
import Acme.Serve.Serve;

/**
 * 
 * This cannot be restarted once stopped.
 * <p>
 * All properties can be set by a Properties map.  See more info at <a href="http://tjws.sourceforge.net/">TJWS Website</a>
 * <p>
 * Server will not run unless you set the port or ssl port properties.  You cannot run both an SSL and Non-SSL listener.
 * Create a new server if you want to do that.
 *
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 * 
 * @deprecated See resteasy-undertow module.
 */
public class TJWSServletServer
{

   public static class FileMappingServe extends Serve
   {
      private static final long serialVersionUID = -5031104686755790970L;
      private PathTreeDictionary mappingTable = null;
      private Hashtable<String,String> initParams = null;

      public void initFileMappings()
      {
         addDefaultServlets(null); // optional file servlet
         if (mappingTable != null)
            super.setMappingTable(mappingTable);
      }

      ;

      public void addFileMapping(String context, File directory)
      {
         if (mappingTable == null)
         {
            mappingTable = new PathTreeDictionary();
         }
         mappingTable.put(context, directory);
      }
      
      public String getInitParameter(String param)
      {
         if (initParams == null)
         {
            return null;
         }
         return initParams.get(param);
      }

      public Hashtable<String, String> getInitParams()
      {
         return initParams;
      }

      public void setInitParams(Hashtable<String, String> initParams)
      {
         this.initParams = initParams;
      }
   }

   public FileMappingServe server = new FileMappingServe();

   protected Properties props = new Properties();

   public void addServlet(String bindPath, HttpServlet servlet)
   {
      server.addServlet(bindPath, servlet);
   }

   public void addServlet(String bindPath, HttpServlet servlet, Hashtable initParams)
   {
      server.addServlet(bindPath, servlet, initParams);
   }
   
   public void addServlet(String bindPath, HttpServlet servlet, Hashtable<String,String> initParams, Hashtable<String,String> contextParams)
   {
      server.setInitParams(contextParams);
      server.addServlet(bindPath, servlet, initParams);
   }
   
   public void setProps(Properties props)
   {
      this.props.putAll(props);
   }

   public void setPort(int port)
   {
      //props.put(Serve.ARG_PORT, Integer.toString(port));
      props.put(Serve.ARG_PORT, port);
   }

   public void setBindAddress(String address)
   {
      props.put(Serve.ARG_BINDADDRESS, address);
   }

   public void setSessionTimeout(long timeout)
   {
      props.put(Serve.ARG_SESSION_TIMEOUT, Long.toString(timeout));
   }

   public void setKeepAlive(boolean keepAlive)
   {
      props.put(Serve.ARG_KEEPALIVE, Boolean.toString(keepAlive));
   }

   public void setKeepAliveTimeout(long timeout)
   {
      props.put(Serve.ARG_KEEPALIVE_TIMEOUT, Long.toString(timeout));
   }

   public void setMaxKeepAliveConnections(int max)
   {
      props.put(Serve.ARG_MAX_CONN_USE, Integer.toString(max));
   }

   public void setThreadPoolSize(int max)
   {
      props.put(Serve.ARG_THREAD_POOL_SIZE, Integer.toString(max));
   }

   public void setSSLAlgorithm(String algorithm)
   {
      props.put(SSLAcceptor.ARG_ALGORITHM, algorithm);
   }

   public void setSSLKeyStoreFile(String path)
   {
      props.put(SSLAcceptor.ARG_KEYSTOREFILE, path);
   }

   public void setSSLKeyStorePass(String passwd)
   {
      props.put(SSLAcceptor.ARG_KEYSTOREPASS, passwd);
   }

   public void setSSLKeyPass(String passwd)
   {
      props.put(SSLAcceptor.ARG_KEYPASS, passwd);
   }

   public void setSSLKeyStoreType(String type)
   {
      props.put(SSLAcceptor.ARG_KEYSTORETYPE, type);
   }

   public void setSSLProtocol(String protocol)
   {
      props.put(SSLAcceptor.ARG_PROTOCOL, protocol);
   }

   public void setSSLPort(int port)
   {
      props.put(SSLAcceptor.ARG_PORT, Integer.toString(port));
   }

   public void addFileMapping(String context, File directory)
   {
      server.addFileMapping(context, directory);
   }

   public void start()
   {
      if (this.props == null) this.props = new Properties();
      if (!props.containsKey(Serve.ARG_PORT) && !props.containsKey(SSLAcceptor.ARG_PORT))
         throw new RuntimeException(Messages.MESSAGES.mustSetPort());
      if (props.containsKey(Serve.ARG_PORT) && props.containsKey(SSLAcceptor.ARG_PORT))
         throw new RuntimeException(Messages.MESSAGES.mustSetEitherPortOrSSLPort());
      if (props.containsKey(SSLAcceptor.ARG_PORT)) props.put(Serve.ARG_ACCEPTOR_CLASS, SSLAcceptor.class.getName());
      props.setProperty(Serve.ARG_NOHUP, "nohup");
      server.arguments = props;
      server.initFileMappings();
      server.runInBackground();
   }

   public void stop()
   {
      server.stopBackground();
   }

}
