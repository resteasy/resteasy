package org.jboss.resteasy.test.asynch.resource;

import javax.ws.rs.container.CompletionCallback;

public class CallbackSecondSettingCompletionCallback implements CompletionCallback {

   private static String throwableName;
   public static final String NULL = "NULL";
   public static final String OUTOFORDER = "CallbackSecondSettingCompletionCallback is not second";
   public static final String NONAME = "No name has been set yet";

   @Override
   public void onComplete(Throwable throwable) {
      throwableName = throwable == null ? NULL : throwable.getClass()
            .getName();
      if (!CallbackSettingCompletionCallback.getLastThrowableName().equals(throwableName)) {
         throwableName = throwableName + OUTOFORDER;
      }
   }

   public static final String getLastThrowableName() {
      return throwableName;
   }

   public static final void resetLastThrowableName() {
      throwableName = NONAME;
   }

}
