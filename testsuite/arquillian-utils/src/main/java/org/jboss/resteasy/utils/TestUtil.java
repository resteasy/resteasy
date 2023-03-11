package org.jboss.resteasy.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import org.jboss.logging.Logger;
import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;
import org.jboss.resteasy.api.validation.ResteasyViolationException;
import org.jboss.resteasy.api.validation.ViolationReport;
import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher;
import org.jboss.resteasy.utils.maven.MavenUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;

/**
 * Base util class for RESTEasy testing.
 */
public class TestUtil {

    protected static final Logger logger = Logger.getLogger(TestUtil.class.getName());

    private static String baseResourcePath = new StringBuilder()
            .append("src").append(File.separator)
            .append("test").append(File.separator)
            .append("resources").append(File.separator).toString();

    private static final boolean MODULAR_JVM;

    /**
     * Try to initialize logger. This is unsuccessful on EAP deployment, because EAP do not contain log4j.
     * Logger is not necessary for this class. Some methods could be used without it.
     */
    static {

        // Shouldn't happen, but we'll assume we're not a modular environment
        final String javaSpecVersion = System.getProperty("java.specification.version");
        boolean modularJvm = false;
        if (javaSpecVersion != null) {
            final Matcher matcher = Pattern.compile("^(?:1\\.)?(\\d+)$").matcher(javaSpecVersion);
            if (matcher.find()) {
                modularJvm = Integer.parseInt(matcher.group(1)) >= 9;
            }
        }
        MODULAR_JVM = modularJvm;
    }

    /**
     * Initialize deployment.
     *
     * @return Deployment.
     */
    public static WebArchive prepareArchive(String deploymentName) {
        WebArchive war = ShrinkWrap.create(WebArchive.class, deploymentName + ".war");
        war.addClass(TestApplication.class);
        return war;
    }

    public static WebArchive prepareArchiveWithApplication(String deploymentName, Class<? extends Application> clazz) {
        WebArchive war = ShrinkWrap.create(WebArchive.class, deploymentName + ".war");
        war.addClass(clazz);
        return war;
    }

    /**
     * Finish preparing war deployment and deploy it.
     *
     * Add classes in @resources to deployment. Also all sub-classes of classes in @resources are added to deployment.
     * But only classes in @resources (not sub-classes of classes in @resources) can be used as resources
     * (getClasses function of TestApplication class return only classes in @resources).
     *
     * @param resources classes used in deployment as resources
     */
    public static Archive<?> finishContainerPrepare(WebArchive war, Map<String, String> contextParams,
            final Class<?>... resources) {
        return finishContainerPrepare(war, contextParams, null, resources);
    }

    /**
     * Finish preparing war deployment and deploy it.
     *
     * Add classes in @resources to deployment. Also all sub-classes of classes in @resources are added to deployment.
     * But only classes in @resources (not sub-classes of classes in @resources) can be used as resources
     * (getClasses function of TestApplication class return only classes in @resources).
     *
     * @param singletons classes used in deployment as singletons
     * @param resources  classes used in deployment as resources
     */
    public static Archive<?> finishContainerPrepare(WebArchive war, Map<String, String> contextParams,
            List<Class<?>> singletons, final Class<?>... resources) {

        if (contextParams == null) {
            contextParams = new Hashtable<>();
        }

        Set<String> classNamesInDeployment = new HashSet<>();
        Set<String> singletonsNamesInDeployment = new HashSet<>();

        if (resources != null) {
            for (final Class<?> clazz : resources) {
                war.addClass(clazz);
                classNamesInDeployment.add(clazz.getTypeName());
            }
        }

        if (singletons != null) {
            for (Class<?> singleton : singletons) {
                war.addClass(singleton);
                singletonsNamesInDeployment.add(singleton.getTypeName());
            }
        }

        if (contextParams != null && contextParams.size() > 0 && !war.contains("WEB-INF/web.xml")) {
            war.addAsWebInfResource(createWebXml(null, contextParams), "web.xml");
        }

        // prepare class list for getClasses function of TestApplication class
        StringBuilder classes = new StringBuilder();
        boolean start = true;
        for (String clazz : classNamesInDeployment) {
            if (start) {
                start = false;
            } else {
                classes.append(",");
            }
            classes.append(clazz);
        }
        war.addAsResource(new StringAsset(classes.toString()), "classes.txt");

        // prepare singleton list for getSingletons function of TestApplication class
        StringBuilder singletonBuilder = new StringBuilder();
        start = true;
        for (String clazz : singletonsNamesInDeployment) {
            if (start) {
                start = false;
            } else {
                singletonBuilder.append(",");
            }
            singletonBuilder.append(clazz);
        }
        war.addAsResource(new StringAsset(singletonBuilder.toString()), "singletons.txt");

        if (System.getProperty("STORE_WAR") != null) {
            war.as(ZipExporter.class).exportTo(new File("target", war.getName()), true);
        }
        return war;
    }

