package org.resteasy;

import org.resteasy.spi.ResourceFactory;
import org.resteasy.util.IsHttpMethod;
import org.resteasy.util.PathHelper;

import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ProviderFactory;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Registry {

    public class Node {
        private List<ResourceInvoker> invokers = new ArrayList<ResourceInvoker>();
        private List<Node> wildChildren = new ArrayList<Node>();
        private Map<String, Node> children = new HashMap<String, Node>();

        public Node() {
        }

        public void addChild(String[] path, int pathIndex, ResourceInvoker invoker) {
            Matcher matcher = PathHelper.URI_TEMPLATE_PATTERN.matcher(path[pathIndex]);
            if (matcher.matches()) {
                String uriParamName = matcher.group(2);
                invoker.addUriParam(pathIndex, uriParamName);
                Node child = new Node();
                wildChildren.add(child);
                if (path.length == pathIndex + 1) {
                    child.invokers.add(invoker);
                } else {
                    child.addChild(path, ++pathIndex, invoker);
                }
            } else {
                Node child = children.get(path[pathIndex]);
                if (child == null) {
                    child = new Node();
                    children.put(path[pathIndex], child);
                }
                if (path.length == pathIndex + 1) {
                    child.invokers.add(invoker);
                } else {
                    child.addChild(path, ++pathIndex, invoker);
                }
            }
        }

        public ResourceInvoker findResourceInvoker(String httpMethod, String[] path, int pathIndex, MediaType contentType, List<MediaType> accepts) {
            if (pathIndex >= path.length) return match(httpMethod, contentType, accepts);
            else return findChild(httpMethod, path, pathIndex, contentType, accepts);
        }

        private ResourceInvoker findChild(String httpMethod, String[] path, int pathIndex, MediaType contentType, List<MediaType> accepts) {
            Node next = children.get(path[pathIndex]);
            if (next != null) return next.findResourceInvoker(httpMethod, path, ++pathIndex, contentType, accepts);
            else if (wildChildren != null) {
                for (Node wildcard : wildChildren) {
                    ResourceInvoker wildcardReturn = wildcard.findResourceInvoker(httpMethod, path, ++pathIndex, contentType, accepts);
                    if (wildcardReturn != null) return wildcardReturn;
                }
                return null;
            } else {
                return null;
            }
        }

        private ResourceInvoker match(String httpMethod, MediaType contentType, List<MediaType> accepts) {
            for (ResourceInvoker invoker : invokers) {
                if (invoker.matchByType(contentType, accepts) && invoker.getHttpMethods().contains(httpMethod))
                    return invoker;
            }
            return null;
        }


    }

    private Node root = new Node();
    private ProviderFactory providerFactory;

    public Registry(ProviderFactory providerFactory) {
        this.providerFactory = providerFactory;
    }

    public void addResourceFactory(ResourceFactory factory) {
        Class<?> clazz = factory.getScannableClass();
        Path basePath = clazz.getAnnotation(Path.class);
        for (Method method : clazz.getMethods()) {
            Path path = method.getAnnotation(Path.class);
            Set<String> httpMethods = IsHttpMethod.getHttpMethods(method);
            if (path == null && httpMethods == null) continue;

            String pathExpression = null;
            if (basePath != null) pathExpression = basePath.value();
            if (path != null)
                pathExpression = (pathExpression == null) ? path.value() : pathExpression + "/" + path.value();
            if (pathExpression == null) pathExpression = "";
            if (httpMethods == null) {
                throw new RuntimeException("@Path without an http method is not implemented yet: " + method);
            } else {
                ResourceInvoker invoker = new ResourceInvoker(clazz, method, factory, providerFactory, httpMethods);
                String[] paths = pathExpression.split("/");
                root.addChild(paths, 0, invoker);

            }

        }
    }

    public ResourceInvoker getResourceInvoker(String httpMethod, String path, MediaType contentType, List<MediaType> accepts) {
        if (path.startsWith("/")) path = path.substring(1);
        return root.findResourceInvoker(httpMethod, path.split("/"), 0, contentType, accepts);
    }
}
