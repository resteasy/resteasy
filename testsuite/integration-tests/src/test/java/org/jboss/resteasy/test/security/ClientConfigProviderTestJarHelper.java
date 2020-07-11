package org.jboss.resteasy.test.security;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 * Contains utility methods used for creating, running and getting results of jars meant to test ClientConfigProvider functionality.
 */
class ClientConfigProviderTestJarHelper {

    enum TestType {
        TEST_CREDENTIALS_ARE_USED_FOR_BASIC,
        TEST_CLIENTCONFIG_CREDENTIALS_ARE_IGNORED_IF_DIFFERENT_SET,
        TEST_SSLCONTEXT_USED,
        TEST_CLIENTCONFIG_SSLCONTEXT_IGNORED_WHEN_DIFFERENT_SET,
        TEST_BEARER_TOKEN_IS_USED,
        TEST_BEARER_TOKEN_IGNORED_IF_BASIC_SET_BY_USER
    }

    private static class ConfigProviderProperties {
        String mainClassName;
        String mainClassCompiled;
        String mainClassPath;
        String mainClassWithPackage;
        String mockedClientConfigProviderImplName;
        String mockedClientConfigProviderImplClassPath;
    }

    private static final String PACKAGE_NAME = "org.jboss.resteasy.test.security.testjar";
    private static final String PACKAGE_PATH = "org/jboss/resteasy/test/security/testjar/";
    private static final String JAR_NAME = "client-config-provider-test.jar";

    private static ConfigProviderProperties bearerJarConfigProperties = new ConfigProviderProperties();
    private static ConfigProviderProperties basicAuthJarConfigProperties = new ConfigProviderProperties();
    private static ConfigProviderProperties sslJarConfigProperties = new ConfigProviderProperties();

    static {
        basicAuthJarConfigProperties.mainClassName = "ClientConfigTestMainClass";
        basicAuthJarConfigProperties.mainClassCompiled = basicAuthJarConfigProperties.mainClassName + ".class";
        basicAuthJarConfigProperties.mainClassPath = PACKAGE_PATH + basicAuthJarConfigProperties.mainClassCompiled;
        basicAuthJarConfigProperties.mainClassWithPackage = PACKAGE_NAME + "." + basicAuthJarConfigProperties.mainClassCompiled;
        basicAuthJarConfigProperties.mockedClientConfigProviderImplName = "ClientConfigProviderImplCredentials";
        basicAuthJarConfigProperties.mockedClientConfigProviderImplClassPath = PACKAGE_PATH + basicAuthJarConfigProperties.mockedClientConfigProviderImplName + ".class";

        sslJarConfigProperties.mainClassName = "ClientConfigTestMainClass";
        sslJarConfigProperties.mainClassCompiled = sslJarConfigProperties.mainClassName + ".class";
        sslJarConfigProperties.mainClassPath = PACKAGE_PATH + sslJarConfigProperties.mainClassCompiled;
        sslJarConfigProperties.mainClassWithPackage = PACKAGE_NAME + "." + sslJarConfigProperties.mainClassCompiled;
        sslJarConfigProperties.mockedClientConfigProviderImplName = "ClientConfigProviderImplMocked";
        sslJarConfigProperties.mockedClientConfigProviderImplClassPath = PACKAGE_PATH + sslJarConfigProperties.mockedClientConfigProviderImplName + ".class";

        bearerJarConfigProperties.mainClassName = "ClientConfigBearerTokenTestMainClass";
        bearerJarConfigProperties.mainClassCompiled = bearerJarConfigProperties.mainClassName + ".class";
        bearerJarConfigProperties.mainClassPath = PACKAGE_PATH + bearerJarConfigProperties.mainClassCompiled;
        bearerJarConfigProperties.mainClassWithPackage = PACKAGE_NAME + "." + bearerJarConfigProperties.mainClassCompiled;
        bearerJarConfigProperties.mockedClientConfigProviderImplName = "ClientConfigProviderImplWithBearerMocked";
        bearerJarConfigProperties.mockedClientConfigProviderImplClassPath = PACKAGE_PATH + bearerJarConfigProperties.mockedClientConfigProviderImplName + ".class";
    }