    /**
     * Returns the host name defined by the {@code wildfly.management.host} system property, {@code node} system property
     * or {@code localhost} by default.
     *
     * @return the management host name
     */
    public static String getManagementHost() {
        return System.getProperty("wildfly.management.host", PortProviderUtil.getHost());
    }

    /**
     * Returns the management port defined by the {@code wildfly.management.port} or {@code 9990} by default.
     *
     * @return the management port
     */
    public static int getManagementPort() {
        return Integer.parseInt(System.getProperty("wildfly.management.port", "9990"));
    }

    /**
     * Returns the management port by the {@code wildfly.management.port} or {@code 9990} by default plus the offset.
     *
     * @param offset the offset for the default port
     *
     * @return the offset management port
     */
    public static int getManagementPort(final int offset) {
        return getManagementPort() + offset;
    }

    /**
     * Add package info to deployment.
     *
     * @param clazz Package info is for package of this class.
     */
    protected WebArchive addPackageInfo(WebArchive war, final Class<?> clazz) {
        return war.addPackages(false, new org.jboss.shrinkwrap.api.Filter<org.jboss.shrinkwrap.api.ArchivePath>() {
            @Override
            public boolean include(final ArchivePath path) {
                return path.get().endsWith("package-info.class");
            }
        }, clazz.getPackage());
    }

    /**
     * Convert input stream to String.
     *
     * @param in Input stream
     * @return Converted string
     */
    public static String readString(final InputStream in) throws IOException {
        char[] buffer = new char[1024];
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        int wasRead = 0;
        do {
            wasRead = reader.read(buffer, 0, 1024);
            if (wasRead > 0) {
                builder.append(buffer, 0, wasRead);
            }
        } while (wasRead > -1);

        return builder.toString();
    }

    public static String getErrorMessageForKnownIssue(String jira, String message) {
        StringBuilder s = new StringBuilder();
        s.append("https://issues.jboss.org/browse/");
        s.append(jira);
        s.append(" - ");
        s.append(message);
        return s.toString();
    }

    public static String getErrorMessageForKnownIssue(String jira) {
        return getErrorMessageForKnownIssue(jira, "known issue");
    }

    public static String getJbossHome() {
        return System.getProperty("jboss.home");
    }

    public static String getJbossHome(boolean onServer) {
        if (onServer == false) {
            return getJbossHome();
        }
        return System.getProperty("jboss.home.dir", "");
    }

    /**
     * Get the path to the containers base dir for standalone mode (configuration, logs, etc..).
     * When arquillian.xml contains more containers that could be started simultaneously the parameter containerQualifier
     * is used to determine which base dir to get.
     *
     * @param containerQualifier container qualifier or null if the arquillian.xml contains max 1 container available
     *                           to be running at time
     * @return absolute path to base dir
     */
    public static String getStandaloneDir(String containerQualifier) {
        return getStandaloneDir(false, containerQualifier);
    }

    /**
     * Get the path to the containers base dir for standalone mode (configuration, logs, etc..).
     * When arquillian.xml contains more containers that could be started simultaneously the parameter containerQualifier
     * is used to determine which base dir to get.
     *
     * @param onServer           whether the check is made from client side (the path is constructed) or from deployment (the
     *                           path
     *                           is read from actual runtime value)
     * @param containerQualifier container qualifier or null if the arquillian.xml contains max 1 container available
     *                           to be running at time; this has no effect when onServer is true
     * @return absolute path to base dir
     */
    public static String getStandaloneDir(boolean onServer, String containerQualifier) {
        if (onServer == false) {
            if (containerQualifier == null) {
                return new File(getJbossHome(), "standalone").getAbsolutePath();
            } else {
                return new File("target", containerQualifier).getAbsolutePath();
            }
        } else {
            return System.getProperty("jboss.server.base.dir", "");
        }
    }

