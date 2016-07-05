package org.jboss.resteasy.cdi.events.ejb;

import java.util.ArrayList;
import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

/**
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright May 8, 2012
 */
@Stateful
public class EventObserverImpl implements EventObserver
{
   @Inject @Read(context="reader") Event<String> readEvent;
   @Inject private Logger log;
   
   private ArrayList<Object> eventList = new ArrayList<Object>();
   
   public void process(@Observes @Process String event)
   {
      eventList.add(event);
      log.info("EventObserverImpl.process() got " + event);
   }
   
   public void processRead(@Observes @Process @Read(context="resource") String event)
   {
      eventList.add(event);
      log.info("EventObserverImpl.processRead() got " + event);
   }
   
   public void processWrite(@Observes @Process @Write(context="resource") String event)
   {
      eventList.add(event);
      log.info("EventObserverImpl.processWrite() got " + event);
   }
   
   public ArrayList<Object> getEventList()
   {
      return new ArrayList<Object>(eventList);
   }
}

