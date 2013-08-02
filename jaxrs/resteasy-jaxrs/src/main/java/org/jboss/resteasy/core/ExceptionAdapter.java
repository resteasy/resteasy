package org.jboss.resteasy.core;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * A ExceptionAdapter.
 *
 * @author <a href="ryan@damnhandy.com>Ryan J. McDonough</a>
 * @version $Revision: $
 */
public class ExceptionAdapter extends RuntimeException
{

   /**
    * The serialVersionUID
    */
   private static final long serialVersionUID = 6628087350457915908L;

   private final String stackTrace;

   /**
    *
    */
   private Exception originalException;

   /**
    * Create a new ExceptionAdapter.
    *
    * @param e
    */
   public ExceptionAdapter(Exception e)
   {
      this(e.getMessage(), e);
   }

   public ExceptionAdapter(String message, Exception e)
   {
      super(new StringBuilder(message == null ? "" : message).append(" : ").append(e.getMessage()).toString());
      originalException = e;
      StringWriter sw = new StringWriter();
      e.printStackTrace(new PrintWriter(sw));
      stackTrace = sw.toString();
   }

   /**
    * FIXME Comment this
    *
    * @see @see java.lang.Throwable#printStackTrace()
    */
   public void printStackTrace()
   {
      printStackTrace(System.err);
   }

   /**
    * FIXME Comment this
    *
    * @param s
    * @see @see java.lang.Throwable#printStackTrace(java.io.PrintStream)
    */
   public void printStackTrace(java.io.PrintStream s)
   {
      synchronized (s)
      {
         s.printf("%s: ", getClass().getName());
         s.print(stackTrace);
      }
   }

   /**
    * FIXME Comment this
    *
    * @param s
    * @see @see java.lang.Throwable#printStackTrace(java.io.PrintWriter)
    */
   public void printStackTrace(java.io.PrintWriter s)
   {
      synchronized (s)
      {
         s.print(getClass().getName() + ": ");
         s.print(stackTrace);
      }
   }


   /**
    * Rethrows the original exception class.
    *
    * @throws Exception
    */
   public void rethrow() throws Exception
   {
      throw this.originalException;
   }
}
