package org.jboss.resteasy.auth.oauth;


/**
 * OAuth DB Provider. WIP
 * @author Stéphane Épardaud <stef@epardaud.fr>
 */
public abstract class OAuthDBProvider extends OAuthMemoryProvider {
/* WIP
	@Entity
	public static class Consumer implements OAuthConsumer {
		@Id
		private String key;
		
		@Column(nullable = false)
		private String secret;
		
		@OneToMany(mappedBy = "consumer")
		private List<Token> accessTokens;
		
		public String getKey() {
			return key;
		}
		public void setKey(String key) {
			this.key = key;
		}
		public String getSecret() {
			return secret;
		}
		public void setSecret(String secret) {
			this.secret = secret;
		}
		public List<Token> getAccessTokens() {
			return accessTokens;
		}
		public void setAccessTokens(List<Token> accessTokens) {
			this.accessTokens = accessTokens;
		}
	}
	
	@Entity
	@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "consumer", "token" }))
	static class Token implements OAuthToken{
		@ManyToOne
		@JoinColumn(nullable = false)
		private Consumer consumer;
		@Id
		private String token;
		@Column(nullable = false)
		private String secret;
		private String user;
		@ManyToMany
		private Set<String> roles;
		public Consumer getConsumer() {
			return consumer;
		}
		public void setConsumer(Consumer consumer) {
			this.consumer = consumer;
		}
		public String getToken() {
			return token;
		}
		public void setToken(String token) {
			this.token = token;
		}
		public String getSecret() {
			return secret;
		}
		public void setSecret(String secret) {
			this.secret = secret;
		}
		public Set<String> getRoles() {
			return roles;
		}
		public void setRoles(Set<String> roles) {
			this.roles = roles;
		}
		public String getUser() {
			return user;
		}
		public void setUser(String user) {
			this.user = user;
		}
		public Principal getPrincipal() {
			final String user = this.user;
			return new Principal(){
				public String getName() {
					return user;
				}
			};
		}
	}
*/	
	public OAuthDBProvider(String realm) {
		super(realm);
	}
}
