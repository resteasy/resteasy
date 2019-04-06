package org.jboss.resteasy.utils;

/**
 * Counter for log messages in log file.
 */
public class LogCounter {
   /**
    * Examined log message
    */
   private String message;

   /**
    * Initial count of examined log message
    */
   private int initCount;

   /**
    * Log file is accessed differently if test is run on server, or as client
    */
   private boolean onServer;

   /**
    * Whether to use regexp or just plain String message
    */
   private boolean useRegexp = false;


   /**
    * Container qualifier when arquillian starts multiple instance, null otherwise.
    */
   private String containerQualifier;


   public LogCounter(final String message, final boolean onServer, final String containerQualifier) {
      this(message, onServer, containerQualifier, false);
   }

   public LogCounter(final String message, final boolean onServer, final String containerQualifier, final boolean useRegexp) {
      this.message = message;
      this.onServer = onServer;
      this.containerQualifier = containerQualifier;
      this.useRegexp = useRegexp;
      this.initCount = TestUtil.getWarningCount(message, onServer, containerQualifier, useRegexp);
   }

   public LogCounter(final String message, final boolean onServer) {
      this(message, onServer, null);
   }

   /**
    * Get count of examined log message, logged after creation of this LogCounter
    */
   public int count() {
      return TestUtil.getWarningCount(message, onServer, containerQualifier, useRegexp) - initCount;
   }
}