    static String createClientConfigProviderTestJarWithBASIC() throws IOException {
        return createClientConfigProviderTestJar(basicAuthJarConfigProperties);
    }

    static String createClientConfigProviderTestJarWithSSL() throws IOException {
        return createClientConfigProviderTestJar(sslJarConfigProperties);
    }

    static String createClientConfigProviderTestJarWithBearerToken() throws IOException {
        return createClientConfigProviderTestJar(bearerJarConfigProperties);
    }

    private static String createClientConfigProviderTestJar(ConfigProviderProperties properties) throws IOException {
        Manifest manifest = new Manifest();
        Attributes mainAttributes = manifest.getMainAttributes();
        mainAttributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");
        mainAttributes.put(Attributes.Name.MAIN_CLASS, properties.mainClassWithPackage);
        File file = new File(JAR_NAME);
        try (JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(file), manifest)) {
            ClassLoader classLoader = ClientConfigProviderTestJarHelper.class.getClassLoader();
            String[] addClassesToJAR = {properties.mainClassPath, properties.mockedClientConfigProviderImplClassPath};
            for (String name : addClassesToJAR) {
                putClassToJar(jarOutputStream, name,
                        Objects.requireNonNull(classLoader.getResource(name)));
            }
            exposeService(jarOutputStream,
                    "org.jboss.resteasy.client.jaxrs.spi.ClientConfigProvider",
                    PACKAGE_NAME + "." + properties.mockedClientConfigProviderImplName);
        }
        return file.getAbsolutePath();
    }

    static Process runClientConfigProviderTestJar(TestType testType, String jarPath, String[] args) throws IOException {
        String[] finalArgs = new String[args.length + 2];
        finalArgs[0] = sslJarConfigProperties.mainClassName;
        finalArgs[1] = testType.name();
        System.arraycopy(args, 0, finalArgs, 2, args.length);

        return ClientConfigProviderTestJarHelper.runClientConfigProviderTestJar(jarPath, finalArgs);
    }

    static Process runClientConfigProviderBearerTestJar(TestType testType, String jarPath) throws IOException {
        return ClientConfigProviderTestJarHelper.runClientConfigProviderTestJar(jarPath, new String[]{bearerJarConfigProperties.mainClassName, testType.name()});
    }

    static Process runClientConfigProviderTestJar(String jarPath, String[] args) throws IOException {
        // use quotation marks for classpath on windows because folder names can have spaces
        String classPath = System.getProperty("os.name").contains("indows") ? "\"" + jarPath + ";" + System.getProperty("java.class.path") + "\"" : jarPath + ":" + System.getProperty("java.class.path");
        return Runtime.getRuntime()
                .exec("java -cp "  +  classPath + " " + ClientConfigProviderTestJarHelper.PACKAGE_NAME + "." + String.join(" ", args) );
    }

    static String getResultOfProcess(Process proc) throws IOException {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.contains("WARN")) {
                    builder.append(line);
                }
            }
        }
        return builder.toString();
    }

    private static void putClassToJar(JarOutputStream jar, String s, java.net.URL resource) throws IOException {
        byte[] buffer = new byte[1024];
        JarEntry entry = new JarEntry(s);
        jar.putNextEntry(entry);
        int len;
        try (InputStream is = new BufferedInputStream(new FileInputStream(resource.getPath()))) {
            while ((len = is.read(buffer, 0, buffer.length)) != -1) {
                jar.write(buffer, 0, len);
            }
        } finally {
            jar.closeEntry();
        }
    }

    private static void exposeService(JarOutputStream jar, String service, String implementation) throws IOException {
        JarEntry entry = new JarEntry("META-INF/services/" + service);
        jar.putNextEntry(entry);
        try {
            jar.write((implementation).getBytes(StandardCharsets.UTF_8));
        } finally {
            jar.closeEntry();
        }
    }
}
