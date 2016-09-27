package org.jboss.resteasy.plugins.providers.jackson;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.ws.rs.ConstrainedTo;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.resteasy.core.MediaTypeMap;
import org.jboss.resteasy.spi.ResteasyConfiguration;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.CommitHeaderOutputStream;
import org.jboss.resteasy.resteasy_jaxrs.i18n.*;

/**
 * <p>
 *  JSONP is an alternative to normal AJAX requests. Instead of using a XMLHttpRequest a script tag is added to the DOM.
 *  The browser will call the corresponding URL and download the JavaScript. The server creates a response which looks like a
 *  method call. The parameter is the body of the request. The name of the method to call is normally passed as query parameter.
 *  The method has to be present in the current JavaScript environment.
 * </p>
 * <p>
 *  Jackson JSON processor can produce such an response. This interceptor checks if the media type is a JavaScript one if there is a query
 *  parameter with the method name. The default name of this query parameter is "callback". So this interceptor is compatible with 
 *  <a href="http://api.jquery.com/jQuery.ajax/">jQuery</a>.
 * </p>
 * <p>
 *  It is possible to wrap the generated javascript function call in a try-catch block.
 *  You can enable it either by setting the {@link #wrapInTryCatch} property of the provider instance to {@code true}
 *  or by setting the {@code resteasy.jsonp.silent} context-param to true:
 * </p>
 * <pre>
 *  {@code
 *  <context-param>
 *   <param-name>resteasy.jsonp.silent</param-name>
 *   <param-value>true</param-value>
 *  </context-param>
 *  }
 * </pre>
 *
 * @author <a href="mailto:holger.morch@nokia.com">Holger Morch</a>
 * @version $Revision: 1 $
 */
@Provider
@ConstrainedTo(RuntimeType.SERVER)
public class Jackson2JsonpInterceptor implements WriterInterceptor{

    /**
     * "text/javascript" media type. Default media type of script tags.
     */
    public static final MediaType TEXT_JAVASCRIPT_MEDIA_TYPE = new MediaType("text", "javascript");
    
    /**
     * "application/javascript" media type.
     */
    public static final MediaType APPLICATION_JAVASCRIPT_MEDIA_TYPE = new MediaType("application", "javascript");

    /**
     * "text/json" media type.
     */
    public static final MediaType TEXT_JSON_TYPE = new MediaType("text", "json");

    /**
     * "application/*+json" media type. 
     */
    public static final MediaType APPLICATION_PLUS_JSON_TYPE = new MediaType("application", "*+json");

    /**
     * Default name of the query parameter with the method name.
     */
    public static final String DEFAULT_CALLBACK_QUERY_PARAMETER = "callback";

    /**
     * If response media type is one of this jsonp response may be created.
     */
    public static final MediaTypeMap<String> jsonpCompatibleMediaTypes = new MediaTypeMap<String>();
    
    /**
     * Default {@link ObjectMapper} for type resolution. Used if none is provided by {@link Providers}.
     */
    protected static final ObjectMapper DEFAULT_MAPPER = new ObjectMapper();
    
    static {
        jsonpCompatibleMediaTypes.add(MediaType.APPLICATION_JSON_TYPE  , MediaType.APPLICATION_JSON_TYPE.toString());
        jsonpCompatibleMediaTypes.add(APPLICATION_JAVASCRIPT_MEDIA_TYPE, APPLICATION_JAVASCRIPT_MEDIA_TYPE.toString());
        jsonpCompatibleMediaTypes.add(APPLICATION_PLUS_JSON_TYPE       , APPLICATION_PLUS_JSON_TYPE.toString());
        jsonpCompatibleMediaTypes.add(TEXT_JSON_TYPE                   , TEXT_JSON_TYPE.toString());
        jsonpCompatibleMediaTypes.add(TEXT_JAVASCRIPT_MEDIA_TYPE       , TEXT_JAVASCRIPT_MEDIA_TYPE.toString());
    }

    private UriInfo uri;
    
    private String callbackQueryParameter = DEFAULT_CALLBACK_QUERY_PARAMETER;

    private boolean wrapInTryCatch = false;

    public Jackson2JsonpInterceptor() {
        ResteasyConfiguration context = ResteasyProviderFactory.getContextData(ResteasyConfiguration.class);
        if (context != null) {
            wrapInTryCatch = Boolean.parseBoolean(context.getParameter("resteasy.jsonp.silent"));
            enabled = Boolean.parseBoolean(context.getParameter("resteasy.jsonp.enable"));
        }
    }

