package org.jboss.keystone;

import org.jboss.resteasy.keystone.model.Project;
import org.jboss.resteasy.keystone.model.Role;
import org.jboss.resteasy.keystone.model.StoredUser;
import org.jboss.resteasy.keystone.server.SkeletonKeyApplication;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.Context;
import java.util.HashMap;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@ApplicationPath("/")
public class Server extends Application
{
   protected SkeletonKeyApplication app;

   public Server(@Context Configurable confgurable)
   {
      app = new SkeletonKeyApplication(confgurable);

      try
      {
         StoredUser admin = new StoredUser();
         admin.setName("Bill");
         admin.setId("1");
         admin.setUsername("admin");
         HashMap<String, String> creds = new HashMap<String, String>();
         creds.put("password", "geheim");
         admin.setCredentials(creds);
         app.getUsers().create(admin);

         Project project = new Project();
         project.setName("Skeleton Key");
         project.setEnabled(true);
         app.getProjects().createProject(project);

         Role adminRole = new Role();
         adminRole.setName("admin");
         app.getRoles().create(adminRole);

         app.getProjects().addUserRole(project.getId(), admin.getId(), adminRole.getId());

         project = new Project();
         project.setId("42");
         project.setName("Skeleton App");
         project.setEnabled(true);
         app.getProjects().createProject(project);

         Role userRole = new Role();
         userRole.setName("user");
         app.getRoles().create(userRole);

         StoredUser user = new StoredUser();
         user.setName("Some User");
         user.setUsername("someuser");
         creds = new HashMap<String, String>();
         creds.put("password", "geheim");
         user.setCredentials(creds);
         app.getUsers().create(user);


         app.getProjects().addUserRole(project.getId(), user.getId(), userRole.getId());

         StoredUser superuser = new StoredUser();
         superuser.setName("Super User");
         superuser.setUsername("superuser");
         creds = new HashMap<String, String>();
         creds.put("password", "geheim");
         superuser.setCredentials(creds);
         app.getUsers().create(superuser);


         app.getProjects().addUserRole(project.getId(), superuser.getId(), adminRole.getId());
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }


   }

   @Override
   public Set<Object> getSingletons()
   {
      return app.getSingletons();
   }
}
