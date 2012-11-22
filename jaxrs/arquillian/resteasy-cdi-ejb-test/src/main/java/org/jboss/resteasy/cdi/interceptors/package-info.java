/**
 * This package (along with org.jboss.resteasy.test.cdi.interceptors) tests the application
 * of interceptors to:
 * 
 * *) ContainerRequestFilter
 * *) ContainerResponseFilter
 * *) ReaderInterceptor
 * *) MessageBodyReader
 * *) WriterInterceptor
 * *) MessageBodyWriter
 * *) Resources
 * 
 * There are class level interceptors and method level interceptors.  Some of them are bound through
 * the use of the @Interceptors annotation, and some are bound through CDI interceptor binding types.
 */
package org.jboss.resteasy.cdi.interceptors;


