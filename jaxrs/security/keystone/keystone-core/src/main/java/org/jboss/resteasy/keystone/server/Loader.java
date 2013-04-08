package org.jboss.resteasy.keystone.server;

import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.infinispan.Cache;
import org.jboss.resteasy.keystone.model.IdentityStore;
import org.jboss.resteasy.keystone.model.Role;
import org.jboss.resteasy.keystone.model.StoredProject;
import org.jboss.resteasy.keystone.model.StoredUser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Loader
{
   final ObjectMapper DEFAULT_MAPPER = new ObjectMapper();
   final ObjectMapper WRAPPED_MAPPER = new ObjectMapper();

   public Loader()
   {
      DEFAULT_MAPPER.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
      DEFAULT_MAPPER.enable(SerializationConfig.Feature.INDENT_OUTPUT);
      DEFAULT_MAPPER.enable(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
   }

   public String export(Cache cache)
   {
      IdentityStore store = toStore(cache);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      write(baos, store);
      return new String(baos.toByteArray());
   }

   public IdentityStore importStore(InputStream is)
   {
      try
      {
         JsonParser jp = DEFAULT_MAPPER.getJsonFactory().createJsonParser(is);
         return DEFAULT_MAPPER.readValue(jp, IdentityStore.class);
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   public void importStore(InputStream is, Cache cache)
   {
      IdentityStore store = importStore(is);
      importStore(store, cache);
   }


   public void importStore(IdentityStore store, Cache cache)
   {
      if (store.getRoles() != null)
      {
         for (Role role : store.getRoles())
         {
            cache.put("/roles/" + role.getId(), role);
         }
      }
      if (store.getUsers() != null)
      {
         for (StoredUser user : store.getUsers())
         {
            cache.put("/users/" + user.getId(), user);
         }
      }

      if (store.getProjects() != null)
      {

         for (StoredProject project : store.getProjects())
         {
            cache.put("/projects/" + project.getProject().getId(), project);
         }
      }
   }

   public void export(Cache cache, OutputStream os)
   {
      IdentityStore store = toStore(cache);
      write(os, store);

   }

   private void write(OutputStream os, IdentityStore store)
   {
      try
      {
         JsonGenerator jg = DEFAULT_MAPPER.getJsonFactory().createJsonGenerator(os, JsonEncoding.UTF8);
         DEFAULT_MAPPER.writeValue(jg, store);
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   public IdentityStore toStore(Cache cache)
   {
      List<StoredUser> users = new ArrayList<StoredUser>();
      List<Role> roles = new ArrayList<Role>();
      List<StoredProject> projects = new ArrayList<StoredProject>();
      IdentityStore store = new IdentityStore();
      for (Object value : cache.values())
      {
         if (value instanceof StoredUser)
         {
            users.add((StoredUser) value);
            store.setUsers(users);
         } else if (value instanceof Role)
         {
            roles.add((Role) value);
            store.setRoles(roles);
         } else if (value instanceof StoredProject)
         {
            projects.add((StoredProject) value);
            store.setProjects(projects);
         }
      }
      return store;
   }

}
