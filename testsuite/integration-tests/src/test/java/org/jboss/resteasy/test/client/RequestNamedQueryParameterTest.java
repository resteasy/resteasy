package org.jboss.resteasy.test.client;

import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;
import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.internal.AbortedResponse;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:dkafetzi@redhat.com">Dimitris Kafetzis</a>
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpSince RESTEasy 6.3.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class RequestNamedQueryParameterTest extends ClientTestBase {

    @Path("someResource")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public interface SomeResource {

        @GET
        void methodWithLists(@QueryParam("listA") List<String> listA, @QueryParam("listB") List<String> listB,
                @QueryParam("listC") List<String> listC);
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ClientInvocationBuilderTest.class.getSimpleName());
        war.addClass(SomeResource.class);
        war.addClass(ClientTestBase.class);
        return war;
    }

    /**
     * @tpTestDetails Check if empty named query parameters of a request are properly handled.
     * @tpPassCrit The query for this request invocation should be empty.
     *             The empty Named Query Parameters should be Ignored
     *             (no stray or duplicate '&'s should be present)
     * @tpSince RESTEasy 6.3.0
     */
    @Test
    public void testWithEmptyNamedQueryParameters() {
        ResteasyClientBuilder builder = (ResteasyClientBuilder) ClientBuilder.newBuilder();
        ResteasyClient client = builder.httpEngine(new ClientHttpEngine() {
            @Override
            public Response invoke(Invocation request) {
                Assert.assertEquals("", ((ClientInvocation) request).getUri().getQuery());
                return new AbortedResponse(null, new ServerResponse());
            }

            @Override
            public SSLContext getSslContext() {
                return null;
            }

            @Override
            public HostnameVerifier getHostnameVerifier() {
                return null;
            }

            @Override
            public void close() {
            }
        }).build();

        try {
            client.target("http://a.place.org").proxy(SomeResource.class).methodWithLists(List.of(), List.of(), List.of());
        } finally {
            client.close();
        }

    }

    /**
     * @tpTestDetails Check if empty named query parameters of a request are properly handled, This time the parameters are
     *                mixed, some are empty some have contents.
     * @tpPassCrit The query for this request invocation contains only the parameters with content.
     *             The empty Named Query Parameters should be Ignored
     *             (no stray or duplicate '&'s should be present)
     * @tpSince RESTEasy 6.3.0
     */
    @Test
    public void testWithMixedNamedQueryParameters() {
        ResteasyClientBuilder builder = (ResteasyClientBuilder) ClientBuilder.newBuilder();
        ResteasyClient client = builder.httpEngine(new ClientHttpEngine() {
            @Override
            public Response invoke(Invocation request) {
                Assert.assertEquals("listA=stuff1&listA=stuff2&listC=stuff1&listC=stuff2",
                        ((ClientInvocation) request).getUri().getQuery());
                return new AbortedResponse(null, new ServerResponse());
            }

            @Override
            public SSLContext getSslContext() {
                return null;
            }

            @Override
            public HostnameVerifier getHostnameVerifier() {
                return null;
            }

            @Override
            public void close() {
            }
        }).build();

        try {
            client.target("http://a.place.org").proxy(SomeResource.class).methodWithLists(List.of("stuff1", "stuff2"),
                    List.of(), List.of("stuff1", "stuff2"));
        } finally {
            client.close();
        }

    }

    /**
     * @tpTestDetails Sanity check to make sure that normal behaviour where all the parameters have content did not break.
     * @tpPassCrit The query for this request invocation should have the contents from all the parameters.
     *
     * @tpSince RESTEasy 6.3.0
     */
    @Test
    public void testWithFullNamedQueryParameters() {
        ResteasyClientBuilder builder = (ResteasyClientBuilder) ClientBuilder.newBuilder();
        ResteasyClient client = builder.httpEngine(new ClientHttpEngine() {
            @Override
            public Response invoke(Invocation request) {
                Assert.assertEquals("listA=stuff1&listA=stuff2&listB=stuff1&listB=stuff2&listC=stuff1&listC=stuff2",
                        ((ClientInvocation) request).getUri().getQuery());
                return new AbortedResponse(null, new ServerResponse());
            }

            @Override
            public SSLContext getSslContext() {
                return null;
            }

            @Override
            public HostnameVerifier getHostnameVerifier() {
                return null;
            }

            @Override
            public void close() {
            }
        }).build();

        try {
            client.target("http://a.place.org").proxy(SomeResource.class).methodWithLists(List.of("stuff1", "stuff2"),
                    List.of("stuff1", "stuff2"), List.of("stuff1", "stuff2"));
        } finally {
            client.close();
        }

    }

}
