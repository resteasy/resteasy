package org.jboss.resteasy.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.Permission;

import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.asset.StringAsset;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 * Taken from:
 * https://github.com/wildfly/wildfly-core/blob/master/testsuite/shared/src/main/java/org/jboss/as/test/shared/PermissionUtils.java
 */
public final class PermissionUtil {
    public static Asset createPermissionsXmlAsset(Permission... permissions) {
        return new StringAsset(new String(createPermissionsXml(permissions), StandardCharsets.UTF_8));
    }

    public static byte[] createPermissionsXml(Permission... permissions) {
        final Element permissionsElement = new Element("permissions");
        permissionsElement.setNamespaceURI("http://xmlns.jcp.org/xml/ns/javaee");
        permissionsElement.addAttribute(new Attribute("version", "7"));
        for (Permission permission : permissions) {
            final Element permissionElement = new Element("permission");

            final Element classNameElement = new Element("class-name");
            final Element nameElement = new Element("name");
            classNameElement.appendChild(permission.getClass().getName());
            nameElement.appendChild(permission.getName());
            permissionElement.appendChild(classNameElement);
            permissionElement.appendChild(nameElement);

            final String actions = permission.getActions();
            if (actions != null && ! actions.isEmpty()) {
                final Element actionsElement = new Element("actions");
                actionsElement.appendChild(actions);
                permissionElement.appendChild(actionsElement);
            }
            permissionsElement.appendChild(permissionElement);
        }
        Document document = new Document(permissionsElement);
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            final NiceSerializer serializer = new NiceSerializer(stream);
            serializer.setIndent(4);
            serializer.setLineSeparator("\n");
            serializer.write(document);
            serializer.flush();
            return stream.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Generating permissions.xml failed", e);
        }
    }

    static class NiceSerializer extends Serializer {

        NiceSerializer(OutputStream out) throws UnsupportedEncodingException {
            super(out, "UTF-8");
        }

        protected void writeXMLDeclaration() throws IOException {
            super.writeXMLDeclaration();
            super.breakLine();
        }
    }
}
