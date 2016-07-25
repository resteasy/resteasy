package org.jboss.resteasy.test.providers.jettison.resource;

public class MyService implements UserEntityWebservice {
    public UserEntity create(UserEntity entity) {
        return entity;
    }
}
