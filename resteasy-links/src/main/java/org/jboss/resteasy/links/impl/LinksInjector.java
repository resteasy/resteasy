package org.jboss.resteasy.links.impl;

import java.lang.reflect.Field;

import org.jboss.resteasy.links.RESTServiceDiscovery;
import org.jboss.resteasy.links.i18n.LogMessages;
import org.jboss.resteasy.links.i18n.Messages;

public final class LinksInjector {

    public void inject(Object entity, RESTServiceDiscovery restServiceDiscovery) {
        Field injectionField = findInjectionField(entity);
        if (injectionField == null) {
            return;
        }

        RESTServiceDiscovery fieldValue = null;
        try {
            injectionField.setAccessible(true);
            fieldValue = (RESTServiceDiscovery) injectionField.get(entity);
        } catch (Exception e) {
            LogMessages.LOGGER.error(Messages.MESSAGES.failedToReuseServiceDiscovery(entity), e);
        }

        if (fieldValue == null) {
            fieldValue = restServiceDiscovery;
        } else {
            fieldValue.addAllLinks(restServiceDiscovery);
        }

        try {
            injectionField.set(entity, fieldValue);
            injectionField.setAccessible(false);
        } catch (Exception e) {
            LogMessages.LOGGER.error(Messages.MESSAGES.failedToInjectLinks(entity), e);
        }
    }

    private Field findInjectionField(Object entity) {
        Class<?> entityClass = entity.getClass();
        do {
            for (Field field : entityClass.getDeclaredFields()) {
                if (field.getType().equals(RESTServiceDiscovery.class)) {
                    return field;
                }
            }
            entityClass = entityClass.getSuperclass();
        } while (entityClass != null);

        return null;
    }

}
