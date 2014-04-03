package org.jboss.resteasy.resteasy1043;

import static org.jboss.resteasy.resteasy1043.HTTPCLIENT1340Test.RequestExecutor.*;
import static org.jboss.resteasy.util.HttpResponseCodes.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.auth.params.AuthPNames;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.URIUtil;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.security.Password;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Credits go Igor Fedorenko (https://github.com/ifedorenko/httpclient1340)
 *
 */
@RunWith(Arquillian.class)
@RunAsClient
public class HTTPCLIENT1340Test
{

    private Server server;

    @Deployment
    public static Archive<?> createTestArchive()
    {
       WebArchive war = ShrinkWrap.create(WebArchive.class, RequestExecutor.contextName + ".war")
             .addClass(TestApplication.class)
             .addClass(TestResource.class)
             .addClass(RequestExecutor.class)
             .addAsWebInfResource("web.xml")
             //FIXME for jetty this will fail
             .addAsManifestResource(new StringAsset("Dependencies: org.apache.httpcomponents\n"), "MANIFEST.MF");
       System.out.println(war.toString(true));
       return war;
    }


    @Before
    public void before() throws Exception {
        // enable httpclient write+
        // -Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.SimpleLog
        // -Dorg.apache.commons.logging.simplelog.showdatetime=true
        // -Dorg.apache.commons.logging.simplelog.log.org.apache.http=DEBUG
        System.setProperty( "org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog" );
        System.setProperty( "org.apache.commons.logging.simplelog.showdatetime", "true" );
        System.setProperty( "org.apache.commons.logging.simplelog.log.org.apache.http", "DEBUG" );
        server = startServer( RequestExecutor.port, new File( "src/test/resources" ), username, password, RequestExecutor.contextName);
    }


    @After
    public void after() throws Exception {
        server.stop();
    }

    @Test
    public void testResteasyClient() throws Exception
    {

       new RequestExecutor().executeRequest(RequestExecutor.url);
    }

    @Test
    public void testResteasyClientOnServer() throws Exception
    {
        String url = "http://localhost:8080/" + contextName + "/test";
        ClientRequest request = new ClientRequest(url);
        ClientResponse<String> response = request.get(String.class);

        assertEquals(200, response.getStatus());
    }


    public static class RequestExecutor {

        public static final String username = "username";
        public static  final String password = "password";
        public static final int port = 8888;
        public static final String contextName = "httpclient1340";
        public static final String url = "http://localhost:" + port + "/" + contextName + "/file.txt";


        public String executeRequest(String url) throws Exception {
            ApacheHttpClient4Executor executor = (ApacheHttpClient4Executor) ClientRequest.getDefaultExecutor();
            DefaultHttpClient httpclient  = (DefaultHttpClient) executor.getHttpClient();
            setAuthorization(httpclient, username, password);

            ClientRequest request = new ClientRequest(url, executor);

            String result = "";
            // this fails when connection is not handled properly by underlying httpClient
            for(int i = 1; i < 3; i++) {
                ClientResponse<String> response = request.get(String.class);
                assertEquals(SC_OK, response.getStatus());
                result += ""+i;
                // if response is read, test fails no more !
                //result += response.getEntity();

                response.releaseConnection();
                request.clear();
            }
            return result;
        }

        public static void setAuthorization(DefaultHttpClient httpclient, String username, String password) {
            List<String> authorisationPreference = new ArrayList<String>();
            authorisationPreference.add( AuthPolicy.DIGEST );
            authorisationPreference.add( AuthPolicy.BASIC );
            Credentials credentials = null;
            credentials = new UsernamePasswordCredentials( username, password );
            httpclient.getCredentialsProvider().setCredentials( AuthScope.ANY, credentials );
            httpclient.getParams().setParameter( AuthPNames.TARGET_AUTH_PREF, authorisationPreference );
        }

    }

    @Test
    public void testHttpClient() throws Exception
    {


        DefaultHttpClient httpclient = new DefaultHttpClient();

        setAuthorization(httpclient, username, password);

        try
        {
            executeRequest( httpclient, RequestExecutor.url );
            executeRequest( httpclient, RequestExecutor.url );
        }
        finally
        {
        }
    }



    private void executeRequest( DefaultHttpClient httpclient, String url )
        throws IOException, ClientProtocolException
    {
        HttpGet httpGet = new HttpGet( url );
        HttpResponse response = httpclient.execute( httpGet );
        EntityUtils.consume( response.getEntity() );
        if ( response.getStatusLine().getStatusCode() < 200 || response.getStatusLine().getStatusCode() > 299 )
        {
            throw new IOException( response.getStatusLine().getReasonPhrase() );
        }
    }

    /**
     * Starts test (jetty) server on specified port. The server will serve files from specified basedir and will
     * require specified username/password.
     * <p>
     * The server sets Connection:Keep-Alive http response header for unauthorized requests (default jetty behaviour).
     * <p>
     * The server sets Connection:close http response header for successful requests (see anonymous ServletHolder#handle
     * implemented in this method)
     * @param contextName
     */
    private Server startServer( int port, File basedir, String username, String password, String contextName) throws Exception
    {
        ContextHandlerCollection contexts = new ContextHandlerCollection();
        Server server = new Server();

        server.setHandler( contexts );

        Connector connector = new SocketConnector();
        connector.setPort( port );
        connector.setMaxIdleTime( 5000 );
        server.addConnector( connector );

        if ( username != null )
        {
            HashLoginService userRealm = new HashLoginService( "default" );
            userRealm.putUser( username, new Password( password ), new String[] { Constraint.ANY_ROLE } );

            Constraint constraint = new Constraint( "default", Constraint.ANY_ROLE );
            constraint.setAuthenticate( true );
            ConstraintMapping constraintMapping = new ConstraintMapping();
            constraintMapping.setPathSpec( "/*" );
            constraintMapping.setConstraint( constraint );

            ConstraintSecurityHandler securedHandler = new ConstraintSecurityHandler();
            securedHandler.setAuthenticator( new BasicAuthenticator() );
            securedHandler.addConstraintMapping( constraintMapping );
            securedHandler.setLoginService( userRealm );

            // chain handlers together
            securedHandler.setHandler( contexts );
            server.setHandler( securedHandler );
        }

        server.start();

        ServletContextHandler context = new ServletContextHandler( contexts, URIUtil.SLASH + contextName );
        context.setResourceBase( basedir.getAbsolutePath() );
        DefaultServlet servlet = new DefaultServlet();
        ServletHolder holder = new ServletHolder( servlet )
        {
            @Override
            public void handle( Request baseRequest, ServletRequest request, ServletResponse response )
                throws ServletException, UnavailableException, IOException
            {
                ( (HttpServletResponse) response ).addHeader( "Connection", "close" );
                super.handle( baseRequest, request, response );
            }
        };
        context.addServlet( holder, URIUtil.SLASH );
        contexts.addHandler( context );
        try
        {
            context.start();
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
        return server;

    }

}
