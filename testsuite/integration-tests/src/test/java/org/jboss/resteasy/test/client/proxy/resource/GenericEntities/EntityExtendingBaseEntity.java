package org.jboss.resteasy.test.client.proxy.resource.GenericEntities;

public class EntityExtendingBaseEntity extends BaseEntity {
    private String lastName;

    public EntityExtendingBaseEntity() {}

    public EntityExtendingBaseEntity(final String name, final String lastName) {
        super.setName(name);
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
