package org.jboss.resteasy.test.client.exception.resource;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.NotAllowedException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.NotSupportedException;
import javax.ws.rs.RedirectionException;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.ServiceUnavailableException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.jboss.resteasy.client.exception.ResteasyBadRequestException;
import org.jboss.resteasy.client.exception.ResteasyClientErrorException;
import org.jboss.resteasy.client.exception.ResteasyForbiddenException;
import org.jboss.resteasy.client.exception.ResteasyInternalServerErrorException;
import org.jboss.resteasy.client.exception.ResteasyNotAcceptableException;
import org.jboss.resteasy.client.exception.ResteasyNotAllowedException;
import org.jboss.resteasy.client.exception.ResteasyNotAuthorizedException;
import org.jboss.resteasy.client.exception.ResteasyNotFoundException;
import org.jboss.resteasy.client.exception.ResteasyNotSupportedException;
import org.jboss.resteasy.client.exception.ResteasyRedirectionException;
import org.jboss.resteasy.client.exception.ResteasyServerErrorException;
import org.jboss.resteasy.client.exception.ResteasyServiceUnavailableException;
import org.jboss.resteasy.client.exception.ResteasyWebApplicationException;
import org.jboss.resteasy.utils.PortProviderUtil;

public class ClientWebApplicationExceptionExceptions {

   public  static Response commonResponse = Response.ok("msg").status(444).header("foo", "bar").build();

   private static ResponseBuilder commonBuilder = Response.ok("msg").header("foo", "bar");

   public static WebApplicationException[] oldExceptions = {
         // The first four WebApplicationExceptions test the four WebApplicationException constructors
         // that take a Response parameter.
         new WebApplicationException(commonResponse),
         new WebApplicationException("msg", commonResponse),
         new WebApplicationException(new Exception(), commonResponse),
         new WebApplicationException("msg", new Exception(), commonResponse),

         // The other WebApplicationExceptions test the WebApplicationException subclasses that are
         // thrown according to the status used. The relationship between status and subclass is given
         // in oldExceptionMap.
         new WebApplicationException(commonBuilder.status(333).build()),
         new WebApplicationException(commonBuilder.status(400).build()),
         new WebApplicationException(commonBuilder.status(401).build()),
         new WebApplicationException(commonBuilder.status(403).build()),
         new WebApplicationException(commonBuilder.status(404).build()),
         new WebApplicationException(commonBuilder.status(405).build()),
         new WebApplicationException(commonBuilder.status(406).build()),
         new WebApplicationException(commonBuilder.status(415).build()),
         new WebApplicationException(commonBuilder.status(500).build()),
         new WebApplicationException(commonBuilder.status(503).build()),
         new WebApplicationException(commonBuilder.status(555).build()),
   };

   public static Map<Integer, Class<?>> oldExceptionMap = new HashMap<Integer, Class<?>>();
   static {
      oldExceptionMap.put(333, RedirectionException.class);
      oldExceptionMap.put(400, BadRequestException.class);
      oldExceptionMap.put(401, NotAuthorizedException.class);
      oldExceptionMap.put(403, ForbiddenException.class);
      oldExceptionMap.put(404, NotFoundException.class);
      oldExceptionMap.put(405, NotAllowedException.class);
      oldExceptionMap.put(406, NotAcceptableException.class);
      oldExceptionMap.put(415, NotSupportedException.class);
      oldExceptionMap.put(444, ClientErrorException.class);
      oldExceptionMap.put(500, InternalServerErrorException.class);
      oldExceptionMap.put(503, ServiceUnavailableException.class);
      oldExceptionMap.put(555, ServerErrorException.class);
   }

   public static WebApplicationException[] newExceptions = {
         // The first four ResteasyWebApplicationExceptions test the four ResteasyWebApplicationException
         // constructors that take a Response parameter.
         new ResteasyWebApplicationException(oldExceptions[0]),
         new ResteasyWebApplicationException(oldExceptions[1]),
         new ResteasyWebApplicationException(oldExceptions[2]),
         new ResteasyWebApplicationException(oldExceptions[3]),

         // The other ResteasyWebApplicationExceptions test the ResteasyWebApplicationExceptions subclasses
         // that are thrown according to the status used. The relationship between status and subclass is given
         // in newExceptionMap.
         new ResteasyWebApplicationException(oldExceptions[4]),
         new ResteasyWebApplicationException(oldExceptions[5]),
         new ResteasyWebApplicationException(oldExceptions[6]),
         new ResteasyWebApplicationException(oldExceptions[7]),
         new ResteasyWebApplicationException(oldExceptions[8]),
         new ResteasyWebApplicationException(oldExceptions[9]),
         new ResteasyWebApplicationException(oldExceptions[10]),
         new ResteasyWebApplicationException(oldExceptions[11]),
         new ResteasyWebApplicationException(oldExceptions[12]),
         new ResteasyWebApplicationException(oldExceptions[13]),
         new ResteasyWebApplicationException(oldExceptions[14]),
   };

   public static Map<Integer, Class<?>> newExceptionMap = new HashMap<Integer, Class<?>>();
   static {
      newExceptionMap.put(333, ResteasyRedirectionException.class);
      newExceptionMap.put(400, ResteasyBadRequestException.class);
      newExceptionMap.put(401, ResteasyNotAuthorizedException.class);
      newExceptionMap.put(403, ResteasyForbiddenException.class);
      newExceptionMap.put(404, ResteasyNotFoundException.class);
      newExceptionMap.put(405, ResteasyNotAllowedException.class);
      newExceptionMap.put(406, ResteasyNotAcceptableException.class);
      newExceptionMap.put(415, ResteasyNotSupportedException.class);
      newExceptionMap.put(444, ResteasyClientErrorException.class);
      newExceptionMap.put(500, ResteasyInternalServerErrorException.class);
      newExceptionMap.put(503, ResteasyServiceUnavailableException.class);
      newExceptionMap.put(555, ResteasyServerErrorException.class);
   }

   public static String generateURL(String path) {
      return PortProviderUtil.generateURL(path, "ClientWebApplicationExceptionTest");
   }
}
