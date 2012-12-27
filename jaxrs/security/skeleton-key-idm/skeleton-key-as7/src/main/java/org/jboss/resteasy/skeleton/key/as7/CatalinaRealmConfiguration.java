package org.jboss.resteasy.skeleton.key.as7;

import org.apache.catalina.Session;
import org.apache.catalina.SessionEvent;
import org.apache.catalina.SessionListener;
import org.jboss.logging.Logger;
import org.jboss.resteasy.skeleton.key.RealmConfiguration;
import org.jboss.resteasy.skeleton.key.SkeletonKeyTokenVerification;

import java.util.HashMap;
import java.util.Map;

/**
* @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
* @version $Revision: 1 $
*/
public class CatalinaRealmConfiguration extends RealmConfiguration implements SessionListener
{
   protected Map<Session, String> sessionMap = new HashMap<Session, String>();
   private static final Logger log = Logger.getLogger(CatalinaRealmConfiguration.class);

   public void register(Session session, SkeletonKeyTokenVerification verification)
   {
      log.info("registering: " + verification.getToken().getId());
      synchronized(sessionMap)
      {
         sessionMap.put(session, verification.getToken().getId());
         synchronized(verifications)
         {
            verifications.put(verification.getToken().getId(), verification);
         }
      }
   }

   @Override
   public void sessionEvent(SessionEvent event)
   {
      // We only care about session destroyed events
      if (!Session.SESSION_DESTROYED_EVENT.equals(event.getType())
              && (!Session.SESSION_PASSIVATED_EVENT.equals(event.getType())))
         return;

      Session session = event.getSession();
      boolean logout = false;
      // Was the session destroyed as the result of a timeout?
      // If so, we'll just remove the expired session from the
      // SSO.  If the session was logged out, we'll log out
      // of all session associated with the SSO.
      if (((session.getMaxInactiveInterval() > 0)
              && (System.currentTimeMillis() - session.getLastAccessedTimeInternal() >=
              session.getMaxInactiveInterval() * 1000))
              || (Session.SESSION_PASSIVATED_EVENT.equals(event.getType())))
      {
      } else
      {
         // The session was logged out.
         // Deregister this single session id, invalidating
         // associated sessions
         logout = true;
      }

      String id = null;
      synchronized (sessionMap)
      {
         id = sessionMap.remove(event.getSession());
         if (id != null)
         {
            if (!logout)
            {
               for (String val : sessionMap.values())
               {
                  if (val.equals(id))
                  {
                     return;
                  }
               }
            }
            log.info("*** REMOVING VERFICIATION: " + id);
            synchronized (verifications)
            {
               verifications.remove(id);
            }
         }
      }
   }
}
