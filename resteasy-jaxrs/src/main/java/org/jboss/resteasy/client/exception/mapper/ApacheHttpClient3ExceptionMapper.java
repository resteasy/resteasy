package org.jboss.resteasy.client.exception.mapper;

import java.io.IOException;

import javax.ws.rs.ext.Provider;

import org.apache.commons.httpclient.CircularRedirectException;
import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.ConnectionPoolTimeoutException;
import org.apache.commons.httpclient.HttpContentTooLargeException;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpRecoverableException;
import org.apache.commons.httpclient.InvalidRedirectLocationException;
import org.apache.commons.httpclient.NoHttpResponseException;
import org.apache.commons.httpclient.ProtocolException;
import org.apache.commons.httpclient.RedirectException;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.auth.AuthChallengeException;
import org.apache.commons.httpclient.auth.AuthenticationException;
import org.apache.commons.httpclient.auth.CredentialsNotAvailableException;
import org.apache.commons.httpclient.auth.InvalidCredentialsException;
import org.apache.commons.httpclient.auth.MalformedChallengeException;
import org.apache.commons.httpclient.cookie.MalformedCookieException;
import org.jboss.resteasy.client.exception.ResteasyAuthChallengeException;
import org.jboss.resteasy.client.exception.ResteasyAuthenticationException;
import org.jboss.resteasy.client.exception.ResteasyCircularRedirectException;
import org.jboss.resteasy.client.exception.ResteasyClientException;
import org.jboss.resteasy.client.exception.ResteasyConnectTimeoutException;
import org.jboss.resteasy.client.exception.ResteasyConnectionPoolTimeoutException;
import org.jboss.resteasy.client.exception.ResteasyCredentialsNotAvailableException;
import org.jboss.resteasy.client.exception.ResteasyHttpContentTooLargeException;
import org.jboss.resteasy.client.exception.ResteasyHttpException;
import org.jboss.resteasy.client.exception.ResteasyHttpRecoverableException;
import org.jboss.resteasy.client.exception.ResteasyIOException;
import org.jboss.resteasy.client.exception.ResteasyInvalidCredentialsException;
import org.jboss.resteasy.client.exception.ResteasyInvalidRedirectLocationException;
import org.jboss.resteasy.client.exception.ResteasyMalformedChallengeException;
import org.jboss.resteasy.client.exception.ResteasyMalformedCookieException;
import org.jboss.resteasy.client.exception.ResteasyNoHttpResponseException;
import org.jboss.resteasy.client.exception.ResteasyProtocolException;
import org.jboss.resteasy.client.exception.ResteasyRedirectException;
import org.jboss.resteasy.client.exception.ResteasyURIException;
import org.jboss.resteasy.spi.ClientExceptionMapper;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Aug 1, 2012
 */
@SuppressWarnings("deprecation")
@Provider
public class ApacheHttpClient3ExceptionMapper implements ClientExceptionMapper<Exception>
{
   @Override
   public ResteasyClientException toException(Exception exception)
   {
      if (exception instanceof HttpException)
      {
         return mapHttpException(HttpException.class.cast(exception));
      }
      if (exception instanceof IOException)
      {
         return mapIOException(IOException.class.cast(exception));
      }
      return new ResteasyClientException("Unexpected exception type", exception);
   }

   private ResteasyClientException mapIOException(IOException e)
   {
      if (ConnectionPoolTimeoutException.class.equals(e.getClass()))
      {
         return new ResteasyConnectionPoolTimeoutException(e);
      }
      if (ConnectTimeoutException.class.equals(e.getClass()))
      {
         return new ResteasyConnectTimeoutException(e);
      }
      if (NoHttpResponseException.class.equals(e.getClass()))
      {
         return new ResteasyNoHttpResponseException(e);
      }
      return new ResteasyIOException("IOException", e);
   }
   
   @SuppressWarnings("deprecation")
   private ResteasyClientException mapHttpException(HttpException e)
   {
      if (AuthChallengeException.class.equals(e.getClass()))
      {
         return new ResteasyAuthChallengeException(e);
      }
      if (AuthenticationException.class.equals(e.getClass()))
      {
         return new ResteasyAuthenticationException(e);
      }
      if (CircularRedirectException.class.equals(e.getClass()))
      {
         return new ResteasyCircularRedirectException(e);
      }
      if (CredentialsNotAvailableException.class.equals(e.getClass()))
      {
         return new ResteasyCredentialsNotAvailableException(e);
      }
      if (InvalidCredentialsException.class.equals(e.getClass()))
      {
         return new ResteasyInvalidCredentialsException(e);
      }
      if (InvalidRedirectLocationException.class.equals(e.getClass()))
      {
         return new ResteasyInvalidRedirectLocationException(e);
      }
      if (HttpContentTooLargeException.class.equals(e.getClass()))
      {
         return new ResteasyHttpContentTooLargeException(e);
      }
      if (HttpRecoverableException.class.equals(e.getClass()))
      {
         return new ResteasyHttpRecoverableException(e);
      }
      if (MalformedChallengeException.class.equals(e.getClass()))
      {
         return new ResteasyMalformedChallengeException(e);
      }
      if (MalformedCookieException.class.equals(e.getClass()))
      {
         return new ResteasyMalformedCookieException(e);
      }
      if (ProtocolException.class.equals(e.getClass()))
      {
         return new ResteasyProtocolException(e);
      }
      if (RedirectException.class.equals(e.getClass()))
      {
         return new ResteasyRedirectException(e);
      }
      if (URIException.class.equals(e.getClass()))
      {
         return new ResteasyURIException(e);
      }
      return new ResteasyHttpException("HttpException", e);
   }
}
