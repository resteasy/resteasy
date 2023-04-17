package org.jboss.resteasy.test.providers.sse.resource;

public class SseSmokeUser {
    private String username;
    private String email;

    public SseSmokeUser() {
    }

    public SseSmokeUser(final String username, final String email) {
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
