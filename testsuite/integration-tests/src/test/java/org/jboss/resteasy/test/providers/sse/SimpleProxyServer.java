package org.jboss.resteasy.test.providers.sse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.jboss.logging.Logger;

/**
 * A SimpleProxyServer.
 * <p>
 * Implementation of the simple one threaded proxy which allows
 * to debug and block communication on the demand.
 *
 * @author <a href="pslavice@jboss.com">Pavel Slavicek</a>
 * @version $Revision: 1.1 $
 */
public class SimpleProxyServer
{
   // Logger
   private static final Logger log = Logger.getLogger(SimpleProxyServer.class);

   // Target host
   private String host;

   // Remote port
   private int remotePort;

   // Local port where client will connect
   private int localPort;

   // Is there request for termination?
   private volatile boolean terminateRequest = false;

   // Main thread was terminated?
   private volatile boolean terminated = true;

   private ServerSocket ss = null;

   /**
    * Create a new SimpleProxyServer.
    *
    * @param host
    * @param remotePort
    * @param localPort
    * @param debugCommunication
    */

   public SimpleProxyServer(String host, int remotePort, int localPort)
   {
      this.host = host;
      this.remotePort = remotePort;
      this.localPort = localPort;
   }

   /**
    * Start proxy
    */
   public void start()
   {
      log.info("Starting proxy " + this);

      if (this.terminated)
      {
         this.terminateRequest = false;
         new Thread(new Runnable()
         {
            public void run()
            {
               runServer();
            }
         }, "ProxyServer").start();
      }
   }

   /**
    * Stop proxy
    */
   public void stop()
   {
      log.info("Stop proxy " + this);

      this.terminateRequest = true;
      if (this.ss != null)
      {
         try
         {
            ss.close();
         }
         catch (IOException e)
         {
            // TODO Auto-generated catch block
            log.error("Error:", e);
         }
      }
      while (!this.terminated)
      {
         try
         {
            Thread.yield();
         }
         catch (Exception e)
         {
         }
      }
   }

   /**
    * Method runs thread with proxy which waits for connection
    */
   private void runServer()
   {

      try
      {
         ss = new ServerSocket(this.localPort);
         log.info("Running proxy server on localport " + String.valueOf(this.localPort));
         while (!this.terminateRequest)
         {
            ss.setSoTimeout(500);
            try
            {
               new ProxyThread(ss.accept(), this.host, this.remotePort, this).start();
            }
            catch (SocketTimeoutException ex)
            {
               // Just ignore this exception
            }
         }
      }
      catch (Exception e)
      {
         log.info("Proxy server is closed");
      }
      finally
      {
         if (ss != null)
         {
            try
            {
               ss.close();
            }
            catch (IOException e)
            {
               log.error(e.getMessage(), e);
            }
         }
         this.terminated = true;
      }
   }

   public void setTerminateRequest()
   {
      this.terminateRequest = true;
   }

   public boolean isTerminateRequest()
   {
      return this.terminateRequest;
   }

   /**
    * Connection thread
    *
    * @author <a href="pslavice@jboss.com">Pavel Slavicek</a>
    * @version $Revision: 1.1 $
    */
   private static class ProxyThread extends Thread
   {

      private static final int BUFFER_SIZE = 32768;

      private Socket clientSocket = null;

      private Socket serverSocket = null;

      private String host;

      private int remotePort;

      private SimpleProxyServer controllableProxy;

      /**
       * Create a new ProxyThread.
       *
       * @param socket
       * @param host
       * @param remotePort
       */
      ProxyThread(Socket socket, String host, int remotePort, SimpleProxyServer controllableProxy)
      {
         super("ProxyThread");
         this.clientSocket = socket;
         this.host = host;
         this.remotePort = remotePort;
         this.controllableProxy = controllableProxy;
      }

      /**
       * @see {@link Thread#run()}
       */
      @Override
      public void run()
      {
         final byte[] request = new byte[BUFFER_SIZE];
         byte[] reply = new byte[BUFFER_SIZE];

         if (this.clientSocket == null)
         {
            log.error("Cannot open connection for null client socket!");
            return;
         }
         try
         {
            final InputStream from_client = clientSocket.getInputStream();
            final OutputStream to_client = clientSocket.getOutputStream();

            try
            {
               serverSocket = new Socket(host, this.remotePort);
               serverSocket.setKeepAlive(true);
            }
            catch (IOException e)
            {
               String msg = "Proxy server cannot connect to " + host + ":" + this.remotePort + ":\n" + e + "\n";
               PrintWriter out = new PrintWriter(to_client);
               out.print(msg);
               out.flush();
               this.clientSocket.close();
               this.controllableProxy.setTerminateRequest();
               log.error(msg);
               return;
            }

            // Get server streams.
            final InputStream from_server = serverSocket.getInputStream();
            final OutputStream to_server = serverSocket.getOutputStream();
            Thread t = new Thread()
            {

               @Override
               public void run()
               {
                  int bytes_read;
                  try
                  {
                     while ((bytes_read = from_client.read(request)) != -1 && !controllableProxy.isTerminateRequest())
                     {
                        try
                        {

                           to_server.write(request, 0, bytes_read);
                           to_server.flush();

                        }
                        catch (IOException e)
                        {
                           log.error(e.getMessage(), e);
                        }
                     }
                  }
                  catch (IOException e)
                  {
                     log.info("connection is closed");
                  }

                  try
                  {
                     if (!clientSocket.isClosed())
                     {
                        clientSocket.close();
                     }
                     if (!serverSocket.isClosed())
                     {
                        serverSocket.close();
                     }

                  }
                  catch (IOException e)
                  {
                     log.error(e.getMessage(), e);
                  }
               }
            };
            t.start();

            int bytes_read;
            while ((bytes_read = from_server.read(reply)) != -1 && !controllableProxy.isTerminateRequest())
            {
               to_client.write(reply, 0, bytes_read);
               to_client.flush();
            }
            if (!clientSocket.isClosed())
            {
               clientSocket.close();
            }
            if (!serverSocket.isClosed())
            {
               serverSocket.close();
            }
         }
         catch (IOException e)
         {
            log.info("connection is closed");
         }
      }
   }
}