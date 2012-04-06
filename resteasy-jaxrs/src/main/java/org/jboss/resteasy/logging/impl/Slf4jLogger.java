package org.jboss.resteasy.logging.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Slf4jLogger extends org.jboss.resteasy.logging.Logger
{
   private transient Logger delegate;
   private String classname;

   public Slf4jLogger(String classname)
   {
      delegate = LoggerFactory.getLogger(classname);
      this.classname = classname;
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
      if (!delegate.isDebugEnabled()) return;
      delegate.debug(message, error);
   }

   @Override
   public void info(String message)
   {
      if (!delegate.isInfoEnabled()) return;
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
      if (!delegate.isWarnEnabled()) return;
      delegate.warn(message);
   }

   @Override
   public void warn(String message, Object... params)
   {
      if (!delegate.isWarnEnabled()) return;
      String msg = MessageFormat.format(message, params);
      delegate.warn(msg);
   }

   @Override
   public void warn(String message, Throwable error)
   {
      if (!delegate.isWarnEnabled()) return;
      delegate.warn(message, error);
   }

   @Override
   public void error(String message)
   {
      if (!delegate.isErrorEnabled()) return;
      delegate.error(message);
   }

   @Override
   public void error(String message, Object... params)
   {
      if (!delegate.isErrorEnabled()) return;
      String msg = MessageFormat.format(message, params);
      delegate.error(msg);
   }

   @Override
   public void error(String message, Throwable error)
   {
      if (!delegate.isErrorEnabled()) return;
      delegate.error(message, error);
   }

   @Override
   public boolean isWarnEnabled()
   {
      return delegate.isWarnEnabled();
   }

}
