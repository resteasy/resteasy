package org.jboss.resteasy.core.registry;

import org.jboss.resteasy.core.ResourceInvoker;
import org.jboss.resteasy.core.ResourceLocatorInvoker;
import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.DefaultOptionsMethodException;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.ResteasyUriInfo;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.util.WeightedMediaType;

import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.NotAllowedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.NotSupportedException;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SegmentNode
{
   public static final String RESTEASY_CHOSEN_ACCEPT = "RESTEASY_CHOSEN_ACCEPT";
   public static final String RESTEASY_SERVER_HAS_PRODUCES = "RESTEASY-SERVER-HAS-PRODUCES";
   public static final MediaType[] WILDCARD_ARRAY = {MediaType.WILDCARD_TYPE};
   public static final List<MediaType> DEFAULT_ACCEPTS = new ArrayList<MediaType>();

   static
   {
      DEFAULT_ACCEPTS.add(MediaType.WILDCARD_TYPE);
   }
   protected String segment;
   protected Map<String, SegmentNode> children = new HashMap<String, SegmentNode>();
   protected List<MethodExpression> targets = new ArrayList<MethodExpression>();
   protected boolean requestMatchingSet = false;
   protected boolean isJaxrs_2_0_RequestMatching = false;
   
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
      Collections.sort(potentials);

      if (!requestMatchingSet)
      {
         Map<Class<?>, Object> contextDataMap = ResteasyProviderFactory.getContextDataMap();
         ResteasyDeployment deployment = (ResteasyDeployment) contextDataMap.get(ResteasyDeployment.class);
         if (deployment != null)
         {
            isJaxrs_2_0_RequestMatching = deployment.isJaxrs_2_0_RequestMatching();
            requestMatchingSet = true;
         }
      }
      MethodExpression matchedExpression = null;
      List<Match> matches = new ArrayList<Match>();
      for (MethodExpression expression : potentials)
      {
         // We ignore locators if the first match was a resource method as per the spec Section 3, Step 2(h)
         if (matchedExpression != null && expression.isLocator()) continue;

         Pattern pattern = expression.getPattern();
         Matcher matcher = pattern.matcher(path);
         matcher.region(start, path.length());

         if (matcher.matches())
         {
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
                  // must find the end of the matched pattern
                  // and get the substring from 1st char thru end
                  // of matched chars
                  Pattern p = expression.getPattern();
                  Matcher m = p.matcher(path);
                  m.region(start, path.length());
                  String substring = path;
                  while(m.find()) {
                     String endText = m.group(m.groupCount());
                     if (endText != null && !endText.isEmpty()) {
                        int indx = path.indexOf(endText, length);
                        if (indx > -1) {
                           substring = path.substring(0, indx);
                        }
                     }
                  }

                  uriInfo.pushMatchedPath(substring);
                  uriInfo.pushMatchedURI(substring);
               }
               expression.populatePathParams(request, matcher, path);
               return invoker;
            }
            else
            {
               if (isJaxrs_2_0_RequestMatching)
               {
                  if (matchedExpression == null)
                  {
                     matchedExpression = expression;
                     matches.add(new Match(expression, matcher));   
                  }
                  else if (matchedExpression.compareTo(expression) == 0)
                  {
                     matches.add(new Match(expression, matcher));  
                  }
                  else
                  {
                     break;
                  }
               }
               else
               {
                  matches.add(new Match(expression, matcher));
                  matchedExpression = expression;
               }
            }
         }
      }
      if (matches.size() == 0)
      {
         throw new NotFoundException(Messages.MESSAGES.couldNotFindResourceForFullPath(request.getUri().getRequestUri()));
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

   public static class SortFactor
   {
      public float q = 1.0f;
      public float qs = 1.0f;
      public int d;
      public int dm;
      public String type;
      public String subtype;
      public Map<String, String> params;

      public boolean isWildcardType()
      {
         return type.equals(MediaType.MEDIA_TYPE_WILDCARD);
      }
      public boolean isWildcardSubtype()
      {
         return subtype.equals(MediaType.MEDIA_TYPE_WILDCARD);
      }

   }

   public static SortFactor createSortFactor(MediaType client, MediaType server)
   {
      SortFactor sortFactor = new SortFactor();
      if (client.isWildcardType() != server.isWildcardType())
      {
         sortFactor.type = (client.isWildcardType()) ? server.getType() : client.getType();
         sortFactor.d++;
      }
      else
      {
         sortFactor.type = client.getType();
      }
      if (client.isWildcardSubtype() != server.isWildcardSubtype())
      {
         sortFactor.subtype = (client.isWildcardSubtype()) ? server.getSubtype() : client.getSubtype();
         sortFactor.d++;
      }
      else
      {
         sortFactor.subtype = client.getSubtype();
      }
      String q = client.getParameters().get("q");
      if (q != null) sortFactor.q = Float.parseFloat(q);
      String qs = server.getParameters().get("qs");
      if (qs != null) sortFactor.qs = Float.parseFloat(qs);

      sortFactor.dm = 0;
      for (Map.Entry<String, String> entry : client.getParameters().entrySet())
      {
         String name = entry.getKey();
         if ("q".equals(name)
                 || "qs".equals(name)) continue;
         String val = server.getParameters().get(name);
         if (val == null)
         {
            sortFactor.dm++;
            continue;
         }
         if (!val.equals(entry.getValue()))
         {
            sortFactor.dm++;
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
            sortFactor.dm++;
            continue;
         }
         if (!val.equals(entry.getValue()))
         {
            sortFactor.dm++;
            continue;
         }
      }
      return sortFactor;
   }

   protected class SortEntry implements Comparable<SortEntry>
   {
      Match match;
      MediaType serverProduce;
      SortFactor consumes;
      SortFactor produces;

      public SortEntry(Match match, SortFactor consumes, SortFactor produces, MediaType serverProduce)
      {
         this.serverProduce = serverProduce;
         this.match = match;
         this.consumes = consumes;
         this.produces = produces;
      }

      public MediaType getAcceptType()
      {
         // take params from produce and type and subtype from sort factor
         // to define the returned media type
         Map<String, String> params = new HashMap<String, String>();
         for (Map.Entry<String, String> entry : serverProduce.getParameters().entrySet())
         {
            String name = entry.getKey();
            if ("q".equals(name)
                    || "qs".equals(name)) continue;
            params.put(name, entry.getValue());
         }
         Annotation[] annotations = match.expression.invoker.getMethod().getAnnotations();
         boolean hasProduces = false;
         for (Annotation annotation : annotations)
         {
            if (annotation instanceof Produces)
            {
               hasProduces = true;
               break;
            }
         }
         if (!hasProduces)
         {
            annotations = match.expression.invoker.getMethod().getClass().getAnnotations();
            for (Annotation annotation : annotations)
            {
               if (annotation instanceof Produces)
               {
                  hasProduces = true;
                  break;
               }
            }
         }
         if (hasProduces)
         {
            params.put(RESTEASY_SERVER_HAS_PRODUCES, "true"); 
         }
         return new MediaType(produces.type, produces.subtype, params);
      }


      @Override
      public int compareTo(SortEntry o)
      {
         if (consumes.isWildcardType() && !o.consumes.isWildcardType()) return 1;
         if (!consumes.isWildcardType() && o.consumes.isWildcardType()) return -1;
         if (consumes.isWildcardSubtype() && !o.consumes.isWildcardSubtype()) return 1;
         if (!consumes.isWildcardSubtype() && o.consumes.isWildcardSubtype()) return -1;

         if (consumes.q > o.consumes.q) return -1;
         if (consumes.q < o.consumes.q) return 1;

         if (consumes.qs > o.consumes.qs) return -1;
         if (consumes.qs < o.consumes.qs) return 1;

         if (consumes.d < o.consumes.d) return -1;
         if (consumes.d > o.consumes.d) return 1;

         if (consumes.dm < o.consumes.dm) return -1;
         if (consumes.dm > o.consumes.dm) return 1;

         if (produces.isWildcardType() && !o.produces.isWildcardType()) return 1;
         if (!produces.isWildcardType() && o.produces.isWildcardType()) return -1;
         if (produces.isWildcardSubtype() && !o.produces.isWildcardSubtype()) return 1;
         if (!produces.isWildcardSubtype() && o.produces.isWildcardSubtype()) return -1;

         if (produces.q > o.produces.q) return -1;
         if (produces.q < o.produces.q) return 1;

         if (produces.qs > o.produces.qs) return -1;
         if (produces.qs < o.produces.qs) return 1;

         if (produces.d < o.produces.d) return -1;
         if (produces.d > o.produces.d) return 1;

         if (produces.dm < o.produces.dm) return -1;
         if (produces.dm > o.produces.dm) return 1;

         return match.expression.compareTo(o.match.expression);
      }
   }

   public Match match(List<Match> matches, String httpMethod, HttpRequest request)
   {
      MediaType contentType = request.getHttpHeaders().getMediaType();

      List<MediaType> requestAccepts = request.getHttpHeaders().getAcceptableMediaTypes();
      List<WeightedMediaType> weightedAccepts = new ArrayList<WeightedMediaType>();
      for (MediaType accept : requestAccepts) weightedAccepts.add(WeightedMediaType.parse(accept));

      List<Match> list = new ArrayList<Match>();
      boolean methodMatch = false;
      boolean consumeMatch = false;

      // make a list of all compatible ResourceMethods
      for (Match match : matches)
      {

         ResourceMethodInvoker invoker = (ResourceMethodInvoker) match.expression.getInvoker();
         if (invoker.getHttpMethods().contains(httpMethod.toUpperCase()))
         {
            methodMatch = true;
            if (invoker.doesConsume(contentType))
            {
               consumeMatch = true;
               if (invoker.doesProduce(weightedAccepts))
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
            {
               allowed.addAll(((ResourceMethodInvoker) match.expression.getInvoker()).getHttpMethods());
            }

            if (httpMethod.equalsIgnoreCase("HEAD") && allowed.contains("GET"))
            {
               return match(matches, "GET", request);
            }

            if (allowed.contains("GET")) allowed.add("HEAD");
            allowed.add("OPTIONS");
            StringBuilder allowHeaders = new StringBuilder("");
            boolean first = true;
            for (String allow : allowed)
            {
               if (first) first = false;
               else allowHeaders.append(", ");
               allowHeaders.append(allow);
            }
            String allowHeaderValue = allowHeaders.toString();
            
            if (httpMethod.equals("OPTIONS"))
            {
              
               ResponseBuilder resBuilder =  Response.ok(allowHeaderValue.toString(),  MediaType.TEXT_PLAIN_TYPE).header(HttpHeaderNames.ALLOW, allowHeaderValue.toString());
               
               if (allowed.contains("PATCH"))
               {  
                  Set<MediaType> patchAccepts = new HashSet<MediaType>(8);
                  for (Match match : matches)
                  {
                     if (((ResourceMethodInvoker) match.expression.getInvoker()).getHttpMethods().contains("PATCH"))
                     {
                        patchAccepts.addAll(Arrays.asList(((ResourceMethodInvoker) match.expression.getInvoker())
                              .getConsumes()));
                     }
                  }
                  StringBuilder acceptPatch = new StringBuilder("");
                  first = true;
                  for (MediaType mediaType : patchAccepts)
                  {
                     if (first)
                        first = false;
                     else
                        acceptPatch.append(", ");
                     acceptPatch.append(mediaType.toString());
                  }
                  resBuilder.header(HttpHeaderNames.ACCEPT_PATCH, acceptPatch.toString());
               }
               throw new DefaultOptionsMethodException(Messages.MESSAGES.noResourceMethodFoundForOptions(), resBuilder.build());
            }
            else
            {
               Response res = Response.status(HttpResponseCodes.SC_METHOD_NOT_ALLOWED).header(HttpHeaderNames.ALLOW, allowHeaderValue).build();
               throw new NotAllowedException(Messages.MESSAGES.noResourceMethodFoundForHttpMethod(httpMethod), res);
            }
         }
         else if (!consumeMatch)
         {
            throw new NotSupportedException(Messages.MESSAGES.cannotConsumeContentType());
         }
         throw new NotAcceptableException(Messages.MESSAGES.noMatchForAcceptHeader());
      }
      //if (list.size() == 1) return list.get(0); //don't do this optimization as we need to set chosen accept
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
         List<SortFactor> consumeCombo = new ArrayList<SortFactor>();
         for (MediaType consume : consumes)
         {
            consumeCombo.add(createSortFactor(contentType, consume));
         }
         for (MediaType produce : produces)
         {
            List<MediaType> acceptableMediaTypes = requestAccepts;
            if (acceptableMediaTypes.size() == 0)
            {
               acceptableMediaTypes = DEFAULT_ACCEPTS;
            }
            for (MediaType accept : acceptableMediaTypes)
            {
               if (accept.isCompatible(produce))
               {
                  SortFactor sortFactor = createSortFactor(accept, produce);

                  for (SortFactor consume : consumeCombo)
                  {
                     final Method m = match.expression.getInvoker().getMethod();
                     sortList.add(new SortEntry(match, consume, sortFactor, produce));
                  }
               }

            }
         }
      }
      Collections.sort(sortList);
      SortEntry sortEntry = sortList.get(0);
      String[] mm = matchingMethods(sortList);
      if (mm != null)
      {
         LogMessages.LOGGER.multipleMethodsMatch(requestToString(request), mm);
      }
      request.setAttribute(RESTEASY_CHOSEN_ACCEPT, sortEntry.getAcceptType());
      return sortEntry.match;
   }

   protected void addExpression(MethodExpression expression)
   {
      targets.add(expression);
      Collections.sort(targets);

   }

   private String requestToString(HttpRequest request) {
      return "\"" + request.getHttpMethod() + " " + request.getUri().getPath() + "\"";
   }

   private String[] matchingMethods(List<SortEntry> sortList)
   {
      Set<Method> s = null;
      Iterator<SortEntry> it = sortList.iterator();
      SortEntry a;
      SortEntry b = it.next();
      Method first = b.match.expression.getInvoker().getMethod();
      while (it.hasNext())
      {
         a = b;
         b = it.next();
         if (a.compareTo(b) == 0)
         {
            if (s == null) {
               s = new HashSet<>();
               s.add(first);
            }
            s.add(b.match.expression.getInvoker().getMethod());
         }
         else
         {
            break;
         }
      }
      if (s != null && s.size() > 1) {
         String[] names = new String[s.size()];
         Iterator<Method> iterator = s.iterator();
         int i = 0;
         while (iterator.hasNext())
         {
            names[i++] = iterator.next().toString();
         }
         return names;
      }
      return null;
   }
}
