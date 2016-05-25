package org.jboss.resteasy.test.nextgen.wadl;

import org.jboss.resteasy.test.TestPortProvider;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
public class BasicTest {

    private int port;
    private Client client;

    public BasicTest(int port, Client client) {
        this.port = port;
        this.client = client;
    }

    public void testBasicResource() throws Exception {
        String url = "http://127.0.0.1:${port}/application.xml".replaceAll("\\$\\{port\\}",
                Integer.valueOf(port).toString());
        WebTarget target = client.target(url);
        Response response = target.request().get();

        // get Application
        org.jboss.resteasy.wadl.jaxb.Application application = response.readEntity(org.jboss.resteasy.wadl.jaxb.Application.class);
        assertNotNull("application not null", application);
        assertEquals(1, application.getResources().size());

        // get BasicResource
        org.jboss.resteasy.wadl.jaxb.Resource basicResource = findResourceByName(application, "/basic");
        assertNotNull("basic resouce not null", basicResource);

        {
            // verify the existence of params
            ExistenceVerifier paramExistenceVerifier = new ExistenceVerifier();
            paramExistenceVerifier.createVerifier("name", "name2");
            paramExistenceVerifier.verify(basicResource.getParam(), org.jboss.resteasy.wadl.jaxb.Param.class, "getName");

            // verify existence of two methods: "get" and "post"
            ExistenceVerifier methodExistenceVerifier = new ExistenceVerifier();
            methodExistenceVerifier.createVerifier("get", "post");
            methodExistenceVerifier.verify(basicResource.getMethodOrResource(), org.jboss.resteasy.wadl.jaxb.Method.class, "getId");

            // verify 'post' method has expected id and name
            org.jboss.resteasy.wadl.jaxb.Method post = findMethodById(basicResource, "post");
            assertNotNull("post method not null", post);
            assertEquals("POST", post.getName());
            assertNotNull("post response not null", post.getResponse());
            assertNotNull("post response representation not null", post.getResponse().get(0).getRepresentation());

            // verify 'get' method
            org.jboss.resteasy.wadl.jaxb.Method get = findMethodById(basicResource, "get");
            assertEquals("GET", get.getName());
        }

        {
            // verify existence of resources
            ExistenceVerifier resourceExistenceVerifier = new ExistenceVerifier();
            String compositeResourceName = "composite/{pathParam}";

            resourceExistenceVerifier.createVerifier(compositeResourceName);
            resourceExistenceVerifier.verify(basicResource.getMethodOrResource(), org.jboss.resteasy.wadl.jaxb.Resource.class, "getPath");

            // verify resource 'intr/{foo}'
            org.jboss.resteasy.wadl.jaxb.Resource compositeResource = findResourceByName(basicResource, compositeResourceName);
            assertNotNull(compositeResource);
            assertEquals(compositeResourceName, compositeResource.getPath());

            ExistenceVerifier paramExistenceVerifier = new ExistenceVerifier();
            paramExistenceVerifier.createVerifier("pathParam", "matrixParam");
            paramExistenceVerifier.verify(compositeResource.getParam(), org.jboss.resteasy.wadl.jaxb.Param.class, "getName");

            ExistenceVerifier methodExistenceVerifier = new ExistenceVerifier();
            methodExistenceVerifier.createVerifier("composite");
            methodExistenceVerifier.verify(compositeResource.getMethodOrResource(), org.jboss.resteasy.wadl.jaxb.Method.class, "getId");

            org.jboss.resteasy.wadl.jaxb.Method compositeMethod = findMethodById(compositeResource, "composite");

            // verify response
            assertTrue(compositeResourceName + " response contains respresentation", compositeMethod.getResponse().get(0).getRepresentation().size() > 0);
            assertEquals("text/plain", compositeMethod.getResponse().get(0).getRepresentation().get(0).getMediaType());

            ExistenceVerifier requestVerifier = new ExistenceVerifier();
            requestVerifier.createVerifier("headerParam", "queryParam", "Cookie");
            requestVerifier.verify(compositeMethod.getRequest().getParam(), org.jboss.resteasy.wadl.jaxb.Param.class, "getName");
        }


    }

    private org.jboss.resteasy.wadl.jaxb.Method findMethodById(org.jboss.resteasy.wadl.jaxb.Resource resource, String id) {
        for (Object methodOrResource : resource.getMethodOrResource()) {
            if (methodOrResource.getClass().equals(org.jboss.resteasy.wadl.jaxb.Method.class))
                if (((org.jboss.resteasy.wadl.jaxb.Method) methodOrResource).getId().equals(id))
                    return (org.jboss.resteasy.wadl.jaxb.Method) methodOrResource;
        }
        return null;
    }

    private org.jboss.resteasy.wadl.jaxb.Resource findResourceByName(Object target, String resourceName) {
        if (target.getClass().equals(org.jboss.resteasy.wadl.jaxb.Application.class)) {
            for (org.jboss.resteasy.wadl.jaxb.Resource resource : ((org.jboss.resteasy.wadl.jaxb.Application) target).getResources().get(0).getResource()) {
                if (resource.getPath().equals(resourceName)) {
                    return resource;
                }
            }
        } else if (target.getClass().equals(org.jboss.resteasy.wadl.jaxb.Resource.class)) {
            for (Object resource : ((org.jboss.resteasy.wadl.jaxb.Resource) target).getMethodOrResource()) {
                if (resource.getClass().equals(org.jboss.resteasy.wadl.jaxb.Resource.class) && ((org.jboss.resteasy.wadl.jaxb.Resource) resource).getPath().equals(resourceName)) {
                    return (org.jboss.resteasy.wadl.jaxb.Resource) resource;
                }
            }
        }
        return null;
    }

    class ExistenceVerifier {
        private Map<String, Boolean> data = new HashMap<>();

        public void createVerifier(String... keys) {
            for (String key : keys) {
                data.put(key, false);
            }
        }

        public void verify(List targets, Class targetClass, String fetchKeyMethod) throws InvocationTargetException, IllegalAccessException {
            assertNotNull(targets);
            assertTrue(targets.size() > 0);

            Method invocation = null;

            for (Object target : targets) {
                for (Method method : target.getClass().getMethods()) {
                    if (target.getClass().equals(targetClass) && method.getName().equals(fetchKeyMethod)) {
                        invocation = method;
                        break;
                    }
                }
            }

            if (invocation == null) throw new NoSuchMethodError(fetchKeyMethod);

            for (Object target : targets) {
                for (String key : data.keySet()) {
                    if (target.getClass().equals(targetClass) && key.equals(invocation.invoke(target))) {
                        data.put(key, true);
                    }

                }
            }

            assertTrue(data.toString(), allTrue());
        }

        private boolean allTrue() {
            boolean flag = true;
            for (Boolean value : data.values()) {
                if (value.booleanValue() == false) {
                    flag = false;
                    break;
                }
            }
            return flag;
        }
    }
}
