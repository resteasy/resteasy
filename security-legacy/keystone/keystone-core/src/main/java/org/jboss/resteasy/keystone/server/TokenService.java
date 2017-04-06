package org.jboss.resteasy.keystone.server;

import org.infinispan.Cache;
import org.jboss.resteasy.keystone.core.i18n.LogMessages;
import org.jboss.resteasy.keystone.core.i18n.Messages;
import org.jboss.resteasy.keystone.model.Access;
import org.jboss.resteasy.keystone.model.Authentication;
import org.jboss.resteasy.keystone.model.Project;
import org.jboss.resteasy.keystone.model.Role;
import org.jboss.resteasy.keystone.model.Roles;
import org.jboss.resteasy.keystone.model.StoredUser;
import org.jboss.resteasy.keystone.model.UrlToken;
import org.jboss.resteasy.security.smime.SignedOutput;
import org.jboss.resteasy.util.Base64;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@SuppressWarnings("rawtypes")
@Path("/tokens")
public class TokenService
{
   private Cache cache;
   private long expiration;
   private TimeUnit expirationUnit;
   private PrivateKey privateKey;
   private X509Certificate certificate;

   private ProjectsService projects;
   private UsersService users;

   public TokenService(Cache cache, long expiration, TimeUnit expirationUnit, ProjectsService projects, UsersService users)
   {
      this.cache = cache;
      this.expiration = expiration;
      this.expirationUnit = expirationUnit;
      this.projects = projects;
      this.users = users;
   }

   public X509Certificate getCertificate()
   {
      return certificate;
   }

   public void setPrivateKey(PrivateKey privateKey)
   {
      this.privateKey = privateKey;
   }

   public void setCertificate(X509Certificate certificate)
   {
      this.certificate = certificate;
   }

   @Path("signed")
   @Produces("text/plain")
   @Consumes("application/json")
   @POST
   public SignedOutput createSigned(Authentication auth) throws Exception
   {
      if (privateKey == null || certificate == null)
      {
         LogMessages.LOGGER.warn(Messages.MESSAGES.privateKeyOrCertificateNotSet());
         throw new WebApplicationException(500);
      }
      Access access = create(auth);
      SignedOutput signed = new SignedOutput(access, "application/json");
      signed.setPrivateKey(privateKey);
      signed.setCertificate(certificate);
      return signed;
   }

   @SuppressWarnings("unchecked")
   @POST
   @Consumes("application/json")
   @Produces("application/json")
   public Access create(Authentication auth) throws Exception
   {
      String projectId = auth.getProjectId();
      Project project = null;
      if (projectId == null)
      {
         if (auth.getProjectName() == null) throw new WebApplicationException(401);
         List<Project> list = projects.getProjects(auth.getProjectName()).getList();
         if (list.size() != 1) throw new WebApplicationException(401);
         project = list.get(0);
         projectId = project.getId();
      }
      else
      {
         project = projects.getProject(projectId);
         if (project == null) throw new WebApplicationException(401);
      }
      String userId = auth.getPasswordCredentials().getUser_id();
      if (userId == null)
      {
         String username = auth.getPasswordCredentials().getUsername();
         if (username == null) throw new WebApplicationException(401);
         userId = projects.getUserIdByName(projectId, username);
      }
      if (userId == null) throw new WebApplicationException(401);
      StoredUser user = users.getStoredUser(userId);
      if (user == null) throw new WebApplicationException(401);
      String password = auth.getPasswordCredentials().getPassword();
      MessageDigest digest = MessageDigest.getInstance("MD5");
      String hashPassword = Base64.encodeBytes(digest.digest(password.getBytes(StandardCharsets.UTF_8)));
      String savedPassword = user.getCredentials().get("password-hash");
      if (!hashPassword.equals(savedPassword)) throw new WebApplicationException(401);

      Roles roles = projects.getUserRoles(projectId, userId);
      if (roles == null || roles.getRoles().size() < 1) throw new WebApplicationException(403);

      String tokenId = UUID.randomUUID().toString();
      long expMillis = expirationUnit.toMillis(expiration);
      Calendar expires = Calendar.getInstance();
      expires.setTime(new Date(System.currentTimeMillis() + expMillis));
      Access.Token token = new Access.Token(tokenId, expires, project);
      Access.User userInfo = new Access.User(user.getId(), user.getName(), user.getUsername(), roles.getRoles());
      Access access = new Access(token, null, userInfo, null);
      cache.put("/tokens/" + tokenId, access, expiration, expirationUnit);
      return access;
   }

   @POST
   @Path("url")
   @Consumes("application/json")
   @Produces("application/json")
   public UrlToken createTiny(Authentication auth) throws Exception
   {
      String projectId = auth.getProjectId();
      Project project = null;
      if (projectId == null)
      {
         if (auth.getProjectName() == null) throw new WebApplicationException(401);
         List<Project> list = projects.getProjects(auth.getProjectName()).getList();
         if (list.size() != 1) throw new WebApplicationException(401);
         project = list.get(0);
         projectId = project.getId();
      }
      else
      {
         project = projects.getProject(projectId);
         if (project == null) throw new WebApplicationException(401);
      }
      String userId = auth.getPasswordCredentials().getUser_id();
      if (userId == null)
      {
         String username = auth.getPasswordCredentials().getUsername();
         if (username == null) throw new WebApplicationException(401);
         userId = projects.getUserIdByName(projectId, username);
      }
      if (userId == null) throw new WebApplicationException(401);
      StoredUser user = users.getStoredUser(userId);
      if (user == null) throw new WebApplicationException(401);
      String password = auth.getPasswordCredentials().getPassword();
      MessageDigest digest = MessageDigest.getInstance("MD5");
      String hashPassword = Base64.encodeBytes(digest.digest(password.getBytes(StandardCharsets.UTF_8)));
      String savedPassword = user.getCredentials().get("password-hash");
      if (!hashPassword.equals(savedPassword)) throw new WebApplicationException(401);

      Roles roles = projects.getUserRoles(projectId, userId);
      if (roles == null || roles.getRoles().size() < 1) throw new WebApplicationException(403);

      String tokenId = UUID.randomUUID().toString();
      long expMillis = expirationUnit.toMillis(expiration);
      Calendar expires = Calendar.getInstance();
      expires.setTime(new Date(System.currentTimeMillis() + expMillis));

      UrlToken tiny = new UrlToken();
      //tiny.setId(tokenId);
      tiny.setUserId(user.getId());
      tiny.setExpires(expires);
      tiny.setProjectId(projectId);
      for (Role role : roles)
      {
         tiny.getRoles().add(role.getName());
      }
      return tiny;
   }

   @GET
   @Produces("application/json")
   @Path("{token}")
   @RolesAllowed({"token-verifier", "admin"})
   public Access get(@PathParam("token") String tokenId) throws NotFoundException
   {
      Access access = (Access)cache.get("/tokens/" + tokenId);
      if (access == null) throw new NotFoundException();
      if (access.getToken().getExpires().getTimeInMillis() < System.currentTimeMillis())
      {
         cache.remove("/tokens/" + tokenId);
         throw new NotFoundException();
      }
      return access;
   }

}
