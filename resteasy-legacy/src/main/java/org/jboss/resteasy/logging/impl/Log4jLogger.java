package org.jboss.resteasy.logging.impl;

import org.apache.log4j.Logger;

import java.text.MessageFormat;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
/**
 * @deprecated See RESTEASY-1578.
 */
@Deprecated
public class Log4jLogger extends org.jboss.resteasy.logging.Logger
{
   private transient Logger delegate;
   private String classname;

   public Log4jLogger(String classname)
   {
      this.classname = classname;
      delegate = Logger.getLogger(classname);
   }

   @Override
   public boolean isTraceEnabled()
   {
      return delegate.isTraceEnabled();
   }

   @Override
   public void trace(String message)
   {
      if (!delegate.isTraceEnabled()) return;
      delegate.trace(message);
   }

   @Override
   public void trace(String message, Object... params)
   {
      if (!delegate.isTraceEnabled()) return;
      String msg = MessageFormat.format(message, params);
      delegate.trace(msg);
   }

   @Override
   public void trace(String message, Throwable error)
   {
      if (!delegate.isTraceEnabled()) return;
      delegate.trace(message, error);
   }

   @Override
   public boolean isDebugEnabled()
   {
      return delegate.isDebugEnabled();
   }

   @Override
   public void debug(String message)
   {
      if (!delegate.isDebugEnabled()) return;
      delegate.debug(message);
   }

   @Override
   public void debug(String message, Object... params)
   {
      if (!delegate.isDebugEnabled()) return;
      String msg = MessageFormat.format(message, params);
      delegate.debug(msg);
   }

   @Override
   public void debug(String message, Throwable error)
   {
      if (!isDebugEnabled()) return;
      delegate.debug(message, error);
   }

   @Override
   public void info(String message)
   {
      if (!(delegate.isInfoEnabled())) return;
      delegate.info(message);
   }

   @Override
   public void info(String message, Object... params)
   {
      if (!delegate.isInfoEnabled()) return;
      String msg = MessageFormat.format(message, params);
      delegate.info(msg);
   }

   @Override
   public void info(String message, Throwable error)
   {
      if (!delegate.isInfoEnabled()) return;
      delegate.info(message, error);
   }

   @Override
   public void warn(String message)
   {
      delegate.warn(message);
   }

   @Override
   public void warn(String message, Object... params)
   {
      String msg = MessageFormat.format(message, params);
      delegate.warn(msg);
   }

   @Override
   public void warn(String message, Throwable error)
   {
      delegate.warn(message, error);
   }

   @Override
   public void error(String message)
   {
      delegate.warn(message);
   }

   @Override
   public void error(String message, Object... params)
   {
      String msg = MessageFormat.format(message, params);
      delegate.error(msg);
   }

   @Override
   public void error(String message, Throwable error)
   {
      delegate.warn(message, error);
   }

   @Override
   public boolean isWarnEnabled()
   {
      return true;
   }

}
