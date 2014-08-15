package org.jboss.resteasy.test.providers.preference;

import static junit.framework.Assert.assertEquals;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.core.executors.InMemoryClientExecutor;
import org.junit.Ignore;
import org.junit.Test;

/**
 * 
 * @author <a href="http://community.jboss.org/people/jharting">Jozef Hartinger</a>
 *
 */
public class CustomProviderPreferenceTest {

    @Test
    public void testCustomProviderPreference() throws Exception {

        InMemoryClientExecutor executor = new InMemoryClientExecutor();
        executor.getRegistry().addPerRequestResource(UserResource.class);
        executor.getDispatcher().getProviderFactory().registerProvider(UserBodyWriter.class);
        ClientResponse<String> result = new ClientRequest("/user", executor).get(String.class);

        assertEquals(200, result.getStatus());
        assertEquals("jharting;email@example.com", result.getEntity());
    }
    
    @Test
    @Ignore
    public void testApplicationProvidedLessSpecificWriterOverBuiltinStringWriter() throws Exception {

        InMemoryClientExecutor executor = new InMemoryClientExecutor();
        executor.getRegistry().addPerRequestResource(StringResource.class);
        executor.getDispatcher().getProviderFactory().registerProvider(GeneralWriter.class);
        ClientResponse<String> result = new ClientRequest("/test", executor).get(String.class);

        assertEquals(200, result.getStatus());
        assertEquals("The resource returned: Hello world!", result.getEntity());
    }
}
