package org.jboss.resteasy.test.client.proxy.resource.GenericEntities;

import java.util.ArrayList;
import java.util.List;

public class GenericEntityExtendingBaseEntityResource implements GenericEntityExtendingBaseEntity<EntityExtendingBaseEntity> {
    public static final String FIRST_NAME = "FirstName";
    public static final String LAST_NAME = "LastName";

    public static List<EntityExtendingBaseEntity> generateEntities(int count) {
        List<EntityExtendingBaseEntity> entityExtendingBaseEntities = new ArrayList<>();
        for(int i = 0; i < count; i++) {
            entityExtendingBaseEntities.add(new EntityExtendingBaseEntity(FIRST_NAME, LAST_NAME));
        }
        return entityExtendingBaseEntities;
    }

    @Override
    public List<EntityExtendingBaseEntity> findAll() {
        return generateEntities(2);
    }

    @Override
    public EntityExtendingBaseEntity findOne() {
        return new EntityExtendingBaseEntity(FIRST_NAME, LAST_NAME);
    }

}
