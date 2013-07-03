package org.jboss.resteasy.client.jaxrs.engines;

import org.apache.commons.io.output.DeferredFileOutputStream;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.HttpClientParams;
import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.jboss.resteasy.client.jaxrs.internal.ClientResponse;
import org.jboss.resteasy.util.CaseInsensitiveMap;
import org.jboss.resteasy.util.CommitHeaderOutputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.MultivaluedMap;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: alexogar
 * Date: 7/3/13
 * Time: 11:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class URLConnectionEngine implements ClientHttpEngine {

    protected SSLContext sslContext;
    protected HostnameVerifier hostnameVerifier;

    @Override
    public ClientResponse invoke(ClientInvocation request) {

        final HttpURLConnection connection;

        final int status;
        try {

            connection = createConnection(request);

            executeRequest(request,connection);

            status = connection.getResponseCode();
        } catch (IOException e) {
            throw new ProcessingException("Unable to invoke request", e);
        }


        ClientResponse response = new ClientResponse(request.getClientConfiguration()) {
            private InputStream stream;

            @Override
            protected InputStream getInputStream() {
                if (stream==null) {
                    try {
                        stream = (status < 300) ? connection.getInputStream() : connection.getErrorStream();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                return stream;
            }

            @Override
            protected void setInputStream(InputStream is) {
                stream = is;
            }

            @Override
            protected void releaseConnection() throws IOException {
                try
                {
                    getInputStream().close();
                }
                catch (IOException e)
                {
                }
                connection.disconnect();
            }
        };

        response.setStatus(status);
        response.setHeaders(getHeaders(connection));

        return response;
    }

    private MultivaluedMap<String, String> getHeaders(
            final HttpURLConnection connection)
    {
        MultivaluedMap<String, String> headers = new CaseInsensitiveMap<String>();

        for (Map.Entry<String, List<String>> header : connection.getHeaderFields()
                .entrySet())
        {
            if (header.getKey() != null)
                for (String value : header.getValue())
                    headers.add(header.getKey(), value);
        }
        return headers;
    }

    @Override
    public void close() {
        //empty
    }

    protected HttpURLConnection createConnection(final ClientInvocation request) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) request.getUri().toURL().openConnection();
        connection.setRequestMethod(request.getMethod());

        return connection;
    }

    protected void executeRequest(final ClientInvocation request, HttpURLConnection connection)
    {
        connection.setInstanceFollowRedirects(request.getMethod().equals("GET"));

        if (request.getEntity() != null)
        {
            if (request.getMethod().equals("GET")) throw new ProcessingException("A GET request cannot have a body.");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            request.getDelegatingOutputStream().setDelegate(baos);
            try
            {

                request.writeRequestBody(request.getEntityStream());
                baos.close();
                commitHeaders(request, connection);
                connection.setDoOutput(true);
                OutputStream os = connection.getOutputStream();
                os.write(baos.toByteArray());
                os.flush();
                os.close();
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        else // no body
        {
            commitHeaders(request, connection);
        }
    }

    protected void commitHeaders(ClientInvocation request, HttpURLConnection connection)
    {
        MultivaluedMap<String, String> headers = request.getHeaders().asMap();
        for (Map.Entry<String, List<String>> header : headers.entrySet())
        {
            List<String> values = header.getValue();
            for (String value : values)
            {
//               System.out.println(String.format("setting %s = %s", header.getKey(), value));
                connection.addRequestProperty(header.getKey(), value);
            }
        }
    }

    @Override
    public SSLContext getSslContext() {
        return sslContext;
    }

    @Override
    public HostnameVerifier getHostnameVerifier() {
        return hostnameVerifier;
    }

    public void setSslContext(SSLContext sslContext) {
        this.sslContext = sslContext;
    }

    public void setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
    }
}

