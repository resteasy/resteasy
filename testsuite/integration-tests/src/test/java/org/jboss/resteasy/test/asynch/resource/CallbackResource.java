package org.jboss.resteasy.test.asynch.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;

@Path("resource")
public class CallbackResource extends CallbackResourceBase {

   @GET
   @Path("register")
   public String registerObject(@QueryParam("stage") String stage) {
      AsyncResponse async = takeAsyncResponse(stage);
      boolean b = async.register(new CallbackSettingCompletionCallback()).isEmpty();
      addResponse(async, stage);
      return b ? TRUE : FALSE;
   }

   @GET
   @Path("registerclass")
   public String registerClass(@QueryParam("stage") String stage) {
      AsyncResponse async = takeAsyncResponse(stage);
      boolean b = async.register(CallbackSettingCompletionCallback.class).isEmpty();
      addResponse(async, stage);
      return b ? TRUE : FALSE;
   }

   @GET
   @Path("registerobjects")
   public String registerObjectObject(@QueryParam("stage") String stage) {
      AsyncResponse async = takeAsyncResponse(stage);
      boolean b = async.register(new CallbackSettingCompletionCallback(),
            new CallbackSecondSettingCompletionCallback()).isEmpty();
      addResponse(async, stage);
      return b ? TRUE : FALSE;
   }

   @GET
   @Path("registerclasses")
   public String registerClasses(@QueryParam("stage") String stage) {
      AsyncResponse async = takeAsyncResponse(stage);
      boolean b = async.register(CallbackSettingCompletionCallback.class,
            CallbackSecondSettingCompletionCallback.class).isEmpty();
      addResponse(async, stage);
      return b ? TRUE : FALSE;
   }

   @GET
   @Path("registerthrows")
   public String registerObjectThrowsNpe(@QueryParam("stage") String stage) {
      AsyncResponse async = takeAsyncResponse(stage);
      try {
         CallbackSettingCompletionCallback callback = null;
         async.register(callback);
      } catch (NullPointerException e) {
         return TRUE;
      } catch (Exception e) {
         return "Threw " + e.getClass().getName();
      }
      return FALSE;
   }

   @GET
   @Path("registerclassthrows")
   public String registerClassThrowsNpe(@QueryParam("stage") String stage) {
      AsyncResponse async = takeAsyncResponse(stage);
      try {
         Class<CallbackSettingCompletionCallback> callback = null;
         async.register(callback);
      } catch (NullPointerException e) {
         return TRUE;
      } catch (Exception e) {
         return "Threw " + e.getClass().getName();
      }
      return FALSE;
   }

   @GET
   @Path("registerobjectsthrows1")
   public String registerObjectsThrowsNpe1(@QueryParam("stage") String stage) {
      AsyncResponse async = takeAsyncResponse(stage);
      try {
         CallbackSettingCompletionCallback callback = null;
         async.register(callback, new CallbackSecondSettingCompletionCallback());
      } catch (NullPointerException e) {
         return TRUE;
      } catch (Exception e) {
         return "Threw " + e.getClass().getName();
      }
      return FALSE;
   }

   @GET
   @Path("registerobjectsthrows2")
   public String registerObjectsThrowsNpe2(@QueryParam("stage") String stage) {
      AsyncResponse async = takeAsyncResponse(stage);
      try {
         CallbackSecondSettingCompletionCallback callback = null;
         async.register(new CallbackSettingCompletionCallback(), callback);
      } catch (NullPointerException e) {
         return TRUE;
      } catch (Exception e) {
         return "Threw " + e.getClass().getName();
      }
      return FALSE;
   }

   @GET
   @Path("registerclassesthrows1")
   public String registerClassesThrowsNpe1(@QueryParam("stage") String stage) {
      AsyncResponse async = takeAsyncResponse(stage);
      try {
         Class<CallbackSettingCompletionCallback> callback = null;
         async.register(callback, CallbackSecondSettingCompletionCallback.class);
      } catch (NullPointerException e) {
         return TRUE;
      } catch (Exception e) {
         return "Threw " + e.getClass().getName();
      }
      return FALSE;
   }

   @GET
   @Path("registerclassesthrows2")
   public String registerClassesThrowsNpe2(@QueryParam("stage") String stage) {
      AsyncResponse async = takeAsyncResponse(stage);
      try {
         Class<CallbackSecondSettingCompletionCallback> callback = null;
         async.register(CallbackSettingCompletionCallback.class, callback);
      } catch (NullPointerException e) {
         return TRUE;
      } catch (Exception e) {
         return "Threw " + e.getClass().getName();
      }
      return FALSE;
   }

   @GET
   @Path("exception")
   public String throwExceptionOnAsyncResponse(
            @QueryParam("stage") String stage) {
      AsyncResponse async = takeAsyncResponse(stage);
      boolean b = async.resume(new CallbackExceptionThrowingStringBean(
            "throw exception"));
      addResponse(async, stage);
      return b ? TRUE : FALSE;
   }

   @GET
   @Path("error")
   public String getErrorValue() {
      String name = CallbackSettingCompletionCallback.getLastThrowableName();
      return name;
   }

   @GET
   @Path("seconderror")
   public String getSecondErrorValue() {
      String name = CallbackSecondSettingCompletionCallback.getLastThrowableName();
      return name;
   }

   @GET
   @Path("reset")
   public void resetErrorValue() {
      CallbackSettingCompletionCallback.resetLastThrowableName();
      CallbackSecondSettingCompletionCallback.resetLastThrowableName();
   }
}