    /**
     * The {@link ObjectMapper} used to create typing information. 
     */
    protected ObjectMapper objectMapper;

    /**
     * The {@link Providers} used to retrieve the {@link #objectMapper} from. 
     */
    protected Providers providers;
    
    /**
     * Is this interceptor enabled.
     */
    private boolean enabled = false;

    /**
     * This subclass of {@link CommitHeaderOutputStream} overrides the {@link #close()} method so it would commit
     * the headers only, without actually calling the {@link #close()} method of the delegate {@link OutputStream}
     */
    private static class DoNotCloseDelegateOutputStream extends BufferedOutputStream {

        public DoNotCloseDelegateOutputStream(OutputStream delegate) {
            super(delegate);
        }

        @Override
        public void close() throws IOException {
            flush();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
        LogMessages.LOGGER.debugf("Interceptor : %s,  Method : aroundWriteTo", getClass().getName());

        String function = uri.getQueryParameters().getFirst(callbackQueryParameter);
        if (enabled && function != null && !function.trim().isEmpty() && !jsonpCompatibleMediaTypes.getPossible(context.getMediaType()).isEmpty()){

            OutputStreamWriter writer = new OutputStreamWriter(context.getOutputStream());

            if (wrapInTryCatch) writer.write("try{");
            writer.write(function + "(");
            writer.flush();

            // Disable the close method before calling context.proceed()
            OutputStream old = context.getOutputStream();
            DoNotCloseDelegateOutputStream wrappedOutputStream = new DoNotCloseDelegateOutputStream(old);
            context.setOutputStream(wrappedOutputStream);

            try {
                context.proceed();
                wrappedOutputStream.flush();
                writer.write(")");
                if (wrapInTryCatch) writer.write("}catch(e){}");
                writer.flush();
            } finally {
                context.setOutputStream(old);
            }
        } else {
            context.proceed();
        }
    }
    
    /**
     * Search for an {@link ObjectMapper} for the given class and mediaType
     * 
     * @param type the {@link Class} to serialize
     * @param mediaType the response {@link MediaType}
     * @return the {@link ObjectMapper}
     */
    protected ObjectMapper getObjectMapper(Class<?> type, MediaType mediaType)
    {
        if (objectMapper != null) {
            return objectMapper;
        }

        if (providers != null) {
            ContextResolver<ObjectMapper> resolver = providers.getContextResolver(ObjectMapper.class, mediaType);
            if (resolver == null) {
                resolver = providers.getContextResolver(ObjectMapper.class, null);
            }
            if (resolver != null) {
                return resolver.getContext(type);
            }
        }
        
        return DEFAULT_MAPPER;
    }
    
    
    /**
     * Setter used by RESTeasy to provide the {@link UriInfo}.
     * 
     * @param uri the uri to set
     */
    @Context
    public void setUri(UriInfo uri) {
        this.uri = uri;
    }
    
    /**
     * Setter used by RESTeasy to provide the {@link Providers}
     * 
     * @param providers
     */
    @Context
    public void setProviders(Providers providers) {
        this.providers = providers;
    }

    /**
     * Set an fix {@link ObjectMapper}. If this is not set {@link Providers} are used for lookup. If there are is none too, use a default one.
     * 
     * @param objectMapper
     */
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Get the name of the query parameter which contains the JavaScript method name. Default: callback.
     * 
     * @return the callbackQueryParameter
     */
    public String getCallbackQueryParameter() {
        return callbackQueryParameter;
    }

    /**
     * Set callback query parameter.
     * 
     * @see #getCallbackQueryParameter()
     * @param callbackQueryParameter the callbackQueryParameter to set
     */
    public void setCallbackQueryParameter(String callbackQueryParameter) {
        this.callbackQueryParameter = callbackQueryParameter;
    }

    /**
     * Check is the JSONP callback will be wrapped with try-catch block
     *
     * @return true if try-catch block is generated; false otherwise
     */
    public boolean isWrapInTryCatch() {
        return wrapInTryCatch;
    }

    /**
     * Enables or disables wrapping the JSONP callback try try-catch block
     *
     * @param wrapInTryCatch true if you want to wrap the result with try-catch block; false otherwise
     */
    public void setWrapInTryCatch(boolean wrapInTryCatch) {
        this.wrapInTryCatch = wrapInTryCatch;
    }

}