    public static boolean isOpenJDK() {
        return System.getProperty("java.runtime.name").toLowerCase().contains("openjdk");
    }

    public static boolean isWildFly9x() {
        final String sv = System.getProperty("server.version");
        return ("9.0.2.Final".equals(sv) || "9.0.1.Final".equals(sv) || "9.0.0.Final".equals(sv));
    }

    public static boolean isOracleJDK() {
        if (isOpenJDK()) {
            return false;
        }
        String vendor = System.getProperty("java.vendor").toLowerCase();
        return vendor.contains("sun") || vendor.contains("oracle");
    }

    public static boolean isIbmJdk() {
        return System.getProperty("java.vendor").toLowerCase().contains("ibm");
    }

    /**
     * Indicates whether or not the current JVM is a modular (Java 9+) JVM.
     *
     * @return {@code true} if this is a modular JVM, otherwise {@code false}
     */
    public static boolean isModularJvm() {
        return MODULAR_JVM;
    }

    /**
     * Get resource in test scope for some class.
     * Example: class org.test.MyTest and name "my_resource.txt" returns "src/test/resource/org/test/my_resource.txt"
     */
    public static String getResourcePath(Class<?> c, String name) {
        return new StringBuilder()
                .append(baseResourcePath)
                .append(c.getPackage().getName().replace('.', File.separatorChar))
                .append(File.separator).append(name)
                .toString();
    }

    /**
     * Read server log file from standalone/log/server.log
     */
    public static List<String> readServerLogLines() {
        return readServerLogLines(false);
    }

    public static List<String> readServerLogLines(boolean onServer) {
        return readServerLogLines(onServer, null);
    }

    public static List<String> readServerLogLines(boolean onServer, String containerQualifier) {
        String standaloneDir = TestUtil.getStandaloneDir(onServer, containerQualifier);
        String logPath = String.format("%s%slog%sserver.log", standaloneDir,
                (standaloneDir.endsWith(File.separator) || standaloneDir.endsWith("/")) ? "" : File.separator,
                File.separator);
        logPath = logPath.replace('/', File.separatorChar);
        try {
            return Files.readAllLines(Paths.get(logPath)); // UTF8 is used by default
        } catch (MalformedInputException e1) {
            // some windows machines could accept only StandardCharsets.ISO_8859_1 encoding
            try {
                return Files.readAllLines(Paths.get(logPath), StandardCharsets.ISO_8859_1);
            } catch (IOException e4) {
                throw new RuntimeException("Server logs has not standard Charsets (UTF8 or ISO_8859_1)");
            }
        } catch (IOException e) {
            // server.log file is not created, it is the same as server.log is empty
        }
        return new ArrayList<>();
    }

    /**
     * Get count of lines with specific string in log
     */
    public static int getWarningCount(String findedString, boolean onServer) {
        return getWarningCount(findedString, onServer, null);
    }

    /**
     * Get count of lines with specific string in log
     */
    public static int getWarningCount(String findedString, boolean onServer, String containerQualifier) {
        return getWarningCount(findedString, onServer, containerQualifier, false);
    }

    /**
     * Get count of lines with specific string or regexp in log
     */
    public static int getWarningCount(String findedString, boolean onServer, String containerQualifier, boolean useRegexp) {
        int count = 0;
        List<String> lines = TestUtil.readServerLogLines(onServer, containerQualifier);
        for (String line : lines) {
            if ((!useRegexp && line.contains(findedString)) || (useRegexp && line.matches(findedString))) {
                count++;
            }
        }
        return count;
    }

    /**
     * Check count of violations in ResteasyViolationException.
     */
    public static void countViolations(ResteasyViolationException e,
            int totalCount, int propertyCount, int classCount, int parameterCount, int returnValueCount) {
        Assert.assertEquals("Different total count of violations expected", totalCount, e.getViolations().size());
        Assert.assertEquals("Different count of property violations expected", propertyCount, e.getPropertyViolations().size());
        Assert.assertEquals("Different count of class violations expected", classCount, e.getClassViolations().size());
        Assert.assertEquals("Different count of parameter violations expected", parameterCount,
                e.getParameterViolations().size());
        Assert.assertEquals("Different count of return value violations expected", returnValueCount,
                e.getReturnValueViolations().size());
    }

