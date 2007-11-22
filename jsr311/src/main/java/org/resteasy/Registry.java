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
        private List<ResourceMethod> invokers = new ArrayList<ResourceMethod>();
        private List<Node> wildChildren = new ArrayList<Node>();
        private Map<String, Node> children = new HashMap<String, Node>();

        public Node() {
        }

        public void addChild(String[] path, int pathIndex, ResourceMethod invoker) {
            Matcher matcher = PathHelper.URI_TEMPLATE_PATTERN.matcher(path[pathIndex]);
            if (matcher.matches()) {
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

        public ResourceMethod findResourceInvoker(String httpMethod, String[] path, int pathIndex, MediaType contentType, List<MediaType> accepts) {
            if (pathIndex >= path.length) return match(httpMethod, contentType, accepts);
            else return findChild(httpMethod, path, pathIndex, contentType, accepts);
        }

        private ResourceMethod findChild(String httpMethod, String[] path, int pathIndex, MediaType contentType, List<MediaType> accepts) {
            Node next = children.get(path[pathIndex]);
            if (next != null) return next.findResourceInvoker(httpMethod, path, ++pathIndex, contentType, accepts);
            else if (wildChildren != null) {
                for (Node wildcard : wildChildren) {
                    ResourceMethod wildcardReturn = wildcard.findResourceInvoker(httpMethod, path, ++pathIndex, contentType, accepts);
                    if (wildcardReturn != null) return wildcardReturn;
                }
                return null;
            } else {
                return null;
            }
        }

        private ResourceMethod match(String httpMethod, MediaType contentType, List<MediaType> accepts) {
            for (ResourceMethod invoker : invokers) {
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
        addResourceFactory(factory, null);
    }

    public void addResourceFactory(ResourceFactory factory, String base) {
        Class<?> clazz = factory.getScannableClass();
        Path classBasePath = clazz.getAnnotation(Path.class);
        String classBase = (classBasePath == null) ? null : classBasePath.value();
        if (base == null) base = classBase;
        else if (classBase != null) base = base + "/" + classBase;

        for (Method method : clazz.getMethods()) {
            Path path = method.getAnnotation(Path.class);
            Set<String> httpMethods = IsHttpMethod.getHttpMethods(method);
            if (path == null && httpMethods == null) continue;

            String pathExpression = null;
            if (base != null) pathExpression = base;
            if (path != null)
                pathExpression = (pathExpression == null) ? path.value() : pathExpression + "/" + path.value();
            if (pathExpression == null) pathExpression = "";
            if (httpMethods == null) {
                ResourceLocator locator = new ResourceLocator(pathExpression, factory, method, providerFactory);
                addResourceFactory(locator, pathExpression);
            } else {
                ResourceMethod invoker = new ResourceMethod(pathExpression, clazz, method, factory, providerFactory, httpMethods);
                String[] paths = pathExpression.split("/");
                root.addChild(paths, 0, invoker);

            }

        }
    }

    public ResourceMethod getResourceInvoker(String httpMethod, String path, MediaType contentType, List<MediaType> accepts) {
        if (path.startsWith("/")) path = path.substring(1);
        return root.findResourceInvoker(httpMethod, path.split("/"), 0, contentType, accepts);
    }
}
