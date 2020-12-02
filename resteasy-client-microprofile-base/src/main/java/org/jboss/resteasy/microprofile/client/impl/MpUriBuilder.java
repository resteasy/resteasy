package org.jboss.resteasy.microprofile.client.impl;

import org.eclipse.microprofile.rest.client.ext.QueryParamStyle;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.specimpl.ResteasyUriBuilderImpl;
import org.jboss.resteasy.util.Encode;

import jakarta.ws.rs.core.UriBuilder;
import java.net.URI;

public class MpUriBuilder extends ResteasyUriBuilderImpl {

    private QueryParamStyle queryParamStyle = null;

    public MpUriBuilder() {
        super();
    }
    /*
     * Constructor enables this class to be cloned and parent values
     * set properly.
     */
    public MpUriBuilder(final String host, final String scheme, final int port,
                        final String userInfo, final String path, final String query,
                        final String fragment, final String ssp, final String authority){
        super(host, scheme, port, userInfo, path, query,fragment, ssp, authority);
    }

    public void setQueryParamStyle(QueryParamStyle queryParamStyle) {
        this.queryParamStyle = queryParamStyle;
    }

    @Override
    public UriBuilder clone() {
        MpUriBuilder builder = new MpUriBuilder(getHost(), getScheme(), getPort(),
                getUserInfo(), getPath(), getQuery(), getFragment(),getSsp(),
                getAuthority());
        builder.setQueryParamStyle(queryParamStyle);
        return builder;
    }

    @Override
    public UriBuilder clientQueryParam(String name, Object... values) throws IllegalArgumentException
    {
        UriBuilder uriBuilder = null;

        switch(queryParamStyle){
            case COMMA_SEPARATED:
                clientQueryParamCommaSeparated(name, values);
                break;
            case ARRAY_PAIRS:
                clientQueryParamArrayPairs(name, values);
                break;
            case MULTI_PAIRS:
            default:
                uriBuilder = super.clientQueryParam(name, values);
        }
        return uriBuilder;
    }

    /**
     * key=value1,value2,value3.
     * @param name
     * @param values
     * @return
     * @throws IllegalArgumentException
     */
    private UriBuilder clientQueryParamCommaSeparated(String name, Object... values) throws IllegalArgumentException {
        String query = getQuery();

        StringBuilder sb = new StringBuilder();
        if (query == null) {
            query = "";
        } else {
            sb.append(query).append("&");
        }

        if (name == null) {
            throw new IllegalArgumentException(Messages.MESSAGES.nameParameterNull());
        }
        if (values == null) {
            throw new IllegalArgumentException(Messages.MESSAGES.valuesParameterNull());
        }

        sb.append(Encode.encodeQueryParamAsIs(name))
                .append("=");
        int cntDown = values.length - 1;

        for (Object value : values) {
            if (value == null) {
                throw new IllegalArgumentException(Messages.MESSAGES.passedInValueNull());
            }
            sb.append(Encode.encodeQueryParamAsIs(value.toString()));
            if (cntDown > 0) {
                sb.append(",");
                --cntDown;
            }
        }

        replaceQueryNoEncoding(sb.toString());
        return this;
    }

    /**
     * key[]=value1&key[]=value2&key[]=value3.
     * @param name
     * @param values
     * @return
     * @throws IllegalArgumentException
     */
    private UriBuilder clientQueryParamArrayPairs(String name, Object... values) throws IllegalArgumentException {
        String query = getQuery();

        StringBuilder sb = new StringBuilder();
        String prefix = "";
        if (query == null) {
            query = "";
        } else {
            sb.append(query).append("&");
        }

        if (name == null) {
            throw new IllegalArgumentException(Messages.MESSAGES.nameParameterNull());
        }
        if (values == null) {
            throw new IllegalArgumentException(Messages.MESSAGES.valuesParameterNull());
        }
        for (Object value : values) {
            if (value == null) {
                throw new IllegalArgumentException(Messages.MESSAGES.passedInValueNull());
            }
            sb.append(prefix);
            prefix = "&";
            sb.append(Encode.encodeQueryParamAsIs(name))
                    .append("[]=")
                    .append(Encode.encodeQueryParamAsIs(value.toString()));
        }

        replaceQueryNoEncoding(sb.toString());
        return this;
    }

    public UriBuilder uri(String uriTemplate, QueryParamStyle queryParamStyle) throws IllegalArgumentException
    {
        this.queryParamStyle = queryParamStyle;
        return uriTemplate(uriTemplate);
    }
    public UriBuilder uri(URI uri, QueryParamStyle queryParamStyle) throws IllegalArgumentException {
        this.queryParamStyle = queryParamStyle;
        return uri(uri);
    }
}
