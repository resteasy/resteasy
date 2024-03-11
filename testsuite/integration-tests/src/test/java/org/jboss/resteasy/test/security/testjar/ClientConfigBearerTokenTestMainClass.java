package org.jboss.resteasy.test.security.testjar;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.internal.BasicAuthentication;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.junit.jupiter.api.Assertions;

/**
 * Main class used in jar that is meant to test ClientConfigProvider functionality regarding Bearer token.
 */
public class ClientConfigBearerTokenTestMainClass {
    static String dummyUrl = "dummyUrl";

    public static void main(String[] args) {

        String testType = args[0];
        String result = null;
        ResteasyClientBuilder builder = (ResteasyClientBuilder) ClientBuilder.newBuilder();
        ResteasyClient client = builder.build();

        if (testType.equals("TEST_BEARER_TOKEN_IS_USED")) {
            Response response = client.target(dummyUrl)
                    .register(ClientConfigProviderBearerTokenAbortFilter.class).request().get();
            // ClientConfigProviderBearerTokenAbortFilter will succeed since bearer token was used from ClientConfigProvider
            Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            result = String.valueOf(response.getStatus());
        }

        if (testType.equals("TEST_BEARER_TOKEN_IGNORED_IF_BASIC_SET_BY_USER")) {
            try { // when user sets credentials then ClientConfigProvider's credentials and bearer token will be ignored
                client.register(new BasicAuthentication("bill", "password1"));
                client.register(ClientConfigProviderBearerTokenAbortFilter.class);
                client.target(dummyUrl).request().get();
                fail("ClientConfigProviderBearerTokenAbortFilter should fail since we have set different credentials");
            } catch (Exception e) {
                // bearer token was not added because Basic auth was used, therefore assertion failed with the below message
                String assertionErrorMessage = "The request authorization header is not correct ==> expected: <Bearer myTestToken> but was: <Basic YmlsbDpwYXNzd29yZDE=>";
                assertTrue(e.getMessage().contains(assertionErrorMessage));
                client.close();
                result = "Credentials set by user had precedence";
            }
        }

        //CHECKSTYLE.OFF: RegexpSinglelineJava
        System.out.println(result);
        //CHECKSTYLE.ON: RegexpSinglelineJava
        client.close();
    }
}
