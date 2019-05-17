package org.jboss.resteasy.spi.statistics;


public interface MethodStatisticsLogger {

   /**
    * Retrieve current time
    * @return
    */
   default long timestamp(){
      // no-op
      return 0;
   }

   /**
    * Calculate lenght of time bewteen current time and provided time
    * @param fromTimestamp
    */
   default void duration(final long fromTimestamp) {
      // no-op
   }

   /**
    * Count reported failure of method invocation
    */
   default void incFailureCnt() {
      // no-op
   }

   /**
    * Reinitialize statistics
    */
   default void reset() {
      // no-op
   }

   default long getInvocationCnt() { return -1;}
   default long getFailedInvocationCnt() { return -1;}
   default long getAvgExecutionTime() { return -1;}
   default long getTotalExecutionTime() { return -1;}
}
