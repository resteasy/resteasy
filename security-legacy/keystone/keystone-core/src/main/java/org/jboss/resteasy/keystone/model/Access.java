package org.jboss.resteasy.keystone.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

@JsonRootName("access")
public class Access implements Serializable {

   @JsonIgnoreProperties(ignoreUnknown = true)
   public static final class Token implements Serializable {
		
		private String id;
		
		private Calendar expires;
		
		private Project project;

      public Token()
      {
      }

      public Token(String id, Calendar expires, Project project)
      {
         this.id = id;
         this.expires = expires;
         this.project = project;
      }

      /**
		 * @return the id
		 */
		public String getId() {
			return id;
		}

		/**
		 * @return the expires
		 */
		public Calendar getExpires() {
			return expires;
		}

      public boolean expired()
      {
         return expires.getTime().getTime() < System.currentTimeMillis();
      }

		/**
		 * @return the project
		 */
		public Project getProject() {
			return project;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "Token [id=" + id + ", expires=" + expires + ", project="
					+ project + "]";
		}
		
	}
	
	public static final class Service implements Serializable {
		
		@JsonIgnoreProperties(ignoreUnknown=true)
		public static final class Endpoint {
			
			private String region;
			
			private String publicURL;
			
			private String internalURL;
			
			private String adminURL;

			/**
			 * @return the region
			 */
			public String getRegion() {
				return region;
			}

			/**
			 * @return the publicURL
			 */
			public String getPublicURL() {
				return publicURL;
			}

			/**
			 * @return the internalURL
			 */
			public String getInternalURL() {
				return internalURL;
			}

			/**
			 * @return the adminURL
			 */
			public String getAdminURL() {
				return adminURL;
			}

			/* (non-Javadoc)
			 * @see java.lang.Object#toString()
			 */
			@Override
			public String toString() {
				return "Endpoint [region=" + region + ", publicURL="
						+ publicURL + ", internalURL=" + internalURL
						+ ", adminURL=" + adminURL + "]";
			}
			
		}
		
		private String type;
		
		private String name;
		
		private List<Endpoint> endpoints;
		
		@JsonProperty("endpoints_links")
		private List<Link> endpointsLinks;

		/**
		 * @return the type
		 */
		public String getType() {
			return type;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @return the endpoints
		 */
		public List<Endpoint> getEndpoints() {
			return endpoints;
		}

		/**
		 * @return the endpointsLinks
		 */
		public List<Link> getEndpointsLinks() {
			return endpointsLinks;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "Service [type=" + type + ", name=" + name + ", endpoints="
					+ endpoints + ", endpointsLinks=" + endpointsLinks + "]";
		}
		
	}
	
	public static final class User implements Serializable {

      public User()
      {
      }

      public User(String id, String name, String username, Set<Role> roles)
      {
         this.id = id;
         this.name = name;
         this.username = username;
         this.roles = roles;
      }

      private String id;
		
		private String name;
		
		private String username;
		
		private Set<Role> roles;
		
		/**
		 * @return the id
		 */
		public String getId() {
			return id;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @return the username
		 */
		public String getUsername() {
			return username;
		}

		/**
		 * @return the roles
		 */
		public Set<Role> getRoles() {
			return roles;
		}

		/**
		 * @return the rolesLinks
		 */
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "User [id=" + id + ", name=" + name + ", username="
					+ username + ", roles=" + roles + "]";
		}
		
	}
	
	private Token token;
	
	private List<Service> serviceCatalog;
	
	private User user;
	
	private Map<String, Object> metadata;

   public Access()
   {
   }

   public Access(Token token, List<Service> serviceCatalog, User user, Map<String, Object> metadata)
   {
      this.token = token;
      this.serviceCatalog = serviceCatalog;
      this.user = user;
      this.metadata = metadata;
   }

   /**
	 * @return the token
	 */
	public Token getToken() {
		return token;
	}

	/**
	 * @return the serviceCatalog
	 */
	public List<Service> getServiceCatalog() {
		return serviceCatalog;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @return the metadata
	 */
	public Map<String, Object> getMetadata() {
		return metadata;
	}

   public void setToken(Token token)
   {
      this.token = token;
   }

   public void setServiceCatalog(List<Service> serviceCatalog)
   {
      this.serviceCatalog = serviceCatalog;
   }

   public void setUser(User user)
   {
      this.user = user;
   }

   public void setMetadata(Map<String, Object> metadata)
   {
      this.metadata = metadata;
   }

   /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
	@Override
	public String toString() {
		return "Access [token=" + token + ", serviceCatalog=" + serviceCatalog
				+ ", user=" + user + ", metadata=" + metadata + "]";
	}
	
}
