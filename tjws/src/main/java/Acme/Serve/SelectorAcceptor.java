/* tjws - SelectorAcceptor.java
 * Copyright (C) 1999-2007 Dmitriy Rogatkin.  All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS'' AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 *  ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 *  DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *  CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 *  LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 *  OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 *  SUCH DAMAGE.
 *  
 *  Visit http://tjws.sourceforge.net to get the latest information
 *  about Rogatkin's products.                                                        
 *  $Id: SelectorAcceptor.java,v 1.8 2008/01/18 10:05:23 dmitriy Exp $                
 *  Created on Feb 21, 2007
 *  @author dmitriy
 */
package Acme.Serve;

import Acme.Serve.Serve.Acceptor;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Map;

/**
 * @deprecated See resteasy-undertow module.
 */
@Deprecated
public class SelectorAcceptor implements Acceptor
{
   private ServerSocketChannel channel;

   private Selector selector;

   private Iterator readyItor;

   public Socket accept() throws IOException
   {
      do
      {
         if (readyItor == null)
         {
            if (selector.select() > 0)
               readyItor = selector.selectedKeys().iterator();
            else
               throw new IOException();
         }

         if (readyItor.hasNext())
         {

            // Get key from set
            SelectionKey key = (SelectionKey) readyItor.next();

            // Remove current entry
            readyItor.remove();
            // TODO add processing CancelledKeyException
            if (key.isValid() && key.isAcceptable())
            {
               // Get channel
               ServerSocketChannel keyChannel = (ServerSocketChannel) key.channel();

               // Get server socket
               ServerSocket serverSocket = keyChannel.socket();

               // Accept request
               return serverSocket.accept();
            }
         }
         else
            readyItor = null;
      } while (true);
   }

   public void destroy() throws IOException
   {
      String exceptions = "";
      try
      {
         channel.close();
      }
      catch (IOException e)
      {
         exceptions += e.toString();
      }
      try
      {
         selector.close();
      }
      catch (IOException e)
      {
         exceptions += e.toString();
      }
      if (exceptions.length() > 0)
         throw new IOException(exceptions);
   }

   @SuppressWarnings(value = "unchecked")
   public void init(Map inProperties, Map outProperties) throws IOException
   {
      selector = Selector.open();

      channel = ServerSocketChannel.open();
      channel.configureBlocking(false);
      int port = inProperties.get(Serve.ARG_PORT) != null ? ((Integer) inProperties.get(Serve.ARG_PORT)).intValue()
              : Serve.DEF_PORT;
      InetSocketAddress isa = null;
      if (inProperties.get(Serve.ARG_BINDADDRESS) != null)
         try
         {
            isa = new InetSocketAddress((String) inProperties.get(Serve.ARG_BINDADDRESS), port);
         }
         catch (Exception e)
         {
         }
      if (isa == null)
         isa = new InetSocketAddress(port);
      // TODO add ARG_BACKLOG
      channel.socket().bind(isa);

      // Register interest in when connection
      channel.register(selector, SelectionKey.OP_ACCEPT);
      if (outProperties != null)
      {
         if (channel.socket().isBound())
            outProperties.put(Serve.ARG_BINDADDRESS, channel.socket().getInetAddress().getHostName());
         else
            outProperties.put(Serve.ARG_BINDADDRESS, InetAddress.getLocalHost().getHostName());
      }
   }

   public String toString()
   {
      return "SelectorAcceptor - " + (channel == null ? "unset" : "" + channel.socket());
   }
}