    public static void countViolations(ViolationReport e, int propertyCount, int classCount, int parameterCount,
            int returnValueCount) {
        Assert.assertEquals("Different count of property violations expected", propertyCount, e.getPropertyViolations().size());
        Assert.assertEquals("Different count of class violations expected", classCount, e.getClassViolations().size());
        Assert.assertEquals(parameterCount, e.getParameterViolations().size());
        Assert.assertEquals(returnValueCount, e.getReturnValueViolations().size());
    }

    public static ResteasyConstraintViolation getViolationByMessage(List<ResteasyConstraintViolation> list, String message) {
        for (ResteasyConstraintViolation v : list) {
            if (v.getMessage().contains(message)) {
                return v;
            }
        }
        return null;
    }

    public static ResteasyConstraintViolation getViolationByMessageAndValue(List<ResteasyConstraintViolation> list,
            String message, Object value) {
        for (ResteasyConstraintViolation v : list) {
            if (v.getMessage().contains(message) && v.getValue().equals(value)) {
                return v;
            }
        }
        return null;
    }

    public static ResteasyConstraintViolation getViolationByPath(List<ResteasyConstraintViolation> list, String path) {
        for (ResteasyConstraintViolation v : list) {
            if (v.getPath().contains(path)) {
                return v;
            }
        }
        return null;
    }

    /**
     * Get specified single dependency
     *
     * @param dependency
     * @return Dependency gav
     */
    public static File resolveDependency(String dependency) {
        MavenUtil mavenUtil;
        mavenUtil = MavenUtil.create(true);
        File mavenGav;

        try {
            mavenGav = mavenUtil.createMavenGavFile(dependency);
        } catch (Exception e) {
            throw new RuntimeException("Unable to get artifacts from maven via Aether library", e);
        }

        return mavenGav;
    }

    /**
     * Adds additional dependency needed for the deployment tests. Specified by parameter in the format
     * 'groupId:artifactId:version'
     *
     * @param archive
     * @param dependency
     */
    public static void addOtherLibrary(WebArchive archive, String dependency) {
        archive.addAsLibrary(resolveDependency(dependency));
    }

    public static boolean isWindows() {
        String osName = System.getProperty("os.name");
        if (osName == null) {
            Assert.fail("Can't get the operating system name");
        }
        return (osName.indexOf("Windows") > -1) || (osName.indexOf("windows") > -1);
    }

    /**
     * Generate a URI based on the URL passed appending the path if its value is not {@code null}.
     *
     * @param base the base URL
     * @param path the path to append
     *
     * @return the newly create URI
     *
     * @throws URISyntaxException If the given string violates RFC 2396, as augmented by the above deviations
     * @see URI
     */
    public static URI generateUri(final URL base, final String path) throws URISyntaxException {
        if (path == null || path.isEmpty()) {
            return base.toURI();
        }
        return generateUri(base.toString(), path);
    }

    /**
     * Generate a URI based on the URL passed appending the path if its value is not {@code null}.
     *
     * @param base the base URL
     * @param path the path to append
     *
     * @return the newly create URI
     *
     * @throws URISyntaxException If the given string violates RFC 2396, as augmented by the above deviations
     * @see URI
     */
    public static URI generateUri(final URI base, final String path) throws URISyntaxException {
        if (path == null || path.isEmpty()) {
            return base;
        }
        return generateUri(base.toString(), path);
    }

    /**
     * Creates a {@code beans.xml} file which uses a {@code bean-discovery-mode} of "all".
     *
     * @return a {@code beans.xml} asset
     */
    public static Asset createBeansXml() {
        return new StringAsset("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<beans xmlns=\"https://jakarta.ee/xml/ns/jakartaee\"\n" +
                "       xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "       xsi:schemaLocation=\"https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/beans_3_0.xsd\"\n"
                +
                "       version=\"3.0\" bean-discovery-mode=\"all\">\n" +
                "</beans>");
    }

