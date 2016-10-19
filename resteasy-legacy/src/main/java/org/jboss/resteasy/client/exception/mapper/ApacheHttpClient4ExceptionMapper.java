package org.jboss.resteasy.client.exception.mapper;

import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpException;
import org.apache.http.MalformedChunkCodingException;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.NoHttpResponseException;
import org.apache.http.ProtocolException;
import org.apache.http.UnsupportedHttpVersionException;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.InvalidCredentialsException;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.client.CircularRedirectException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.NonRepeatableRequestException;
import org.apache.http.client.RedirectException;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.cookie.CookieRestrictionViolationException;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.impl.auth.NTLMEngineException;
import org.apache.http.impl.client.TunnelRefusedException;
import org.jboss.resteasy.client.exception.ResteasyAuthenticationException;
import org.jboss.resteasy.client.exception.ResteasyCircularRedirectException;
import org.jboss.resteasy.client.exception.ResteasyClientException;
import org.jboss.resteasy.client.exception.ResteasyClientProtocolException;
import org.jboss.resteasy.client.exception.ResteasyConnectTimeoutException;
import org.jboss.resteasy.client.exception.ResteasyConnectionClosedException;
import org.jboss.resteasy.client.exception.ResteasyConnectionPoolTimeoutException;
import org.jboss.resteasy.client.exception.ResteasyCookieRestrictionViolationException;
import org.jboss.resteasy.client.exception.ResteasyHttpException;
import org.jboss.resteasy.client.exception.ResteasyHttpHostConnectException;
import org.jboss.resteasy.client.exception.ResteasyIOException;
import org.jboss.resteasy.client.exception.ResteasyInvalidCredentialsException;
import org.jboss.resteasy.client.exception.ResteasyMalformedChallengeException;
import org.jboss.resteasy.client.exception.ResteasyMalformedChunkCodingException;
import org.jboss.resteasy.client.exception.ResteasyMalformedCookieException;
import org.jboss.resteasy.client.exception.ResteasyMethodNotSupportedException;
import org.jboss.resteasy.client.exception.ResteasyNTLMEngineException;
import org.jboss.resteasy.client.exception.ResteasyNoHttpResponseException;
import org.jboss.resteasy.client.exception.ResteasyNonRepeatableRequestException;
import org.jboss.resteasy.client.exception.ResteasyProtocolException;
import org.jboss.resteasy.client.exception.ResteasyRedirectException;
import org.jboss.resteasy.client.exception.ResteasyTunnelRefusedException;
import org.jboss.resteasy.client.exception.ResteasyUnsupportedHttpVersionException;

import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Jul 28, 2012
 */
@Provider
public class ApacheHttpClient4ExceptionMapper implements ClientExceptionMapper<Exception>
{
   @Override
   public ResteasyClientException toException(Exception exception)
   {
      if (exception instanceof IOException)
      {
         return mapIOException(IOException.class.cast(exception));
      }
      if (exception instanceof HttpException)
      {
         return mapHttpException(HttpException.class.cast(exception));
      }
      return new ResteasyClientException("Unexpected exception type", exception);
   }

   private ResteasyClientException mapIOException(IOException e)
   {
      if (ClientProtocolException.class.equals(e.getClass()))
      {
         return new ResteasyClientProtocolException(e);
      }
      if (ConnectionClosedException.class.equals(e.getClass()))
      {
         return new ResteasyConnectionClosedException(e);
      }
      if (ConnectionPoolTimeoutException.class.equals(e.getClass()))
      {
         return new ResteasyConnectionPoolTimeoutException(e);
      }
      if (ConnectTimeoutException.class.equals(e.getClass()))
      {
         return new ResteasyConnectTimeoutException(e);
      }
      if (HttpHostConnectException.class.equals(e.getClass()))
      {
         return new ResteasyHttpHostConnectException(e);
      }
      if (MalformedChunkCodingException.class.equals(e.getClass()))
      {
         return new ResteasyMalformedChunkCodingException(e);
      }
      if (NoHttpResponseException.class.equals(e.getClass()))
      {
         return new ResteasyNoHttpResponseException(e);
      }
      if (NoHttpResponseException.class.equals(e.getClass()))
      {
         return new ResteasyNoHttpResponseException(e);
      }
      return new ResteasyIOException("IOException", e);
   }
   
   private ResteasyClientException mapHttpException(HttpException e)
   {
      if (AuthenticationException.class.equals(e.getClass()))
      {
         return new ResteasyAuthenticationException(e);
      }
      if (CircularRedirectException.class.equals(e.getClass()))
      {
         return new ResteasyCircularRedirectException(e);
      }
      if (CookieRestrictionViolationException.class.equals(e.getClass()))
      {
         return new ResteasyCookieRestrictionViolationException(e);
      }
      if (InvalidCredentialsException.class.equals(e.getClass()))
      {
         return new ResteasyInvalidCredentialsException(e);
      }
      if (MalformedChallengeException.class.equals(e.getClass()))
      {
         return new ResteasyMalformedChallengeException(e);
      }
      if (MalformedCookieException.class.equals(e.getClass()))
      {
         return new ResteasyMalformedCookieException(e);
      }
      if (MethodNotSupportedException.class.equals(e.getClass()))
      {
         return new ResteasyMethodNotSupportedException(e);
      }
      if (NonRepeatableRequestException.class.equals(e.getClass()))
      {
         return new ResteasyNonRepeatableRequestException(e);
      }
      if (NTLMEngineException.class.equals(e.getClass()))
      {
         return new ResteasyNTLMEngineException(e);
      }
      if (ProtocolException.class.equals(e.getClass()))
      {
         return new ResteasyProtocolException(e);
      }
      if (RedirectException.class.equals(e.getClass()))
      {
         return new ResteasyRedirectException(e);
      }
      if (TunnelRefusedException.class.equals(e.getClass()))
      {
         return new ResteasyTunnelRefusedException(e);
      }
      if (UnsupportedHttpVersionException.class.equals(e.getClass()))
      {
         return new ResteasyUnsupportedHttpVersionException(e);
      }
      return new ResteasyHttpException("HttpException", e);
   }
}
