package org.jboss.resteasy.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.core.EntityPart;
import jakarta.ws.rs.core.MediaType;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.ValueInjector;
import org.jboss.resteasy.spi.util.Types;
import org.jboss.resteasy.util.Encode;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class FormParamInjector extends StringParameterInjector implements ValueInjector {
    private boolean encode;

    public FormParamInjector(final Class type, final Type genericType, final AccessibleObject target, final String header,
            final String defaultValue, final boolean encode, final Annotation[] annotations,
            final ResteasyProviderFactory factory) {
        super(type, genericType, header, FormParam.class, defaultValue, target, annotations, factory,
                Map.of(FormParam.class, List.of(InputStream.class, EntityPart.class)));
        this.encode = encode;
    }

    @Override
    public Object inject(HttpRequest request, HttpResponse response, boolean unwrapAsync) {
        // A @FormParam for multipart/form-data can be a String, InputStream or EntityPart. This type is handled specially.
        if (EntityPart.class.isAssignableFrom(type)) {
            return request.getFormEntityPart(paramName).orElse(null);
        } else if (List.class.isAssignableFrom(type) && Types.isGenericTypeInstanceOf(EntityPart.class, baseGenericType)) {
            return request.getFormEntityParts();
        } else if (InputStream.class.isAssignableFrom(type)) {
            final Optional<EntityPart> part = request.getFormEntityPart(paramName);
            return part.map(EntityPart::getContent).orElse(null);
        } else if (String.class.isAssignableFrom(type) &&
        // request.getHttpHeaders().getMediaType() may return null, but the isCompatible handles this check
                MediaType.MULTIPART_FORM_DATA_TYPE.isCompatible(request.getHttpHeaders().getMediaType())) {
            final Optional<EntityPart> part = request.getFormEntityPart(paramName);
            return part.map(p -> {
                try {
                    return p.getContent(String.class);
                } catch (IOException e) {
                    throw new UncheckedIOException(Messages.MESSAGES.unableToExtractParameter(paramName, null), e);
                }
            }).orElse(null);
        }
        List<String> list = request.getDecodedFormParameters().get(paramName);
        if (list != null && encode) {
            List<String> encodedList = new ArrayList<String>();
            for (String s : list) {
                encodedList.add(Encode.encodeString(s));
            }
            list = encodedList;
        }
        return extractValues(list);
    }

    @Override
    public Object inject(boolean unwrapAsync) {
        throw new RuntimeException(Messages.MESSAGES.illegalToInjectFormParam());
    }
}
