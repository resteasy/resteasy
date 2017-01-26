package org.jboss.resteasy.logging.impl;

import org.jboss.resteasy.logging.Logger;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
/**
 * @deprecated See RESTEASY-1578.
 */
@Deprecated
public class JULLogger extends Logger
{
   private transient java.util.logging.Logger delegate;
   private String classname;

   public JULLogger(String classname)
   {
      delegate = java.util.logging.Logger.getLogger(classname);
      this.classname = classname;
   }

   @Override
   public boolean isTraceEnabled()
   {
      return delegate.isLoggable(java.util.logging.Level.FINEST);
   }

   @Override
   public void trace(String message)
   {
      if (!delegate.isLoggable(java.util.logging.Level.FINEST)) return;
      delegate.logp(java.util.logging.Level.FINEST, classname, "", message);
   }

   @Override
   public void trace(String message, Object... params)
   {
      if (!delegate.isLoggable(java.util.logging.Level.FINE)) return;
      delegate.logp(java.util.logging.Level.FINEST, classname, "", message, params);
   }

   @Override
   public void trace(String message, Throwable error)
   {
      if (!delegate.isLoggable(java.util.logging.Level.FINE)) return;
      delegate.logp(java.util.logging.Level.FINEST, classname, "", message, error);
   }

   @Override
   public boolean isDebugEnabled()
   {
      return delegate.isLoggable(java.util.logging.Level.FINE);
   }

   @Override
   public void debug(String message)
   {
      if (!delegate.isLoggable(java.util.logging.Level.FINE)) return;
      delegate.logp(java.util.logging.Level.FINE, classname, "", message);
   }

   @Override
   public void debug(String message, Object... params)
   {
      if (!delegate.isLoggable(java.util.logging.Level.FINE)) return;
      delegate.logp(java.util.logging.Level.FINE, classname, "", message, params);
   }

   @Override
   public void debug(String message, Throwable error)
   {
      if (!delegate.isLoggable(java.util.logging.Level.FINE)) return;
      delegate.logp(java.util.logging.Level.FINE, classname, "", message, error);
   }

   @Override
   public void info(String message)
   {
      if (!delegate.isLoggable(java.util.logging.Level.INFO)) return;
      delegate.logp(java.util.logging.Level.INFO, classname, "", message);
   }

   @Override
   public void info(String message, Object... params)
   {
      if (!delegate.isLoggable(java.util.logging.Level.INFO)) return;
      delegate.logp(java.util.logging.Level.INFO, classname, "", message, params);
   }

   @Override
   public void info(String message, Throwable error)
   {
      if (!delegate.isLoggable(java.util.logging.Level.INFO)) return;
      delegate.logp(java.util.logging.Level.INFO, classname, "", message, error);
   }

   @Override
   public void warn(String message)
   {
      if (!delegate.isLoggable(java.util.logging.Level.WARNING)) return;
      delegate.logp(java.util.logging.Level.WARNING, classname, "", message);
   }

   @Override
   public void warn(String message, Object... params)
   {
      if (!delegate.isLoggable(java.util.logging.Level.WARNING)) return;
      delegate.logp(java.util.logging.Level.WARNING, classname, "", message, params);
   }

   @Override
   public void warn(String message, Throwable error)
   {
      if (!delegate.isLoggable(java.util.logging.Level.WARNING)) return;
      delegate.logp(java.util.logging.Level.WARNING, classname, "", message, error);
   }

   @Override
   public void error(String message)
   {
      if (!delegate.isLoggable(java.util.logging.Level.SEVERE)) return;
      delegate.logp(java.util.logging.Level.SEVERE, classname, "", message);
   }

   @Override
   public void error(String message, Object... params)
   {
      if (!delegate.isLoggable(java.util.logging.Level.SEVERE)) return;
      delegate.logp(java.util.logging.Level.SEVERE, classname, "", message, params);
   }

   @Override
   public void error(String message, Throwable error)
   {
      if (!delegate.isLoggable(java.util.logging.Level.SEVERE)) return;
      delegate.logp(java.util.logging.Level.SEVERE, classname, "", message, error);
   }

   @Override
   public boolean isWarnEnabled()
   {
      return delegate.isLoggable(java.util.logging.Level.WARNING);
   }
}
