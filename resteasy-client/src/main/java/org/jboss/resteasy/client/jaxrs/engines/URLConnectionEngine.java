package org.jboss.resteasy.client.jaxrs.engines;

import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.i18n.Messages;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.jboss.resteasy.client.jaxrs.internal.ClientResponse;
import org.jboss.resteasy.util.CaseInsensitiveMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.MultivaluedMap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:alexey.ogarkov@gmail.com">Alexey Ogarkov</a>
 * @version $Revision: 1 $
 */
public class URLConnectionEngine implements ClientHttpEngine
{

    protected SSLContext sslContext;
    protected HostnameVerifier hostnameVerifier;

    /**
     * {@inheritDoc}
     */
    @Override
    public ClientResponse invoke(ClientInvocation request)
    {

        final HttpURLConnection connection;

        final int status;
        try
        {

            connection = createConnection(request);

            executeRequest(request, connection);

            status = connection.getResponseCode();
        } catch (IOException e)
        {
           throw new ProcessingException(Messages.MESSAGES.unableToInvokeRequest(), e);
        }

        //Creating response with stream content
        ClientResponse response = new ClientResponse(request.getClientConfiguration())
        {
            private InputStream stream;

            @Override
            protected InputStream getInputStream()
            {
                if (stream == null)
                {
                    try
                    {
                        stream = (status < 300) ? connection.getInputStream() : connection.getErrorStream();
                    }
                    catch (IOException e)
                    {
                        throw new RuntimeException(e);
                    }
                }

                return stream;
            }

            @Override
            protected void setInputStream(InputStream is)
            {
                stream = is;
                resetEntity();
            }

            @Override
            public void releaseConnection() throws IOException
            {
                InputStream is = getInputStream();
                if (is != null)
                {
                   is.close();
                }
                connection.disconnect();
            }
        };

        //Setting attributes
        response.setStatus(status);
        response.setHeaders(getHeaders(connection));

        return response;
    }

    /**
     * Create map with response headers.
     *
     * @param connection - HttpURLConnection
     * @return map key - list of values
     */
    protected MultivaluedMap<String, String> getHeaders(
            final HttpURLConnection connection)
    {
        MultivaluedMap<String, String> headers = new CaseInsensitiveMap<String>();

        for (Map.Entry<String, List<String>> header : connection.getHeaderFields()
                .entrySet())
        {
            if (header.getKey() != null)
            {
                for (String value : header.getValue())
                {
                    headers.add(header.getKey(), value);
                }
            }
        }
        return headers;
    }

    @Override
    public void close()
    {
        //empty
    }

    /**
     * Create HttpUrlConnection from ClientInvorcation and set request method.
     * @param request ClientInvocation
     * @return HttpURLConnection with method {@literal &} url already set
     * @throws IOException if url or io exceptions
     */
    protected HttpURLConnection createConnection(final ClientInvocation request) throws IOException
    {
        HttpURLConnection connection = (HttpURLConnection) request.getUri().toURL().openConnection();
        connection.setRequestMethod(request.getMethod());

        return connection;
    }

    /**
     * Execute request using HttpURLConnection with body from invocation if needed.
     *
     * @param request ClientInvocation
     * @param connection HttpURLConnection
     */
    protected void executeRequest(final ClientInvocation request, HttpURLConnection connection)
    {
        connection.setInstanceFollowRedirects(request.getMethod().equals("GET"));

        if (request.getEntity() != null)
        {
           if (request.getMethod().equals("GET")) throw new ProcessingException(Messages.MESSAGES.getRequestCannotHaveBody());

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

    /**
     * Add headers to HttpURLConnection from ClientInvocation. Should be executed before writing body.
     * @param request ClientInvocation
     * @param connection HttpURLConnection
     */
    protected void commitHeaders(ClientInvocation request, HttpURLConnection connection)
    {
        MultivaluedMap<String, String> headers = request.getHeaders().asMap();
        for (Map.Entry<String, List<String>> header : headers.entrySet())
        {
            List<String> values = header.getValue();
            for (String value : values)
            {
                connection.addRequestProperty(header.getKey(), value);
            }
        }
    }

    /**
     * {inheritDoc}
     */
    @Override
    public SSLContext getSslContext()
    {
        return sslContext;
    }

    /**
     * {inheritDoc}
     */
    @Override
    public HostnameVerifier getHostnameVerifier()
    {
        return hostnameVerifier;
    }

    public void setSslContext(SSLContext sslContext)
    {
        this.sslContext = sslContext;
    }

    public void setHostnameVerifier(HostnameVerifier hostnameVerifier)
    {
        this.hostnameVerifier = hostnameVerifier;
    }
}

