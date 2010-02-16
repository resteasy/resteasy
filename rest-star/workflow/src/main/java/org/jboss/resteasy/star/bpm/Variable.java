package org.jboss.resteasy.star.bpm;

import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.MediaTypeMatcher;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Variable implements Externalizable
{
   private transient MediaTypeMatcher<Representation> matcher = new MediaTypeMatcher<Representation>();
   private transient ResteasyProviderFactory factory;
   private transient Representation transformableRepresentation;
   private transient Representation initialRepresentation;
   private MultivaluedMap<String, String> entityHeaders = new MultivaluedMapImpl<String, String>();

   public static Set<String> allowable = new HashSet<String>();

   static
   {
      allowable.add("Link");
   }

   public Variable()
   {
   }

   public Variable(MediaType mediaType, MultivaluedMap<String, String> headers, byte[] body)
   {
      for (String allowed : allowable)
      {
         if (headers.containsKey(allowed))
         {
            entityHeaders.put(allowed, headers.get(allowed));
         }
      }
      initialRepresentation = new Representation(mediaType, body);
      matcher.setRepresentations(new ConcurrentHashMap<MediaType, Representation>());
      matcher.getRepresentations().put(initialRepresentation.getMediaType(), initialRepresentation);
   }

   public void writeExternal(ObjectOutput os) throws IOException
   {
      HashMap tmp = new HashMap();
      for (Map.Entry<MediaType, Representation> entry : matcher.getRepresentations().entrySet())
      {
         tmp.put(entry.getKey().toString(), entry.getValue());
      }
      os.writeObject(tmp);
      if (transformableRepresentation == null) os.writeUTF("");
      else os.writeUTF(transformableRepresentation.getMediaType().toString());
      os.writeUTF(initialRepresentation.getMediaType().toString());
      os.writeObject(entityHeaders);
   }

   public void readExternal(ObjectInput is) throws IOException, ClassNotFoundException
   {
      Map<String, Representation> representations = (Map<String, Representation>)is.readObject();
      String type = is.readUTF();
      if (type != null  && !type.equals(""))
      {
         transformableRepresentation = representations.get(type);
      }
      String initial = is.readUTF();
      initialRepresentation = representations.get(initial);

      entityHeaders = (MultivaluedMap<String, String>)is.readObject();

      matcher = new MediaTypeMatcher<Representation>();
      matcher.setRepresentations(new ConcurrentHashMap<MediaType, Representation>());

      for (Representation rep : representations.values())
      {
         matcher.getRepresentations().put(rep.getMediaType(), rep);   
      }

   }

   public MultivaluedMap<String, String> getEntityHeaders()
   {
      return entityHeaders;
   }

   /**
    *
    *
    * @param accepts this parameter is assumed to be sorted by preference
    * @return
    */
   public Representation match(List<MediaType> accepts)
   {
      Representation matched = matcher.match(accepts);

      // we may later want to transform to the perfect match if possible, but that's probably overkill for most uses
      if (matched != null) return matched;

      for (MediaType type : accepts)
      {
         if (type.isWildcardType() || type.isWildcardSubtype()) continue;
         matched = transform(type);
         if (matched != null) return matched;
      }
      
      return null;
   }

   public Representation getRepresentation(MediaType type)
   {
      return matcher.getRepresentations().get(type);
   }

   private Representation transform(MediaType mediaType)
   {
      if (transformableRepresentation != null)
      {
         Representation transformation = transformableRepresentation.transformTo(mediaType, factory);
         if (transformation == null) return null;
         matcher.getRepresentations().put(mediaType, transformation);
         return transformation;
      }
      return null;
   }

   public Representation getInitialRepresentation()
   {
      return initialRepresentation;
   }

   public void addRepresentation(Representation rep)
   {
      matcher.getRepresentations().put(rep.getMediaType(), rep);
   }

   public ResteasyProviderFactory getFactory()
   {
      return factory;
   }

   public void setFactory(ResteasyProviderFactory factory)
   {
      this.factory = factory;
   }

   public void setTransformableRepresentation(MediaType from)
   {
      this.transformableRepresentation = matcher.getRepresentations().get(from);
      if (this.transformableRepresentation == null)
      {
         throw new RuntimeException("You must add the transformable representation before setting it");
      }
   }

   public Set<MediaType> getMediaTypes()
   {
      HashSet<MediaType> set = new HashSet<MediaType>();
      set.addAll(matcher.getRepresentations().keySet());
      return set;
   }
}