    /**
     * Creates a {@code web.xml} file.
     * <p>
     * If the application is non-null a servlet entry is added. If the given annotation is annotated with
     * {@link MultipartConfig @MultiConfig} that entry is added to the {@code web.xml}. The
     * {@code <async-supported>true</async-supported>} entry is also added. The servlet name will be the name of the
     * application.
     * </p>
     *
     * @param application   the application to add a servlet or {@code null} to use annotation scanning
     * @param contextParams the optional context parameters to add
     *
     * @return a {@code web.xml} file
     */
    public static Asset createWebXml(final Class<? extends Application> application,
            final Map<String, String> contextParams) {
        final StringBuilder webXml = new StringBuilder()
                .append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
                .append("<web-app xmlns=\"https://jakarta.ee/xml/ns/jakartaee\" \n")
                .append("   xmlns:xsi=\"https://www.w3.org/2001/XMLSchema-instance\" \n")
                .append("   xsi:schemaLocation=\"https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/web-app_5_0.xsd\"\n")
                .append("   version=\"5.0\">\n");
        for (Map.Entry<String, String> entry : contextParams.entrySet()) {
            final String paramName = entry.getKey();
            final String paramValue = entry.getValue();
            logger.info("Context param " + paramName + " value " + paramValue);

            webXml.append("    <context-param>\n")
                    .append("        <param-name>").append(paramName).append("</param-name>\n")
                    .append("        <param-value>").append(paramValue).append("</param-value>\n")
                    .append("    </context-param>\n");
        }

        if (application != null) {
            final String servletName = application.getName();
            webXml.append("    <servlet>\n")
                    .append("        <servlet-name>")
                    .append(servletName)
                    .append("</servlet-name>\n")
                    .append("        <servlet-class>")
                    .append(HttpServlet30Dispatcher.class.getName())
                    .append("</servlet-class>\n")
                    .append("        <init-param>\n")
                    .append("            <param-name>")
                    .append(Application.class.getName())
                    .append("</param-name>\n")
                    .append("            <param-value>")
                    .append(application.getName())
                    .append("</param-value>\n")
                    .append("        </init-param>\n")
                    .append("        <async-supported>true</async-supported>\n");
            final MultipartConfig multipartConfig = application.getAnnotation(MultipartConfig.class);
            final MultipartConfigElement multipartConfigElement;
            if (multipartConfig != null) {
                multipartConfigElement = new MultipartConfigElement(multipartConfig);
            } else {
                multipartConfigElement = null;
            }
            if (multipartConfigElement != null) {
                webXml.append("        <multipart-config>\n")
                        .append("            <max-file-size>")
                        .append(multipartConfigElement.getMaxFileSize())
                        .append("</max-file-size>\n")
                        .append("            <max-request-size>")
                        .append(multipartConfigElement.getMaxRequestSize())
                        .append("</max-request-size>\n")
                        .append("            <file-size-threshold>")
                        .append(multipartConfigElement.getFileSizeThreshold())
                        .append("</file-size-threshold>\n")
                        .append("            <location>")
                        .append(multipartConfigElement.getLocation())
                        .append("</location>\n")
                        .append("        </multipart-config>\n");
            }
            webXml.append("    </servlet>\n");

            final ApplicationPath applicationPath = application.getAnnotation(ApplicationPath.class);
            webXml.append("    <servlet-mapping>\n")
                    .append("        <servlet-name>").append(servletName).append("</servlet-name>\n");
            if (applicationPath != null) {
                final String pattern = applicationPath.value();
                webXml.append("        <url-pattern>")
                        .append(pattern.endsWith("/") ? pattern + "*" : pattern + "/*")
                        .append("</url-pattern>\n");
            } else {
                webXml.append("        <url-pattern>").append("/*").append("</url-pattern>\n");
            }
            webXml.append("    </servlet-mapping>\n");
        }

        webXml.append("</web-app>\n");
        return new StringAsset(webXml.toString());
    }

    /**
     * Generate a URI based on the URL passed appending the path if its value is not {@code null}.
     *
     * @param base the base URL
     * @param path the path to append
     *
     * @return the newly create URI
     *
     * @throws URISyntaxException If the given string violates RFC 2396, as augmented by the above deviations
     * @see URI
     */
    private static URI generateUri(final String base, final String path) throws URISyntaxException {
        final StringBuilder builder = new StringBuilder(base);
        if (builder.charAt(builder.length() - 1) == '/') {
            if (path.charAt(0) == '/') {
                builder.append(path.substring(1));
            } else {
                builder.append(path);
            }
        } else if (path.charAt(0) == '/') {
            builder.append(path.substring(1));
        } else {
            builder.append('/').append(path);
        }
        return new URI(builder.toString());
    }
}
