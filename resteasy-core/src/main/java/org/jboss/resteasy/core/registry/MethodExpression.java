package org.jboss.resteasy.core.registry;

import java.util.regex.Matcher;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.PathSegment;

import org.jboss.resteasy.core.ResourceLocatorInvoker;
import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.specimpl.ResteasyUriInfo;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.ResourceInvoker;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MethodExpression extends Expression {
    protected SegmentNode parent;
    protected ResourceInvoker invoker;

    public int compareTo(Expression expression) {
        int s = super.compareTo(expression);
        if (s != 0)
            return s;

        MethodExpression me = (MethodExpression) expression;
        if (invoker.getClass().equals(me.invoker.getClass()))
            return 0;

        if (invoker instanceof ResourceMethodInvoker)
            return -1;
        else
            return 1;
    }

    public MethodExpression(final SegmentNode parent, final String segment, final ResourceInvoker invoker) {
        this(parent, segment, invoker, null);
    }

    public MethodExpression(final SegmentNode parent, final String segment, final ResourceInvoker invoker,
            final String additionalRegex) {
        super(segment, additionalRegex);
        this.parent = parent;
        this.invoker = invoker;
    }

    public void populatePathParams(HttpRequest request, Matcher matcher, String path) {
        ResteasyUriInfo uriInfo = (ResteasyUriInfo) request.getUri();
        for (Group group : groups) {
            String value = matcher.group(group.group);
            uriInfo.addEncodedPathParameter(group.name, value);
            int index = matcher.start(group.group);

            int start = 0;
            if (path.charAt(0) == '/')
                start++;
            int segmentIndex = 0;

            if (start < path.length()) {
                int count = 0;
                for (int i = start; i < index && i < path.length(); i++) {
                    if (path.charAt(i) == '/')
                        count++;
                }
                segmentIndex = count;
            }

            int numSegments = 1;
            for (int i = 0; i < value.length(); i++) {
                if (value.charAt(i) == '/')
                    numSegments++;
            }

            if (segmentIndex + numSegments > request.getUri().getPathSegments().size()) {
                throw new BadRequestException(Messages.MESSAGES.numberOfMatchedSegments());
            }
            PathSegment[] encodedSegments = new PathSegment[numSegments];
            PathSegment[] decodedSegments = new PathSegment[numSegments];
            for (int i = 0; i < numSegments; i++) {
                decodedSegments[i] = request.getUri().getPathSegments().get(segmentIndex + i);
                encodedSegments[i] = request.getUri().getPathSegments(false).get(segmentIndex + i);
            }
            uriInfo.getEncodedPathParameterPathSegments().add(group.name, encodedSegments);
            uriInfo.getPathParameterPathSegments().add(group.name, decodedSegments);
        }
    }

    public boolean isLocator() {
        return invoker instanceof ResourceLocatorInvoker;
    }

    public ResourceInvoker getInvoker() {
        return invoker;
    }
}
