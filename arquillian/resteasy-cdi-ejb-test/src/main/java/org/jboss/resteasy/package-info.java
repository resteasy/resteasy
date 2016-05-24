/**
 * This test suite is designed to exercise various interactions between Resteasy, CDI, and EJBs.
 * Among the topics covered are:
 * 
 *    I. Injection into JAX-RS
 *      
 *       A. @see org.jboss.resteasy.cdi.injection, @see org.jboss.resteasy.test.cdi.injection
 *       B. Loggers, integers, BeanManagers, EntityManagers, SLSBs, SFSBs, JMS objects
 *       C. Injected into JAX-RS resources, MessageBodyReaders, MessageBodyWriters, ReaderInterceptors, WriterInterceptors
 *       D. Using @Inject, @EJB 
 *       E. Qualifiers
 *       F. Issue: https://community.jboss.org/thread/213568?start=15&tstart=0
 *    
 *   II. Injection from JAX-RS
 *      
 *       A. @see org.jboss.resteasy.cdi.injection.reverse, @see org.jboss.resteasy.test.cdi.injection.reverse
 *       B. Of JAX-RS resources, MessageBodyReaders, MessageBodyWriters
 *       C. Into SLSBs, SFSBs
 *       
 *  III. Generic types
 * 
 *       A. @see org.jboss.resteasy.cdi.generic, @see org.jboss.resteasy.test.cdi.generic
 *       B. Generically typed beans are injected into JAX-RS resources and decorators, testing
 *          upper and lower bounded wildcard types.
 *       C. Issue: https://community.jboss.org/message/784561#784561
 *
 *   IV. EJBs
 * 
 *       A. @see org.jboss.resteasy.cdi.ejb, @see org.jboss.resteasy.test.cdi.ejb
 *       B. SLSBs and SFSBs are used as JAX-RS resources, MessageBodyReaders, and MessageBodyWriters
 *      
 *    V. Interceptors
 *      
 *       A. @see org.jboss.resteasy.cdi.interceptors, @see org.jboss.resteasy.test.cdi.interceptors
 *       B. On JAX-RS resources, MessageBodyReaders, MessageBodyWriters,
 *          ReaderInterceptors, WriterInterceptors, ContainerRequestFilters, ContainerResponseFilters
 *       C. Using @Interceptors, @InterceptorBinding
 *       D. Intercepting business methods and lifecycle methods
 *       E. Injecting SessionContext into JAX-RS resource to test EJB timer service
 *  
 *   VI. Decorators
 *      
 *       A. @see org.jboss.resteasy.cdi.decorators, @see org.jboss.resteasy.test.cdi.decorators
 *       B. On JAX-RS resouces, MessageBodyReaders, MessageBodyWriters,
 *          ReaderInterceptors, WriterInterceptors, ContainerRequestFilters, ContainerResponseFilters
 *       C. Issue: https://community.jboss.org/message/784561#784561
 *         
 *  VII. Events
 *      
 *       A. @see org.jboss.resteasy.cdi.events, @see org.jboss.resteasy.test.cdi.events
 *       B. Injected into and fired by JAX-RS resources, MessageBodyReaders, MessageBodyWriters,
 *          ReaderInterceptors, WriterInterceptors
 *       C. Observed by an JAX-RS component (MessageBodyReader)
 *       D. Events injected and observed by EJBs are tested as well
 *          1. @see org.jboss.resteasy.cdi.events.ejb, @see org.jboss.resteasy.test.cdi.events.ejb
 *         
 * VIII. Inheritence
 *      
 *       A. @see org.jboss.resteasy.cdi.inheritence, @see org.jboss.resteasy.test.cdi.inheritence
 *       B. Alternatives and specialized beans are injected into a JAX-RS resource.
 *       C. Issue: https://community.jboss.org/message/780466
 *  
 *   IX. Asynchronous processing
 *       A. @see org.jboss.resteasy.cdi.asynch, @see org.jboss.resteasy.test.cdi.asynch 
 *       B. JAX-RS and EJB asynchronous processing are tested alone and together.
 *      
 *    X. Extensions - bean
 *       A. @see org.jboss.resteasy.cdi.extension.bean, @see org.jboss.resteasy.test.cdi.extension.bean
 *       B. An application defined implementation of javax.enterprise.inject.spi.Bean is introduced.
 *       
 *   XI. Extensions - scope
 *       A. @see org.jboss.resteasy.cdi.extension.scope, @see org.jboss.resteasy.test.cdi.extension.scope
 *       B. An application defined scope is introduced.
 *       
 *  XII. Modularity
 *       A. @see org.jboss.resteasy.cdi.modules, @see org.jboss.resteasy.test.cdi.modules
 *       B. Injection of an SFSB into a JAX-RS resource is tested across a variety of jar, ejb-jar, war,
 *          and ear boundaries.
 *       
 * XIII. Validation
 *       A. @see org.jboss.resteasy.cdi.validation, @see org.jboss.resteasy.test.cdi.validation
 *       B. The "experimental" validation facility (which was specified in a provisional JAX-RS 2.0 spec
 *          and then removed) is tested.
 *       C. Issue: https://community.jboss.org/thread/217515
 *        
 *        
 * Note. These tests run in a version of AS7 with Resteasy 3.0-beta-2, with two modifications:
 * 
 *       1. Weld 1.2.0-SNAPSHOT with a fix for WELD-1174.
 *       2. resteasy-jaxrs 3.0-alpha-1-SNAPSHOT with some modifications to the Bean Validation facility..
 *       
 * This version of AS7 is downloaded by maven from https://repository.jboss.org/nexus, where it is
 * identified as org.jboss.resteasy.test:as7-dist-cdi-ejb-7.1.1.Final:1.0:jar.
 */
package org.jboss.resteasy;


