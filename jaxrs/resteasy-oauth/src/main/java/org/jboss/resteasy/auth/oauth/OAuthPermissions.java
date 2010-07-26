package org.jboss.resteasy.auth.oauth;

public class OAuthPermissions {

    private String permissionType;
    private String[] permissions;
    
    public OAuthPermissions(String permissionType, String[] permissions) {
        this.permissionType = permissionType;
        this.permissions = permissions;
    }
    
    public String[] getPermissions() {
        return permissions;
    }
    
    public String getPermissionType() {
        return permissionType;
    }
}
