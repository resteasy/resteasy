package org.jboss.resteasy.test.security.testjar;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.internal.BasicAuthentication;

import javax.net.ssl.SSLContext;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

/**
 * ClientConfigProvider implementation used in jar that tests ClientConfigProvider functionality regarding HTTP BASIC auth and SSLContext.
 */
public class ClientConfigTestMainClass {
    public static void main(String[] args) throws IOException, URISyntaxException, NoSuchAlgorithmException {
        if (args.length <= 1) {
            throw new IllegalArgumentException("Url must be supplied!");
        }

        if (args.length > 2) {
            ClientConfigProviderImplMocked.KEYSTORE_PATH = args[2];
        }

        String testType = args[0];
        String result = null;
        URL url = new URL(args[1]);
        ResteasyClientBuilder resteasyClientBuilder = (ResteasyClientBuilder) ClientBuilder.newBuilder();
        ResteasyClient client = resteasyClientBuilder.build();
        Response response;

        if (testType.equals("TEST_CREDENTIALS_ARE_USED_FOR_BASIC") || testType.equals("TEST_SSLCONTEXT_USED")) {
            response = client.target(url.toURI()).request().get();
            result = Integer.toString(response.getStatus());
        }

        if (testType.equals("TEST_CLIENTCONFIG_CREDENTIALS_ARE_IGNORED_IF_DIFFERENT_SET")) {
            client.register(new BasicAuthentication("invalid", "invalid_pass"));
            response = client.target(url.toURI()).request().get();
            result = Integer.toString(response.getStatus());
        }

        if (testType.equals("TEST_CLIENTCONFIG_SSLCONTEXT_IGNORED_WHEN_DIFFERENT_SET")) {
            ResteasyClient clientWithSSLContextSetByUser = resteasyClientBuilder.sslContext(SSLContext.getDefault()).build();
            try {
                response = clientWithSSLContextSetByUser.target(url.toURI()).request().get();
                result = Integer.toString(response.getStatus());
            } catch (Exception e) {
                if (e.getCause().getMessage().contains("unable to find valid certification path to requested target")) {
                    result = "SSLHandshakeException";
                }
            }
        }
        //CHECKSTYLE.OFF: RegexpSinglelineJava
        System.out.println(result);
        //CHECKSTYLE.ON: RegexpSinglelineJava
        client.close();
    }
}
