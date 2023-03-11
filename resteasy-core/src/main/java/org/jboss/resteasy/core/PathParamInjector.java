package org.jboss.resteasy.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.PathSegment;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.specimpl.ResteasyUriInfo;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.InternalServerErrorException;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.ValueInjector;
import org.jboss.resteasy.spi.util.Types;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@SuppressWarnings("rawtypes")
public class PathParamInjector implements ValueInjector {
    private StringParameterInjector extractor;
    private String paramName;
    private boolean encode;
    //   private boolean pathSegment = false;
    private boolean pathSegmentArray = false;
    private boolean pathSegmentList = false;

    public PathParamInjector(final Class type, final Type genericType, final AccessibleObject target, final String paramName,
            final String defaultValue, final boolean encode, final Annotation[] annotations,
            final ResteasyProviderFactory factory) {
        if (isPathSegmentArray(type)) {
            pathSegmentArray = true;
        } else if (isPathSegmentList(type, genericType)) {
            pathSegmentList = true;
        } else if (type.equals(PathSegment.class)) {
            //         pathSegment = true;
        } else {
            extractor = new StringParameterInjector(type, genericType, paramName, PathParam.class, defaultValue, target,
                    annotations, factory) {
                @Override
                protected void throwProcessingException(String message, Throwable cause) {
                    throw new NotFoundException(message, cause);
                }
            };
        }
        this.paramName = paramName;
        this.encode = encode;
    }

    private boolean isPathSegmentArray(Class type) {
        return type.isArray() && type.getComponentType().equals(PathSegment.class);
    }

    private boolean isPathSegmentList(Class type, Type genericType) {
        Class collectionBaseType = Types.getCollectionBaseType(type, genericType);
        return (List.class.equals(type) || ArrayList.class.equals(type)) && collectionBaseType != null
                && collectionBaseType.equals(PathSegment.class);
    }

    @Override
    public Object inject(HttpRequest request, HttpResponse response, boolean unwrapAsync) {
        if (extractor == null) // we are a PathSegment
        {
            ResteasyUriInfo uriInfo = (ResteasyUriInfo) request.getUri();
            List<PathSegment[]> list = null;
            if (encode) {
                list = uriInfo.getEncodedPathParameterPathSegments().get(paramName);
            } else {
                list = uriInfo.getPathParameterPathSegments().get(paramName);
            }
            if (list == null) {
                throw new InternalServerErrorException(Messages.MESSAGES.unknownPathParam(paramName, uriInfo.getPath()));
            }
            List<PathSegment> segmentList = flattenToList(list);
            if (pathSegmentArray) {
                PathSegment[] segments = new PathSegment[segmentList.size()];
                segments = segmentList.toArray(segments);
                return segments;
            } else if (pathSegmentList) {
                return segmentList;
            } else {
                return segmentList.get(segmentList.size() - 1);
            }
        } else {
            List<String> list = request.getUri().getPathParameters(!encode).get(paramName);
            if (list == null) {
                if (extractor.isCollectionOrArray()) {
                    return extractor.extractValues(null);
                } else {
                    return extractor.extractValue(null);
                }
            }
            if (extractor.isCollectionOrArray()) {
                return extractor.extractValues(list);
            } else {
                return extractor.extractValue(list.get(list.size() - 1));
            }
        }
    }

    @Override
    public Object inject(boolean unwrapAsync) {
        throw new RuntimeException(Messages.MESSAGES.illegalToInjectPathParam());
    }

    private List<PathSegment> flattenToList(List<PathSegment[]> list) {
        ArrayList<PathSegment> psl = new ArrayList<PathSegment>();
        for (PathSegment[] psa : list) {
            for (PathSegment ps : psa) {
                psl.add(ps);
            }
        }
        return psl;
    }
}
