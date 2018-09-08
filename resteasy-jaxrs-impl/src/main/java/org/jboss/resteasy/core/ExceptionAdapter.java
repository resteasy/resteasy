package org.jboss.resteasy.core;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * A ExceptionAdapter.
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
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
    * @param e exception
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
    * Prints stack trace.
    *
    * @see java.lang.Throwable#printStackTrace()
    */
   //CHECKSTYLE.OFF: RegexpSinglelineJava
   public void printStackTrace()
   {
      printStackTrace(System.err);
   }
   //CHECKSTYLE.ON: RegexpSinglelineJava

   /**
    * Prints stack trace.
    *
    * @param s print stream
    * @see java.lang.Throwable#printStackTrace(java.io.PrintStream)
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
    * Prints stack trace.
    *
    * @param s print writer
    * @see java.lang.Throwable#printStackTrace(java.io.PrintWriter)
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
    * @throws Exception exception
    */
   public void rethrow() throws Exception
   {
      throw this.originalException;
   }
}
