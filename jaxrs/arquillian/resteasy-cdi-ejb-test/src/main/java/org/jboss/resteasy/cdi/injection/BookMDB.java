/*
* JBoss, Home of Professional Open Source
* Copyright 2005, JBoss Inc., and individual contributors as indicated
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2.1 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/
package org.jboss.resteasy.cdi.injection;

import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.jboss.resteasy.cdi.util.Counter;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright May 28, 2012
 */
@MessageDriven(name = "BookMDB",
               activationConfig = {@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
                                   @ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/test")}
)
public class BookMDB implements MessageListener
{
   @Inject private Logger log;
   @Inject private BookResource resource;
   
   public void onMessage(Message arg0)
   {
      log.info("BookResource.theSecret(): " + resource.theSecret());
      TextMessage tm = TextMessage.class.cast(arg0);
      try
      {
         log.info(this + ": msg: " + tm.getText());
         BookCollection collection = resource.getBookCollection();
         log.info(this + ": collection.size() before: " + collection.getBooks().size());
         Counter counter = resource.getCounter();
         collection.addBook(new Book(counter.getNext(), tm.getText()));
         log.info(this + ": collection.size() after: " + collection.getBooks().size());
         resource.getCountDownLatch().countDown();
      }
      catch (JMSException e)
      {
         e.printStackTrace();
      }
   }

}

