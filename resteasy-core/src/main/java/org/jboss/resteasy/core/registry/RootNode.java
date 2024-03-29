package org.jboss.resteasy.core.registry;

import static org.jboss.resteasy.core.registry.SegmentNode.RESTEASY_CHOSEN_ACCEPT;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;

import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.specimpl.ResteasyUriInfo;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.ResourceInvoker;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class RootNode {
    protected SegmentNode root = new SegmentNode("");
    protected int size = 0;
    protected MultivaluedMap<String, MethodExpression> bounded = new MultivaluedHashMap<String, MethodExpression>();
    protected ConcurrentHashMap<MatchCache.Key, MatchCache> cache = new ConcurrentHashMap<>();
    private static int CACHE_SIZE = 2048;
    private static boolean CACHE = true;
    static {
        if (System.getSecurityManager() == null) {
            CACHE = Boolean.parseBoolean(System.getProperty(ResteasyContextParameters.RESTEASY_MATCH_CACHE_ENABLED, "true"));
            CACHE_SIZE = Integer.getInteger(ResteasyContextParameters.RESTEASY_MATCH_CACHE_SIZE, 2048);
        } else {
            CACHE = AccessController.doPrivileged((PrivilegedAction<Boolean>) () -> Boolean
                    .parseBoolean(System.getProperty(ResteasyContextParameters.RESTEASY_MATCH_CACHE_ENABLED, "true")));
            CACHE_SIZE = AccessController.doPrivileged((PrivilegedAction<Integer>) () -> Integer
                    .getInteger(ResteasyContextParameters.RESTEASY_MATCH_CACHE_SIZE, 2048));
        }
    }

    public int getSize() {
        return size;
    }

    public MultivaluedMap<String, ResourceInvoker> getBounded() {
        MultivaluedHashMap<String, ResourceInvoker> rtn = new MultivaluedHashMap<String, ResourceInvoker>();
        for (Map.Entry<String, List<MethodExpression>> entry : bounded.entrySet()) {
            for (MethodExpression exp : entry.getValue()) {
                rtn.add(entry.getKey(), exp.getInvoker());
            }
        }
        return rtn;
    }

    public ResourceInvoker match(HttpRequest request, int start) {
        if (!CACHE || (request.getHttpHeaders().getMediaType() != null
                && !request.getHttpHeaders().getMediaType().getParameters().isEmpty())) {
            return root.match(request, start).invoker;
        }
        MatchCache.Key key = new MatchCache.Key(request, start);
        MatchCache match = cache.get(key);
        if (match != null) {
            //System.out.println("*** cache hit: " + key.method + " " + key.path);
            request.setAttribute(RESTEASY_CHOSEN_ACCEPT, match.chosen);
            // We need to add the matched request template
            ((ResteasyUriInfo) request.getUri()).addMatchedResourceTemplate(match.pathExpression());
        } else {
            match = root.match(request, start);
            if (match.match != null && match.match.expression.getNumGroups() == 0
                    && match.invoker instanceof ResourceMethodInvoker) {
                //System.out.println("*** caching: " + key.method + " " + key.path);
                match.match = null;
                if (cache.size() >= CACHE_SIZE) {
                    cache.clear();
                }
                cache.putIfAbsent(key, match);
            }
        }
        return match.invoker;
    }

    public void removeBinding(String path, Method method) {
        List<MethodExpression> expressions = bounded.get(path);
        if (expressions == null)
            return;
        for (MethodExpression expression : expressions) {
            ResourceInvoker invoker = expression.getInvoker();
            if (invoker.getMethod().equals(method)) {
                expression.parent.targets.remove(expression);
                expressions.remove(expression);
                if (expressions.size() == 0)
                    bounded.remove(path);
                size--;
                if (invoker instanceof ResourceMethodInvoker) {
                    ((ResourceMethodInvoker) invoker).cleanup();
                }
                return;
            }
        }
    }

    public void addInvoker(String path, ResourceInvoker invoker) {
        MethodExpression expression = addExpression(path, invoker);
        size++;
        bounded.add(path, expression);
    }

    protected MethodExpression addExpression(String path, ResourceInvoker invoker) {
        if (path.startsWith("/"))
            path = path.substring(1);
        if (path.endsWith("/"))
            path = path.substring(0, path.length() - 1);
        if ("".equals(path)) {
            if (invoker instanceof ResourceMethodInvoker) {
                MethodExpression expression = new MethodExpression(root, "", invoker);
                root.addExpression(expression);
                return expression;

            } else {
                MethodExpression expression = new MethodExpression(root, "", invoker, "(.*)");
                root.addExpression(expression);
                return expression;
            }
        }
        MethodExpression exp;
        //Matcher param = PathHelper.URI_PARAM_PATTERN.matcher(path);
        int expidx = path.indexOf('{');
        if (expidx > -1) {
            int i = expidx;
            while (i - 1 > -1) {
                if (path.charAt(i - 1) == '/') {
                    break;
                }
                i--;
            }
            String staticPath = null;
            if (i > 0)
                staticPath = path.substring(0, i - 1);
            SegmentNode node = root;
            if (staticPath != null) {
                String[] split = staticPath.split("/");
                for (String segment : split) {
                    SegmentNode tmp = node.children.get(segment);
                    if (tmp == null) {
                        tmp = new SegmentNode(segment);
                        node.children.put(segment, tmp);
                    }
                    node = tmp;
                }
            }
            if (invoker instanceof ResourceMethodInvoker) {
                exp = new MethodExpression(node, path, invoker);
            } else {
                exp = new MethodExpression(node, path, invoker, "(/.+)?");

            }
            node.addExpression(exp);
        } else {
            String[] split = path.split("/");
            SegmentNode node = root;
            for (String segment : split) {
                SegmentNode tmp = node.children.get(segment);
                if (tmp == null) {
                    tmp = new SegmentNode(segment);
                    node.children.put(segment, tmp);
                }
                node = tmp;
            }
            if (invoker instanceof ResourceMethodInvoker) {
                exp = new MethodExpression(node, path, invoker);
                node.addExpression(exp);
            } else {
                exp = new MethodExpression(node, path, invoker, "(.*)");
                node.addExpression(exp);
            }
        }
        return exp;
    }
}
