/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.resteasy.test.injection;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import dev.resteasy.junit.extension.annotations.RequestPath;
import dev.resteasy.junit.extension.annotations.RestBootstrap;
import dev.resteasy.junit.extension.annotations.RestResource;

/**
 * Tests the injection of the {@code @*Param} and {@link BeanParam @BeanParam} members of a CDI managed resource. The
 * members are only injected by CDI and not again by RESTEasy property injection.
 */
@RestBootstrap({ CdiParamInjectionTest.ParamResource.class, CdiParamInjectionTest.HierarchyResource.class })
@Tag("cdi")
class CdiParamInjectionTest {

    @Test
    void queryParamSetterInjectedOnce(@RestResource @RequestPath("param/query") final WebTarget target) {
        Assertions.assertEquals(1, target.queryParam("q", "value").request().get(int.class));
    }

    @Test
    void beanParamSetterInjectedOnce(@RestResource @RequestPath("param/bean") final WebTarget target) {
        Assertions.assertEquals(1, target.queryParam("q", "value").request().get(int.class));
    }

    @Test
    void contextOnlyBeanParamInjected(@RestResource @RequestPath("param/context") final WebTarget target) {
        Assertions.assertEquals("/param/context", target.request().get(String.class));
    }

    @Test
    void beanParamSuperclassInjected(@RestResource @RequestPath("hierarchy") final WebTarget target) {
        Assertions.assertEquals("2:value", target.queryParam("page", 2).queryParam("q", "value")
                .request().get(String.class));
    }

    public static class PageParams {
        @QueryParam("page")
        int page;
    }

    public static class SearchParams extends PageParams {
        @QueryParam("q")
        String q;
    }

    @Dependent
    public static class Aggregator {
        @QueryParam("q")
        String q;
    }

    @Dependent
    public static class ContextAggregator {
        @Context
        UriInfo uriInfo;
    }

    @Path("/param")
    @RequestScoped
    public static class ParamResource {
        private int queryParamInjections;
        private int beanParamInjections;

        @QueryParam("q")
        public void setQ(final String q) {
            queryParamInjections++;
        }

        @BeanParam
        public void setAggregator(final Aggregator aggregator) {
            beanParamInjections++;
        }

        @GET
        @Path("query")
        @Produces(MediaType.TEXT_PLAIN)
        public int queryParamInjections() {
            return queryParamInjections;
        }

        @GET
        @Path("bean")
        @Produces(MediaType.TEXT_PLAIN)
        public int beanParamInjections() {
            return beanParamInjections;
        }

        @GET
        @Path("context")
        @Produces(MediaType.TEXT_PLAIN)
        public String contextAggregatorPath(@BeanParam final ContextAggregator aggregator) {
            return aggregator.uriInfo.getPath();
        }
    }

    @Path("/hierarchy")
    @RequestScoped
    public static class HierarchyResource {
        @BeanParam
        PageParams page;

        @GET
        @Produces(MediaType.TEXT_PLAIN)
        public String get(@BeanParam final SearchParams search) {
            return page.page + ":" + search.q;
        }
    }
}
