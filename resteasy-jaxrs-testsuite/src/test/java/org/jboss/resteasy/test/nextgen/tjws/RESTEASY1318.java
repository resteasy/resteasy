package org.jboss.resteasy.test.nextgen.tjws;

import org.jboss.resteasy.plugins.server.tjws.TJWSServletServer;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
public class RESTEASY1318 {

    TJWSServletServer server = null;

    @Before
    public void setup() {
        server = new TJWSServletServer();
//        server.setPort(TestPortProvider.getPort());
        server.setSSLPort(TestPortProvider.getPort());
        server.setSSLKeyStoreFile(getClass()
                .getProtectionDomain().getCodeSource()
                .getLocation().getPath() + "/server_ks");
        server.setSSLKeyStorePass("123123");
        server.setSSLKeyPass("123123");
    }

    @After
    public void finish() {
        server.stop();
    }

    @Test
    public void testResource() throws Exception {
        server.addServlet("/hello", new HttpServlet() {
            private static final long serialVersionUID = -4176523779912453903L;

            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                resp.getWriter().write("world");
            }
        });
        server.start();

//        ClientRequest request = TestPortProvider.createClientRequest("/hello");
//        Assert.assertEquals("world", request.get(String.class).getEntity());
        System.setProperty("javax.net.ssl.trustStore", getClass()
                .getProtectionDomain().getCodeSource()
                .getLocation().getPath() + "/client_ks");

        Client client = ClientBuilder.newBuilder().build();
        String url = "https://localhost:PORT/hello".replaceAll("PORT",
                String.valueOf(TestPortProvider.getPort()));
        Response response = client.target(url).request().get();
        assertEquals("world", response.readEntity(String.class));

    }

}
