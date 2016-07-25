package org.jboss.resteasy.test.providers.custom.resource;

public class CustomProviderPreferenceUser {

    private String username;
    private String email;

    public CustomProviderPreferenceUser() {
    }

    public CustomProviderPreferenceUser(final String username, final String email) {
        this.username = username;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}