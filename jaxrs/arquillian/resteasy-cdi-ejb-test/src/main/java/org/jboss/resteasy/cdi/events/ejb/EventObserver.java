package org.jboss.resteasy.cdi.events.ejb;

import java.util.ArrayList;

import javax.ejb.Local;
import javax.enterprise.event.Observes;

/**
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Jun 29, 2012
 */
@Local
public interface EventObserver
{
   public void process(@Observes @Process String event);
   public void processRead(@Observes @Process @Read(context="resource") String event);
   public void processWrite(@Observes @Process @Write(context="resource") String event);
   public ArrayList<Object> getEventList();
}
