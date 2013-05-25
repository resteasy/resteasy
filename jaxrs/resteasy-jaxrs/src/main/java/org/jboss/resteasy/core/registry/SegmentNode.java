package org.jboss.resteasy.core.registry;

import org.jboss.resteasy.core.ResourceInvoker;
import org.jboss.resteasy.core.ResourceLocatorInvoker;
import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.spi.DefaultOptionsMethodException;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.ResteasyUriInfo;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.util.WeightedMediaType;

import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.NotAllowedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.NotSupportedException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SegmentNode
{
   public static final String RESTEASY_CHOSEN_ACCEPT = "RESTEASY_CHOSEN_ACCEPT";
   public static final MediaType[] WILDCARD_ARRAY = {MediaType.WILDCARD_TYPE};
   public static final List<MediaType> DEFAULT_ACCEPTS = new ArrayList<MediaType>();

   static
   {
      DEFAULT_ACCEPTS.add(MediaType.WILDCARD_TYPE);
   }
   protected String segment;
   protected Map<String, SegmentNode> children = new HashMap<String, SegmentNode>();
   protected List<MethodExpression> targets = new ArrayList<MethodExpression>();

   public SegmentNode(String segment)
   {
      this.segment = segment;
   }

   protected static class Match
   {
      MethodExpression expression;
      Matcher matcher;

      public Match(MethodExpression expression, Matcher matcher)
      {
         this.expression = expression;
         this.matcher = matcher;
      }
   }

   public ResourceInvoker match(HttpRequest request, int start)
   {
      String path = request.getUri().getMatchingPath();
      if (start < path.length() && path.charAt(start) == '/') start++;
      List<MethodExpression> potentials = new ArrayList<MethodExpression>();
      potentials(path, start, potentials);

      boolean expressionMatched = false;
      List<Match> matches = new ArrayList<Match>();
      for (MethodExpression expression : potentials)
      {
         // We ignore locators if the first match was a resource method as per the spec Section 3, Step 2(h)
         if (expressionMatched && expression.isLocator()) continue;

         Pattern pattern = expression.getPattern();
         Matcher matcher = pattern.matcher(path);
         matcher.region(start, path.length());

         if (matcher.matches())
         {
            expressionMatched = true;
            ResourceInvoker invoker = expression.getInvoker();
            if (invoker instanceof ResourceLocatorInvoker)
            {
               ResteasyUriInfo uriInfo = (ResteasyUriInfo) request.getUri();
               int length = matcher.start(expression.getNumGroups() + 1);
               if (length == -1)
               {
                  uriInfo.pushMatchedPath(path);
                  uriInfo.pushMatchedURI(path);
               }
               else
               {
                  String substring = path.substring(0, length);
                  uriInfo.pushMatchedPath(substring);
                  uriInfo.pushMatchedURI(substring);
               }
               expression.populatePathParams(request, matcher, path);
               return invoker;
            }
            else
            {
               matches.add(new Match(expression, matcher));
            }
         }
      }
      if (matches.size() == 0)
      {
         throw new NotFoundException("Could not find resource for full path: " + request.getUri().getRequestUri());
      }
      Match match = match(matches, request.getHttpMethod(), request);
      match.expression.populatePathParams(request, match.matcher, path);
      return match.expression.getInvoker();

   }

   public void potentials(String path, int start, List<MethodExpression> matches)
   {
      if (start == path.length()) // we've reached end of string
      {
         matches.addAll(targets);
         return;
      }

      if (start < path.length())
      {
         String simpleSegment = null;
         int endOfSegmentIndex = path.indexOf('/', start);
         if (endOfSegmentIndex > -1) simpleSegment = path.substring(start, endOfSegmentIndex);
         else simpleSegment = path.substring(start);
         SegmentNode child = children.get(simpleSegment);
         if (child != null)
         {
            int next = start + simpleSegment.length();
            if (endOfSegmentIndex > -1) next++; // go past '/'
            child.potentials(path, next, matches);
         }
      }
      for (MethodExpression exp : targets)
      {
         // skip any static matches as they will not match anyways
         if (exp.getNumGroups() > 0 || exp.getInvoker() instanceof ResourceLocatorInvoker)
         {
            matches.add(exp);
         }
      }
   }

   public static MediaType createSortFactor(MediaType client, MediaType server)
   {
      int d = 0;
      String type;
      String subtype;
      if (client.isWildcardType() != server.isWildcardType())
      {
         type = (client.isWildcardType()) ? server.getType() : client.getType();
         d++;
      }
      else
      {
         type = client.getType();
      }
      if (client.isWildcardSubtype() != server.isWildcardSubtype())
      {
         subtype = (client.isWildcardSubtype()) ? server.getSubtype() : client.getSubtype();
         d++;
      }
      else
      {
         subtype = client.getSubtype();
      }
      Map<String, String> params = new HashMap<String, String>();
      String q = client.getParameters().get("q");
      if (q != null) params.put("q", q);
      String qs = server.getParameters().get("qs");
      if (qs != null) params.put("qs", qs);
      params.put("d", Integer.toString(d));

      int dm = 0;
      for (Map.Entry<String, String> entry : client.getParameters().entrySet())
      {
         String name = entry.getKey();
         if ("q".equals(name)
                 || "qs".equals(name)) continue;
         String val = server.getParameters().get(name);
         if (val == null)
         {
            dm++;
            continue;
         }
         if (!val.equals(entry.getValue()))
         {
            dm++;
            continue;
         }
      }

      for (Map.Entry<String, String> entry : server.getParameters().entrySet())
      {
         String name = entry.getKey();
         if ("q".equals(name)
                 || "qs".equals(name)) continue;
         String val = client.getParameters().get(name);
         if (val == null)
         {
            dm++;
            continue;
         }
         if (!val.equals(entry.getValue()))
         {
            dm++;
            continue;
         }
      }
      params.put("dm", Integer.toString(dm));
      return new MediaType(type, subtype, params);

   }

   protected class SortEntry implements Comparable<SortEntry>
   {
      Match match;
      MediaType accept;
      MediaType consumes;
      float qConsumes = 1.0f;
      float qsConsumes = 1.0f;
      int dConsumes = 0;
      int dmConsumes = 0;
      MediaType produces;
      float qProduces = 1.0f;
      float qsProduces = 1.0f;
      int dProduces = 0;
      int dmProduces = 0;

      public SortEntry(Match match, MediaType consumes, MediaType produces, MediaType accept)
      {
         this.accept = accept;
         this.match = match;
         this.consumes = consumes;
         String q = consumes.getParameters().get("q");
         if (q != null) qConsumes = Float.parseFloat(q);
         String qs = consumes.getParameters().get("qs");
         if (qs != null) qsConsumes = Float.parseFloat(qs);
         String d = consumes.getParameters().get("d");
         if (d != null) dConsumes = Integer.parseInt(d);
         String dm = consumes.getParameters().get("dm");
         if (dm != null) dmConsumes = Integer.parseInt(dm);


         this.produces = produces;
         q = produces.getParameters().get("q");
         if (q != null) qProduces = Float.parseFloat(q);
         qs = produces.getParameters().get("qs");
         if (qs != null) qsProduces = Float.parseFloat(qs);
         d = produces.getParameters().get("d");
         if (d != null) dProduces = Integer.parseInt(d);
         dm = produces.getParameters().get("dm");
         if (dm != null) dmProduces = Integer.parseInt(dm);
      }


      @Override
      public int compareTo(SortEntry o)
      {
         if (consumes.isWildcardType() && !o.consumes.isWildcardType()) return 1;
         if (!consumes.isWildcardType() && o.consumes.isWildcardType()) return -1;
         if (consumes.isWildcardSubtype() && !o.consumes.isWildcardSubtype()) return 1;
         if (!consumes.isWildcardSubtype() && o.consumes.isWildcardSubtype()) return -1;

         if (qConsumes > o.qConsumes) return -1;
         if (qConsumes < o.qConsumes) return 1;

         if (qsConsumes > o.qsConsumes) return -1;
         if (qsConsumes < o.qsConsumes) return 1;

         if (dConsumes < o.dConsumes) return -1;
         if (dConsumes > o.dConsumes) return 1;

         if (dmConsumes < o.dmConsumes) return -1;
         if (dmConsumes > o.dmConsumes) return 1;

         if (produces.isWildcardType() && !o.produces.isWildcardType()) return 1;
         if (!produces.isWildcardType() && o.produces.isWildcardType()) return -1;
         if (produces.isWildcardSubtype() && !o.produces.isWildcardSubtype()) return 1;
         if (!produces.isWildcardSubtype() && o.produces.isWildcardSubtype()) return -1;

         if (qProduces > o.qProduces) return -1;
         if (qProduces < o.qProduces) return 1;

         if (qsProduces > o.qsProduces) return -1;
         if (qsProduces < o.qsProduces) return 1;

         if (dProduces < o.dProduces) return -1;
         if (dProduces > o.dProduces) return 1;

         if (dmProduces < o.dmProduces) return -1;
         if (dmProduces > o.dmProduces) return 1;

         return match.expression.compareTo(o.match.expression);
      }
   }

   public Match match(List<Match> matches, String httpMethod, HttpRequest request)
   {
      MediaType contentType = request.getHttpHeaders().getMediaType();

      List<MediaType> oldaccepts = request.getHttpHeaders().getAcceptableMediaTypes();
      List<WeightedMediaType> accepts = new ArrayList<WeightedMediaType>();
      for (MediaType accept : oldaccepts) accepts.add(WeightedMediaType.parse(accept));

      List<Match> list = new ArrayList<Match>();

      boolean methodMatch = false;
      boolean consumeMatch = false;

      // make a list of all compatible ResourceMethods
      for (Match match : matches)
      {

         ResourceMethodInvoker invoker = (ResourceMethodInvoker) match.expression.getInvoker();
         if (invoker.getHttpMethods().contains(httpMethod))
         {
            methodMatch = true;
            if (invoker.doesConsume(contentType))
            {
               consumeMatch = true;
               if (invoker.doesProduce(accepts))
               {
                  list.add(match);
               }
            }

         }
      }

      if (list.size() == 0)
      {
         if (!methodMatch)
         {
            HashSet<String> allowed = new HashSet<String>();
            for (Match match : matches)
               allowed.addAll(((ResourceMethodInvoker) match.expression.getInvoker()).getHttpMethods());

            if (httpMethod.equalsIgnoreCase("HEAD") && allowed.contains("GET"))
            {
               return match(matches, "GET", request);
            }

            if (allowed.contains("GET")) allowed.add("HEAD");
            allowed.add("OPTIONS");
            String allowHeaderValue = "";
            boolean first = true;
            for (String allow : allowed)
            {
               if (first) first = false;
               else allowHeaderValue += ", ";
               allowHeaderValue += allow;
            }

            if (httpMethod.equals("OPTIONS"))
            {
               Response res = Response.ok(allowHeaderValue,  MediaType.TEXT_PLAIN_TYPE).header(HttpHeaderNames.ALLOW, allowHeaderValue).build();
               throw new DefaultOptionsMethodException("No resource method found for options, return OK with Allow header", res);
            }
            else
            {
               Response res = Response.status(HttpResponseCodes.SC_METHOD_NOT_ALLOWED).header(HttpHeaderNames.ALLOW, allowHeaderValue).build();
               throw new NotAllowedException("No resource method found for " + httpMethod + ", return 405 with Allow header", res);
            }
         }
         else if (!consumeMatch)
         {
            throw new NotSupportedException("Cannot consume content type");
         }
         throw new NotAcceptableException("No match for accept header");
      }
      if (list.size() == 1) return list.get(0);
      List<SortEntry> sortList = new ArrayList<SortEntry>();
      for (Match match : list)
      {
         ResourceMethodInvoker invoker = (ResourceMethodInvoker) match.expression.getInvoker();
         if (contentType == null) contentType = MediaType.WILDCARD_TYPE;

         MediaType[] consumes = invoker.getConsumes();
         if (consumes.length == 0)
         {
            consumes = WILDCARD_ARRAY;
         }
         MediaType[] produces = invoker.getProduces();
         if (produces.length == 0)
         {
            produces = WILDCARD_ARRAY;
         }
         List<MediaType> consumeCombo = new ArrayList<MediaType>();
         for (MediaType consume : consumes)
         {
            consumeCombo.add(createSortFactor(contentType, consume));
         }
         for (MediaType produce : produces)
         {
            List<MediaType> acceptableMediaTypes = request.getHttpHeaders().getAcceptableMediaTypes();
            if (acceptableMediaTypes.size() == 0)
            {
               acceptableMediaTypes = DEFAULT_ACCEPTS;
            }
            for (MediaType accept : acceptableMediaTypes)
            {
               if (accept.isCompatible(produce))
               {
                  MediaType sortFactor = createSortFactor(accept, produce);
                  // take params from produce and type and subtype from sort factor
                  // to define the returned media type
                  Map<String, String> params = new HashMap<String, String>();
                  for (Map.Entry<String, String> entry : produce.getParameters().entrySet())
                  {
                     String name = entry.getKey();
                     if ("q".equals(name)
                             || "qs".equals(name)) continue;
                     params.put(name, entry.getValue());
                  }
                  MediaType chosen = new MediaType(sortFactor.getType(), sortFactor.getSubtype(), params);

                  for (MediaType consume : consumeCombo)
                  {
                     sortList.add(new SortEntry(match, consume, sortFactor, chosen));
                  }
               }

            }
         }
      }
      Collections.sort(sortList);
      SortEntry sortEntry = sortList.get(0);
      request.setAttribute(RESTEASY_CHOSEN_ACCEPT, sortEntry.accept);
      return sortEntry.match;
   }

   protected void addExpression(MethodExpression expression)
   {
      targets.add(expression);
      Collections.sort(targets);

   }


}
