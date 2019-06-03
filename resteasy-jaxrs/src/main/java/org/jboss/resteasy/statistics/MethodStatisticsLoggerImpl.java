package org.jboss.resteasy.statistics;

import org.jboss.resteasy.spi.statistics.MethodStatisticsLogger;

public class MethodStatisticsLoggerImpl implements MethodStatisticsLogger {
   private volatile long invocationCnt = 0;
   private volatile long failureCnt = 0;
   private volatile long totalExecutionTime = 0;

   @Override
   public long timestamp(){
      return System.nanoTime();
   }

   @Override
   public void duration(final long fromTimestamp) {
      // invocation count and execution time are related.
      // Set them together so avgExecutionTime will be calculated correctly
      synchronized (this)
      {
         ++invocationCnt;
         totalExecutionTime += (System.nanoTime() - fromTimestamp);
      }
   }

   @Override
   public void incFailureCnt() {
      ++failureCnt;
   }

   @Override
   public void reset() {
      synchronized (this)
      {
         invocationCnt = 0;
         failureCnt = 0;
         totalExecutionTime = 0;
      }
   }

   @Override
   public long getInvocationCnt() {
      return invocationCnt;
   }

   @Override
   public long getFailedInvocationCnt() {
      return failureCnt;
   }

   @Override
   public long getAvgExecutionTime() {
      long avgExecTime = -1;

      synchronized (this)
      {
         try
         {
            avgExecTime = totalExecutionTime / invocationCnt;
         } catch (Exception e)
         {

            if (invocationCnt == 0 && totalExecutionTime == 0)
            {
               avgExecTime = 0;
            }
         }
      }
      return avgExecTime;
   }

   @Override
   public long getTotalExecutionTime() {
      return totalExecutionTime;
   }
}
