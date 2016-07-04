package org.jboss.resteasy.keystone.model;

import com.fasterxml.jackson.annotation.JsonRootName;

import java.io.Serializable;

@JsonRootName("auth")
public class Authentication implements Serializable {
	
	public static final class Token {
		
		private String id;

		/**
		 * @return the id
		 */
		public String getId() {
			return id;
		}

		/**
		 * @param id the id to set
		 */
		public void setId(String id) {
			this.id = id;
		}
		
	}

	public static final class PasswordCredentials {
		
		private String username;
		
		private String password;

      private String user_id;

      public String getUser_id()
      {
         return user_id;
      }

      public void setUser_id(String user_id)
      {
         this.user_id = user_id;
      }

      /**
		 * @return the username
		 */
		public String getUsername() {
			return username;
		}

		/**
		 * @param username the username to set
		 */
		public void setUsername(String username) {
			this.username = username;
		}

		/**
		 * @return the password
		 */
		public String getPassword() {
			return password;
		}

		/**
		 * @param password the password to set
		 */
		public void setPassword(String password) {
			this.password = password;
		}
		
	}
	
	public static final class ApiAccessKeyCredentials {
		
		private String accessKey;
		
		private String secretKey;

		/**
		 * @return the accessKey
		 */
		public String getAccessKey() {
			return accessKey;
		}

		/**
		 * @param accessKey the accessKey to set
		 */
		public void setAccessKey(String accessKey) {
			this.accessKey = accessKey;
		}

		/**
		 * @return the secretKey
		 */
		public String getSecretKey() {
			return secretKey;
		}

		/**
		 * @param secretKey the secretKey to set
		 */
		public void setSecretKey(String secretKey) {
			this.secretKey = secretKey;
		}
		
	}
	
	private String projectId;
	
	private String projectName;
	
	private PasswordCredentials passwordCredentials;
	
	private ApiAccessKeyCredentials apiAccessKeyCredentials;
	
	private Token token;

	/**
	 * @return the projectId
	 */
	public String getProjectId() {
		return projectId;
	}

	/**
	 * @param projectId the projectId to set
	 */
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	/**
	 * @return the projectName
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * @param projectName the projectName to set
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	/**
	 * @return the passwordCredentials
	 */
	public PasswordCredentials getPasswordCredentials() {
		return passwordCredentials;
	}

	/**
	 * @param passwordCredentials the passwordCredentials to set
	 */
	public void setPasswordCredentials(PasswordCredentials passwordCredentials) {
		this.passwordCredentials = passwordCredentials;
	}

	/**
	 * @return the apiAccessKeyCredentials
	 */
	public ApiAccessKeyCredentials getApiAccessKeyCredentials() {
		return apiAccessKeyCredentials;
	}

	/**
	 * @param apiAccessKeyCredentials the apiAccessKeyCredentials to set
	 */
	public void setApiAccessKeyCredentials(
			ApiAccessKeyCredentials apiAccessKeyCredentials) {
		this.apiAccessKeyCredentials = apiAccessKeyCredentials;
	}

	/**
	 * @return the token
	 */
	public Token getToken() {
		return token;
	}

	/**
	 * @param token the token to set
	 */
	public void setToken(Token token) {
		this.token = token;
	}
	
}
