package org.jboss.resteasy.arquillian.extension;

import java.lang.reflect.Field;
import java.util.List;

import org.jboss.arquillian.container.spi.client.deployment.Deployment;
import org.jboss.arquillian.container.spi.client.deployment.DeploymentScenario;

public class DeploymentScenarioUtils {

    /**
     * Should be moved to DeploymentScenario
     */
    public static void removeDeploymentFromDeploymentScenario(DeploymentScenario deploymentScenario, String deploymentName) throws Exception {
        @SuppressWarnings("unchecked")
        List<Deployment> deployments = (List<Deployment>) getFieldValue(deploymentScenario, "deployments");
        Deployment deploymentFound = null;
        for (Deployment deployment : deployments) {
            if (deployment.getDescription().getName().equals(deploymentName)) {
                deploymentFound = deployment;
                break;
            }
        }
        if (deploymentFound != null)
            deployments.remove(deploymentFound);
    }

    public static Field findFieldOfBean(Object bean, String fieldName) {
        Field field = null;
        Class <?> clazz = bean.getClass();
        while (clazz != null && field == null) {
            field = findField(clazz.getDeclaredFields(), fieldName);
            clazz = clazz.getSuperclass();
        }
        if (field == null)
            throw new IllegalStateException("Field " + fieldName + " not found");
        return field;
    }

    public static Object getFieldValue(Object bean, String fieldName) throws IllegalAccessException {
        Field field = findFieldOfBean(bean, fieldName);
        field.setAccessible(true);
        return field.get(bean);
    }

    public static Field findField(Field[] fields, String name) {
        for (Field field : fields)
            if (field.getName().equals(name))
                return field;
        return null;
    }


}
