package org.jboss.resteasy.test.core.basic;

import java.util.Collection;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.test.core.basic.resource.AnnotationInheritanceGenericsAbstract;
import org.jboss.resteasy.test.core.basic.resource.AnnotationInheritanceGenericsEntity;
import org.jboss.resteasy.test.core.basic.resource.AnnotationInheritanceGenericsImpl;
import org.jboss.resteasy.test.core.basic.resource.AnnotationInheritanceGenericsInterface;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Configuration
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for JAX-RS annotation inheritance with generics.
 * @tpSince RESTEasy 4.0.0
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class AnnotationInheritanceGenericsTest {

    private static final String TEST_NAME = AnnotationInheritanceGenericsTest.class.getSimpleName();

    @Deployment
    public static Archive<?> deploySimpleResource() {
        WebArchive war = TestUtil.prepareArchive(TEST_NAME);
        war.addClasses(
                AnnotationInheritanceGenericsEntity.class,
                AnnotationInheritanceGenericsInterface.class,
                AnnotationInheritanceGenericsAbstract.class);

        return TestUtil.finishContainerPrepare(war, null, AnnotationInheritanceGenericsImpl.class);
    }

    protected Client client;

    @BeforeEach
    public void beforeTest() {
        client = ClientBuilder.newClient();
    }

    @AfterEach
    public void afterTest() {
        client.close();
        client = null;
    }

    @Test
    public void testGetCollectionInterface() {
        final Collection<AnnotationInheritanceGenericsEntity> entityList = invokeRequest(
                AnnotationInheritanceGenericsImpl.class,
                null,
                HttpMethod.GET,
                null,
                new GenericType<Collection<AnnotationInheritanceGenericsEntity>>() {
                });

        Assertions.assertNotNull(entityList, "Response entity list must not be null");
        Assertions.assertEquals(1, entityList.size(), "Response entity list must contain exactly one element");

        final AnnotationInheritanceGenericsEntity entity = entityList.iterator().next();
        Assertions.assertEquals(AnnotationInheritanceGenericsImpl.METHOD_ID_INTERFACE_GET_COLLECTION,
                entity.getId(),
                "Response entity ID must match method id");
    }

    @Test
    public void testGetSingleInterface() {
        final AnnotationInheritanceGenericsEntity entity = invokeRequest(
                AnnotationInheritanceGenericsImpl.class,
                "1",
                HttpMethod.GET,
                null,
                new GenericType<AnnotationInheritanceGenericsEntity>() {
                });

        Assertions.assertNotNull(entity, "Response entity must not be null");
        Assertions.assertEquals(AnnotationInheritanceGenericsImpl.METHOD_ID_INTERFACE_GET_SINGLE,
                entity.getId(), "Response entity ID must match method id");
    }

    @Test
    public void testPostInterface() {
        final AnnotationInheritanceGenericsEntity requestEntity = new AnnotationInheritanceGenericsEntity();

        final AnnotationInheritanceGenericsEntity entity = invokeRequest(
                AnnotationInheritanceGenericsImpl.class,
                null,
                HttpMethod.POST,
                requestEntity,
                new GenericType<AnnotationInheritanceGenericsEntity>() {
                });

        Assertions.assertNotNull(entity, "Response entity must not be null");
        Assertions.assertEquals(AnnotationInheritanceGenericsImpl.METHOD_ID_INTERFACE_POST,
                entity.getId(),
                "Response entity ID must match method id");
    }

    @Test
    public void testPutInterface() {
        final AnnotationInheritanceGenericsEntity requestEntity = new AnnotationInheritanceGenericsEntity();

        final AnnotationInheritanceGenericsEntity entity = invokeRequest(
                AnnotationInheritanceGenericsImpl.class,
                "1",
                HttpMethod.PUT,
                requestEntity,
                new GenericType<AnnotationInheritanceGenericsEntity>() {
                });

        Assertions.assertNotNull(entity, "Response entity must not be null");
        Assertions.assertEquals(AnnotationInheritanceGenericsImpl.METHOD_ID_INTERFACE_PUT,
                entity.getId(),
                "Response entity ID must match method id");
    }

    @Test
    public void testPutAbstract() {
        final AnnotationInheritanceGenericsEntity requestEntity = new AnnotationInheritanceGenericsEntity();

        final AnnotationInheritanceGenericsEntity entity = invokeRequest(
                AnnotationInheritanceGenericsImpl.class,
                null,
                HttpMethod.PUT,
                requestEntity,
                new GenericType<AnnotationInheritanceGenericsEntity>() {
                });

        Assertions.assertNotNull(entity, "Response entity must not be null");
        Assertions.assertEquals(AnnotationInheritanceGenericsImpl.METHOD_ID_ABSTRACT_PUT,
                entity.getId(),
                "Response entity ID must match method id");
    }

    private <T> T invokeRequest(
            final Class<?> resourceClass,
            final String pathArgument,
            final String httpMethod,
            final Object requestEntity,
            final GenericType<T> responseClass) {
        final Path resourcePathAnnotation = resourceClass.getAnnotation(Path.class);
        final String resourcePath = resourcePathAnnotation.value().startsWith("/") ? resourcePathAnnotation.value()
                : '/' + resourcePathAnnotation.value();
        final String resourceUrl = PortProviderUtil.generateURL(resourcePath, TEST_NAME);

        final WebTarget target = client.target(resourceUrl).path((pathArgument != null) ? pathArgument : "");

        final Invocation requestInvocation;
        if (requestEntity == null) {
            requestInvocation = target.request().build(httpMethod);
        } else {
            requestInvocation = target.request(MediaType.APPLICATION_XML_TYPE).build(httpMethod, Entity.xml(requestEntity));
        }

        if (responseClass != null) {
            final T responseEntity = requestInvocation.invoke(responseClass);
            return responseEntity;
        } else {
            requestInvocation.invoke();
            return null;
        }
    }

}
