package org.jboss.resteasy.test.providers.jaxb;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Jaxb provider
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class XmlJAXBContextFinderTest {

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(XmlJAXBContextFinderTest.class.getSimpleName());

        return TestUtil.finishContainerPrepare(war, null, BeanWrapper.class,
                FirstBean.class, SecondBean.class,
                FirstTestResource.class, SecondTestResource.class,
                MyJAXBContextResolver.class);
    }

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    public static final class BeanWrapper {

        @XmlAnyElement(lax = true)
        private Object bean;

        public Object getBean() {
            return this.bean;
        }

        public void setBean(Object bean) {
            this.bean = bean;
        }

    }

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    public static final class FirstBean {

        private String data;

        public String getData() {
            return this.data;
        }

        public void setData(String data) {
            this.data = data;
        }

    }

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    public static final class SecondBean {

        private String data;

        public String getData() {
            return this.data;
        }

        public void setData(String data) {
            this.data = data;
        }

    }

    @Path("/firstTestResource")
    @Produces(MediaType.APPLICATION_XML)
    public static final class FirstTestResource {

        @GET
        public Response get() {
            FirstBean firstBean = new FirstBean();
            firstBean.setData("firstTestResource");
            BeanWrapper beanWrapper = new BeanWrapper();
            beanWrapper.setBean(firstBean);
            return Response.ok(beanWrapper).build();
        }

    }

    @Path("/secondTestResource")
    @Produces(MediaType.APPLICATION_XML)
    public static final class SecondTestResource {

        @GET
        public Response get() {
            SecondBean secondBean = new SecondBean();
            secondBean.setData("secondTestResource");
            BeanWrapper beanWrapper = new BeanWrapper();
            beanWrapper.setBean(secondBean);
            return Response.ok(beanWrapper).build();
        }

    }

    @Provider
    @Produces(MediaType.APPLICATION_XML)
    public static class MyJAXBContextResolver implements ContextResolver<JAXBContext> {
        private JAXBContext jaxbContext;

        @Override
        public JAXBContext getContext(Class<?> type) {
            if (this.jaxbContext == null) {
                try {
                    this.jaxbContext = JAXBContext.newInstance(BeanWrapper.class,
                            FirstBean.class, SecondBean.class);
                } catch (JAXBException e) {
                }
            }
            return this.jaxbContext;
        }
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, XmlJAXBContextFinderTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails In the following test both firstWebTarget and secondWebTarget will share the same XmlJAXBContextFinder
     *                inherited from their shared parent configuration. We define and register a ContextResolver<JAXBContext>
     *                for each
     *                webTarget so that firstWebTarget and secondWebTarget have its own (respectively firstJaxbContextResolver
     *                and secondJaxbContextResolver).
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void test() {
        Client client = ClientBuilder.newClient();
        try {
            // First webTarget
            WebTarget firstWebTarget = client.target(generateURL("/firstTestResource"));
            ContextResolver<JAXBContext> firstJaxbContextResolver = new ContextResolver<JAXBContext>() {

                private JAXBContext jaxbContext;

                @Override
                public JAXBContext getContext(Class<?> type) {
                    if (this.jaxbContext == null) {
                        try {
                            this.jaxbContext = JAXBContext.newInstance(BeanWrapper.class,
                                    FirstBean.class);
                        } catch (JAXBException e) {
                        }
                    }
                    return this.jaxbContext;
                }

            };
            Response firstResponse = firstWebTarget.register(firstJaxbContextResolver)
                    .request(MediaType.APPLICATION_XML_TYPE).get();
            BeanWrapper firstBeanWrapper = firstResponse.readEntity(BeanWrapper.class);
            Assertions.assertTrue(FirstBean.class.isAssignableFrom(firstBeanWrapper.getBean().getClass()),
                    "First bean is not assignable from the parent bean");

            // Second webTarget
            WebTarget secondWebTarget = client.target(generateURL("/secondTestResource"));
            // Will never be called
            ContextResolver<JAXBContext> secondJaxbContextResolver = new ContextResolver<JAXBContext>() {

                private JAXBContext jaxbContext;

                @Override
                public JAXBContext getContext(Class<?> type) {
                    if (this.jaxbContext == null) {
                        try {
                            this.jaxbContext = JAXBContext.newInstance(BeanWrapper.class,
                                    SecondBean.class);
                        } catch (JAXBException e) {
                        }
                    }
                    return this.jaxbContext;
                }

            };
            Response secondResponse = secondWebTarget.register(secondJaxbContextResolver)
                    .request(MediaType.APPLICATION_XML_TYPE).get();
            BeanWrapper secondBeanWrapper = secondResponse.readEntity(BeanWrapper.class);
            Assertions.assertTrue(SecondBean.class.isAssignableFrom(secondBeanWrapper.getBean().getClass()),
                    "Second bean is not assignable from the parent bean");
        } finally {
            client.close();
        }
    }

}
