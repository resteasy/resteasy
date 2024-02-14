package org.jboss.resteasy.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.junit.jupiter.api.Assertions;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 *         Taken from:
 *         https://github.com/wildfly/wildfly-core/blob/master/testsuite/shared/src/main/java/org/jboss/as/test/shared/PermissionUtils.java
 */
public final class PermissionUtil {
    public static Asset createPermissionsXmlAsset(Permission... permissions) {
        return new StringAsset(new String(createPermissionsXml(permissions), StandardCharsets.UTF_8));
    }

    public static Asset createPermissionsXmlAsset(final Iterable<? extends Permission> permissions,
            final Permission... additionalPermissions) {
        return new StringAsset(new String(createPermissionsXml(permissions, additionalPermissions), StandardCharsets.UTF_8));
    }

    public static Asset createPermissionsXmlAsset(final Iterable<? extends Permission> permissions) {
        return new StringAsset(new String(createPermissionsXml(permissions), StandardCharsets.UTF_8));
    }

    public static byte[] createPermissionsXml(Permission... permissions) {
        return createPermissionsXml(Arrays.asList(permissions));
    }

    public static byte[] createPermissionsXml(final Iterable<? extends Permission> permissions,
            final Permission... additionalPermissions) {
        final Element permissionsElement = new Element("permissions");
        permissionsElement.setNamespaceURI("http://xmlns.jcp.org/xml/ns/javaee");
        permissionsElement.addAttribute(new Attribute("version", "7"));
        addPermissionXml(permissionsElement, permissions);
        if (additionalPermissions != null && additionalPermissions.length > 0) {
            addPermissionXml(permissionsElement, Arrays.asList(additionalPermissions));
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

    /**
     * This should only be used as a workaround for issues with API's outside RESTEasy where something like a
     * {@link java.util.ServiceLoader} needs access to an implementation.
     * <p>
     * Adds file permissions for every JAR in the modules directory. The {@code module.jar.path} system property
     * <strong>must</strong> be set.
     * </p>
     *
     * @param moduleNames the module names to add file permissions for
     *
     * @return a collection of permissions required
     */
    public static Collection<Permission> addModuleFilePermission(final String... moduleNames) {
        final String value = System.getProperty("module.jar.path");
        if (value == null || value.isBlank()) {
            return Collections.emptySet();
        }
        // Get the module path
        final Path moduleDir = Path.of(value);
        final Collection<Permission> result = new ArrayList<>();
        for (String moduleName : moduleNames) {
            final Path definedModuleDir = moduleDir.resolve(moduleName.replace('.', File.separatorChar)).resolve("main");
            // Find all the JAR's
            try (Stream<Path> stream = Files.walk(definedModuleDir)) {
                stream
                        .filter((path) -> path.getFileName().toString().endsWith(".jar"))
                        .map((path) -> new FilePermission(path.toAbsolutePath().toString(), "read"))
                        .forEach(result::add);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        return result;
    }

    /**
     * Creates the permissions required for the {@code java.io.tmpdir}. This adds permissions to read the directory, then
     * adds permissions for all files and subdirectories of the temporary directory. The actions are used for the latter
     * permission.
     *
     * @param actions the actions required for the temporary directory
     *
     * @return the permissions required
     */
    public static Collection<FilePermission> createTempDirPermission(final String actions) {
        String tempDir = System.getProperty("java.io.tmpdir");
        // This should never happen, but it's a better error message than an NPE
        Assertions.assertNotNull(tempDir, "The java.io.tmpdir could not be resolved");
        if (tempDir.charAt(tempDir.length() - 1) != File.separatorChar) {
            tempDir += File.separatorChar;
        }
        return List.of(new FilePermission(tempDir, "read"), new FilePermission(tempDir + "-", actions));
    }

    private static void addPermissionXml(final Element permissionsElement, final Iterable<? extends Permission> permissions) {
        for (Permission permission : permissions) {
            final Element permissionElement = new Element("permission");

            final Element classNameElement = new Element("class-name");
            final Element nameElement = new Element("name");
            classNameElement.appendChild(permission.getClass().getName());
            nameElement.appendChild(permission.getName());
            permissionElement.appendChild(classNameElement);
            permissionElement.appendChild(nameElement);

            final String actions = permission.getActions();
            if (actions != null && !actions.isEmpty()) {
                final Element actionsElement = new Element("actions");
                actionsElement.appendChild(actions);
                permissionElement.appendChild(actionsElement);
            }
            permissionsElement.appendChild(permissionElement);
        }
    }

    /**
     * A builder for creating a permission file.
     */
    public static class Builder {
        private final Collection<Permission> permissions;

        /**
         * Creates a new builder
         */
        public Builder() {
            this.permissions = new ArrayList<>();
        }

        /**
         * Creates a new builder.
         *
         * @return the new builder
         */
        public static Builder create() {
            return new Builder();
        }

        /**
         * Adds the permission to the generated {@linkplain Asset asset}.
         *
         * @param permission the permission to add
         *
         * @return this builder
         */
        public Builder add(final Permission permission) {
            this.permissions.add(permission);
            return this;
        }

        /**
         * Adds the permissions to the generated {@linkplain Asset asset}.
         *
         * @param permissions the permissions to add
         *
         * @return this builder
         */
        public Builder add(final Permission... permissions) {
            Collections.addAll(this.permissions, permissions);
            return this;
        }

        /**
         * Adds the permissions to the generated {@linkplain Asset asset}.
         *
         * @param permissions the permissions to add
         *
         * @return this builder
         */
        public Builder add(final Collection<? extends Permission> permissions) {
            this.permissions.addAll(permissions);
            return this;
        }

        /**
         * Creates the {@linkplain Asset asset} XML for the permissions.
         *
         * @return the {@code permissions.xml} asset
         */
        public Asset build() {
            return createPermissionsXmlAsset(permissions);
        }
    }

    static class NiceSerializer extends Serializer {

        NiceSerializer(final OutputStream out) throws UnsupportedEncodingException {
            super(out, "UTF-8");
        }

        protected void writeXMLDeclaration() throws IOException {
            super.writeXMLDeclaration();
            super.breakLine();
        }
    }
}
