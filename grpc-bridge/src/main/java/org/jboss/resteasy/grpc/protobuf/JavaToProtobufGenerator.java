package org.jboss.resteasy.grpc.protobuf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.logging.Logger;
import org.jboss.resteasy.grpc.runtime.servlet.HttpServletRequestImpl;

import com.github.javaparser.ParseResult;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.VoidType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.declarations.ResolvedClassDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedFieldDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedTypeDeclaration;
import com.github.javaparser.resolution.types.ResolvedArrayType;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserClassDeclaration;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.model.typesystem.ReferenceTypeImpl;
import com.github.javaparser.symbolsolver.reflectionmodel.ReflectionClassDeclaration;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.github.javaparser.utils.Log;
import com.github.javaparser.utils.SourceRoot;

/**
 * Traverses a set of JAX-RS resources and creates a protobuf representation.
 * <p/>
 * <ol>
 * <li>Find all JAX-RS resource methods and resource locators and create an rpc entry for each</li>
 * <li>Find the transitive closure of the classes mentioned in the resource methods and locators
 * and create a message entry for each.</li>
 * </ol>
 * <p/>
 * </pre>
 * For example,
 * <p/>
 *
 * <pre>
 * public class CC1 {
 *
 *     &#064;Path("m1")
 *     &#064;GET
 *     String m1(CC2 cc2) {
 *         return "x";
 *     }
 *
 *     String m2(String s) {
 *         return "x";
 *     }
 *
 *     &#064;Path("m3")
 *     &#064;GET
 *     String m3(CC4 cc4) {
 *         return "x";
 *     }
 * }
 * </pre>
 *
 * together with the class definitions
 * <p/>
 *
 * <pre>
 * package io.grpc.classes;
 *
 * public class CC2 extends CC3 {
 *    int j;
 *
 *    public CC2(String s, int j) {
 *       super(s);
 *       this.j = j;
 *    }
 *
 *    public CC2() {}
 * }
 *
 * ========================
 * package io.grpc.classes;
 *
 * public class CC3 {
 *    String s;
 *
 *    public CC3(String s) {
 *       this.s = s;
 *    }
 *
 *    public CC3() {}
 * }
 *
 * ========================
 * package io.grpc.classes;
 *
 * public class CC4 {
 *    private String s;
 *    private CC5 cc5;
 *
 *    public CC4(String s, CC5 cc5) {
 *       this.s = s;
 *       this.cc5 = cc5;
 *    }
 *
 *    public CC4() {}
 * }
 *
 * ========================
 * package io.grpc.classes;
 *
 * public class CC5 {
 *    int k;
 *
 *    public CC5(int k) {
 *       this.k = k;
 *    }
 *
 *    public CC5() {}
 * }
 * </pre>
 *
 * is translated to CC1.proto:
 * <p/>
 *
 * <pre>
 * syntax = "proto3";
 * package io.grpc.classes;
 * option java_package = "io.grpc.classes";
 * option java_outer_classname = "CC1_proto";
 *
 * service CC1Service {
 *    rpc m1 (io_grpc_classes___CC2) returns (String);
 *    rpc m3 (io_grpc_classes___CC4) returns (String);
 * }
 *
 * message io_grpc_classes___CC2 {
 *    int32 j = 1;
 *    io_grpc_classes___CC3 cC3___super = 2;
 * }
 *
 * message io_grpc_classes___CC4 {
 *    string s = 3;
 *    io_grpc_classes___CC5 cc5 = 4;
 * }
 *
 * message io_grpc_classes___CC3 {
 *    string s = 5;
 * }
 *
 * message io_grpc_classes___CC5 {
 *    int32 k = 6;
 * }
 * </pre>
 * <p/>
 * <b>Notes.</b>
 * <ol>
 * <li>{@code CC1.m2()} is not a resource method, so it does not appear in CC1.proto.
 * <li>Protobuf syntax does not support inheritance, so {@code JavaToProtobufGenerator}
 * treats a superclass as a special field. For example, {@code CC2} is a subclass of {@code CC3},
 * so each instance of {@code CC2} has a field named {@code cC3___super} of {@code type io_grpc_classes___CC3}.
 */
public class JavaToProtobufGenerator {

    private static final Logger logger = Logger.getLogger(JavabufTranslatorGenerator.class);
    private static final String LS = System.lineSeparator();

    private static Map<String, String> TYPE_MAP = new HashMap<String, String>();
    private static Map<String, String> PRIMITIVE_WRAPPER_TYPES = new HashMap<String, String>();
    private static Map<String, String> PRIMITIVE_WRAPPER_DEFINITIONS = new HashMap<String, String>();
    private static Set<String> ANNOTATIONS = new HashSet<String>();
    private static Set<String> HTTP_VERBS = new HashSet<String>();
    private static String prefix;
    private static boolean needEmpty = false;
    private static List<ResolvedReferenceTypeDeclaration> resolvedTypes = new CopyOnWriteArrayList<ResolvedReferenceTypeDeclaration>();
    private static Set<String> entityMessageTypes = new HashSet<String>();
    private static Set<String> returnMessageTypes = new HashSet<String>();
    private static Set<String> jars;
    private static Set<String> additionalClasses;// = new CopyOnWriteArraySet<String>();
    private static Set<String> visited = new HashSet<String>();
    private static JavaSymbolSolver symbolSolver;
    private static ClassVisitor classVisitor = new ClassVisitor();
    private static JakartaRESTResourceVisitor jakartaRESTResourceVisitor = new JakartaRESTResourceVisitor();
    private static boolean started = false;
    private static int counter = 1;
    private static boolean isSSE;
    private static String SSE_EVENT_CLASSNAME = "org_jboss_resteasy_grpc_sse_runtime___SseEvent";

    static {
        TYPE_MAP.put("boolean", "bool");
        TYPE_MAP.put("byte", "int32");
        TYPE_MAP.put("short", "int32");
        TYPE_MAP.put("int", "int32");
        TYPE_MAP.put("long", "int64");
        TYPE_MAP.put("float", "float");
        TYPE_MAP.put("double", "double");
        TYPE_MAP.put("boolean", "bool");
        TYPE_MAP.put("char", "int32");
        TYPE_MAP.put("String", "string");
        TYPE_MAP.put("java.lang.String", "string");

        PRIMITIVE_WRAPPER_TYPES.put("boolean", "gBoolean");
        PRIMITIVE_WRAPPER_TYPES.put("byte", "gByte");
        PRIMITIVE_WRAPPER_TYPES.put("short", "gShort");
        PRIMITIVE_WRAPPER_TYPES.put("int", "gInteger");
        PRIMITIVE_WRAPPER_TYPES.put("long", "gLong");
        PRIMITIVE_WRAPPER_TYPES.put("float", "gFloat");
        PRIMITIVE_WRAPPER_TYPES.put("double", "gDouble");
        PRIMITIVE_WRAPPER_TYPES.put("boolean", "gBoolean");
        PRIMITIVE_WRAPPER_TYPES.put("char", "gCharacter");
        PRIMITIVE_WRAPPER_TYPES.put("string", "gString");
        PRIMITIVE_WRAPPER_TYPES.put("Boolean", "gBoolean");
        PRIMITIVE_WRAPPER_TYPES.put("Byte", "gByte");
        PRIMITIVE_WRAPPER_TYPES.put("Short", "gShort");
        PRIMITIVE_WRAPPER_TYPES.put("Integer", "gInteger");
        PRIMITIVE_WRAPPER_TYPES.put("Long", "gLong");
        PRIMITIVE_WRAPPER_TYPES.put("Float", "gFloat");
        PRIMITIVE_WRAPPER_TYPES.put("Double", "gDouble");
        PRIMITIVE_WRAPPER_TYPES.put("Boolean", "gBoolean");
        PRIMITIVE_WRAPPER_TYPES.put("Character", "gCharacter");
        PRIMITIVE_WRAPPER_TYPES.put("String", "gString");
        PRIMITIVE_WRAPPER_TYPES.put("java.lang.String", "gString");
        PRIMITIVE_WRAPPER_TYPES.put("java.lang.Byte", "gByte");
        PRIMITIVE_WRAPPER_TYPES.put("java.lang.Short", "gShort");
        PRIMITIVE_WRAPPER_TYPES.put("java.lang.Integer", "gInteger");
        PRIMITIVE_WRAPPER_TYPES.put("java.lang.Long", "gLong");
        PRIMITIVE_WRAPPER_TYPES.put("java.lang.Float", "gFloat");
        PRIMITIVE_WRAPPER_TYPES.put("java.lang.Double", "gDouble");
        PRIMITIVE_WRAPPER_TYPES.put("java.lang.Boolean", "gBoolean");
        PRIMITIVE_WRAPPER_TYPES.put("java.lang.Character", "gCharacter");
        PRIMITIVE_WRAPPER_TYPES.put("java.lang.String", "gString");

        PRIMITIVE_WRAPPER_DEFINITIONS.put("Boolean", "message gBoolean   {bool   value = $V$;}");
        PRIMITIVE_WRAPPER_DEFINITIONS.put("Byte", "message gByte      {int32  value = $V$;}");
        PRIMITIVE_WRAPPER_DEFINITIONS.put("Short", "message gShort     {int32  value = $V$;}");
        PRIMITIVE_WRAPPER_DEFINITIONS.put("Integer", "message gInteger   {int32  value = $V$;}");
        PRIMITIVE_WRAPPER_DEFINITIONS.put("Long", "message gLong      {int64  value = $V$;}");
        PRIMITIVE_WRAPPER_DEFINITIONS.put("Float", "message gFloat     {float  value = $V$;}");
        PRIMITIVE_WRAPPER_DEFINITIONS.put("Double", "message gDouble    {double value = $V$;}");
        PRIMITIVE_WRAPPER_DEFINITIONS.put("Character", "message gCharacter {string value = $V$;}");
        PRIMITIVE_WRAPPER_DEFINITIONS.put("String", "message gString    {string value = $V$;}");

        ANNOTATIONS.add("Context");
        ANNOTATIONS.add("CookieParam");
        ANNOTATIONS.add("HeaderParam");
        ANNOTATIONS.add("MatrixParam");
        ANNOTATIONS.add("PathParam");
        ANNOTATIONS.add("QueryParam");

        HTTP_VERBS.add("DELETE");
        HTTP_VERBS.add("HEAD");
        HTTP_VERBS.add("GET");
        HTTP_VERBS.add("OPTIONS");
        HTTP_VERBS.add("PATCH");
        HTTP_VERBS.add("POST");
        HTTP_VERBS.add("PUT");
    }

    public static void main(String[] args) throws IOException {
        if (args == null || (args.length != 4)) {
            logger.info("need four args");
            logger.info("  arg[0]: root directory");
            logger.info("  arg[1]: package to be used in .proto file");
            logger.info("  arg[2]: java package to be used in .proto file");
            logger.info("  arg[3]: java outer classname to be generated from .proto file");
            logger.info("  -Djars: comma separated of jars [optional]");
            logger.info("  -Dclasses: comma separated of addition classes [optional]");
            return;
        }
        prefix = args[3];
        String s = System.getProperty("jars", "default");
        jars = "default".equals(s) || "".equals(s)
                ? new CopyOnWriteArraySet<String>()
                : new CopyOnWriteArraySet<String>(Arrays.asList(s.split(",")));
        s = System.getProperty("classes", "default");
        additionalClasses = "default".equals(s) || "".equals(s)
                ? new CopyOnWriteArraySet<String>()
                : new CopyOnWriteArraySet<String>(Arrays.asList(s.split(",")));
        StringBuilder sb = new StringBuilder();
        protobufHeader(args, sb);
        new JavaToProtobufGenerator().processClasses(args, sb);
        while (!resolvedTypes.isEmpty()) {
            for (ResolvedReferenceTypeDeclaration rrtd : resolvedTypes) {
                classVisitor.visit(rrtd, sb);
            }
        }
        finishProto(sb);
        writeProtoFile(args, sb);
        createProtobufDirectory(args);
    }

    private static void protobufHeader(String[] args, StringBuilder sb) {
        sb.append("syntax = \"proto3\";" + LS);
        sb.append("package " + args[1].replace('-', '.') + ";" + LS);
        sb.append("import \"google/protobuf/any.proto\";" + LS);
        sb.append("import \"google/protobuf/timestamp.proto\";" + LS);
        sb.append("option java_package = \"" + args[2] + "\";" + LS);
        sb.append("option java_outer_classname = \"" + args[3] + "_proto\";" + LS);
    }

    /**
     * Visit all JAX-RS resource classes discovered in project's src/main/java
     */
    private void processClasses(String[] args, StringBuilder sb) throws IOException {
        Log.setAdapter(new Log.StandardOutStandardErrorAdapter());

        // SourceRoot is a tool that read and writes Java files from packages on a certain root directory.
        Path path = Path.of(args[0], "/src/main/java/");
        SourceRoot sourceRoot = new SourceRoot(path);
        TypeSolver reflectionTypeSolver = new ReflectionTypeSolver();
        TypeSolver javaParserTypeSolver = new JavaParserTypeSolver(path);
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(reflectionTypeSolver);
        combinedTypeSolver.add(javaParserTypeSolver);
        for (Iterator<String> it = jars.iterator(); it.hasNext();) {
            String s = it.next();
            combinedTypeSolver.add(new JarTypeSolver(s));
        }
        symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
        sourceRoot.getParserConfiguration().setSymbolResolver(symbolSolver);
        List<ParseResult<CompilationUnit>> list = sourceRoot.tryToParseParallelized();
        for (ParseResult<CompilationUnit> p : list) {
            jakartaRESTResourceVisitor.visit(p.getResult().get(), sb);
        }
        if (started) {
            sb.append("}" + LS);
        }
        processAdditionalClasses(symbolSolver, sb);
    }

    /****************************************************************************/
    /******************************
     * primary methods *****************************
     * /
     *****************************************************************************/

    private static void processAdditionalClasses(JavaSymbolSolver symbolSolver, StringBuilder sb) throws FileNotFoundException {
        StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver);
        while (!additionalClasses.isEmpty()) {
            for (String filename : additionalClasses) {
                int n = filename.indexOf(":");
                if (n < 0) {
                    throw new RuntimeException("bad syntax: " + filename);
                }
                String dir = filename.substring(0, n).trim();
                filename = dir + "/" + filename.substring(n + 1).replace(".", "/") + ".java";
                CompilationUnit cu = StaticJavaParser.parse(new File(filename));
                AdditionalClassVisitor additionalClassVisitor = new AdditionalClassVisitor(dir);
                additionalClassVisitor.visit(cu, sb);
            }
        }
        if (isSSE) {
            sb.append(LS)
                    .append("message org_jboss_resteasy_grpc_sse_runtime___SseEvent {" + LS)
                    .append("  string comment = ").append(counter++).append(";").append(LS)
                    .append("  string id = ").append(counter++).append(";").append(LS)
                    .append("  string name = ").append(counter++).append(";").append(LS)
                    .append("  google.protobuf.Any data = ").append(counter++).append(";").append(LS)
                    .append("  int64 reconnectDelay = ").append(counter++).append(";").append(LS)
                    .append("}").append(LS);
        }
    }

    private static void finishProto(StringBuilder sb) {
        if (needEmpty) {
            sb.append(LS + "message gEmpty {}");
            entityMessageTypes.add("gEmpty");
            returnMessageTypes.add("gEmpty");
        }

        for (String wrapper : PRIMITIVE_WRAPPER_DEFINITIONS.values()) {
            sb.append(LS).append(wrapper.replace("$V$", String.valueOf(counter++)));
        }
        createGeneralEntityMessageType(sb);
        createGeneralReturnMessageType(sb);
    }

    private static void createGeneralEntityMessageType(StringBuilder sb) {
        sb.append(LS + LS + "message gHeader {" + LS).append("   repeated string values = ").append(counter++)
                .append(";" + LS + "}");
        sb.append(LS + LS + "message gCookie {" + LS)
                .append("   string name = ").append(counter++).append(";" + LS)
                .append("   string value = ").append(counter++).append(";" + LS)
                .append("   int32  version = ").append(counter++).append(";" + LS)
                .append("   string path = ").append(counter++).append(";" + LS)
                .append("   string domain = ").append(counter++).append(";" + LS)
                .append("}");
        sb.append(LS + LS + "message gNewCookie {" + LS)
                .append("   string name = ").append(counter++).append(";" + LS)
                .append("   string value = ").append(counter++).append(";" + LS)
                .append("   int32  version = ").append(counter++).append(";" + LS)
                .append("   string path = ").append(counter++).append(";" + LS)
                .append("   string domain = ").append(counter++).append(";" + LS)
                .append("   string comment = ").append(counter++).append(";" + LS)
                .append("   int32 maxAge = ").append(counter++).append(";" + LS)
                .append("   google.protobuf.Timestamp expiry = ").append(counter++).append(";" + LS)
                .append("   bool secure = ").append(counter++).append(";" + LS)
                .append("   bool httpOnly = ").append(counter++).append(";" + LS + LS)
                .append("   enum SameSite {" + LS)
                .append("      NONE   = 0;" + LS)
                .append("      LAX    = 1;" + LS)
                .append("      STRICT = 2;" + LS)
                .append("   }" + LS + LS)
                .append("   SameSite sameSite = ").append(counter++).append(";" + LS)
                .append("}");
        sb.append(LS + LS + "message ServletInfo {" + LS)
                .append("   string characterEncoding = ").append(counter++).append(";" + LS)
                .append("   string clientAddress = ").append(counter++).append(";" + LS)
                .append("   string clientHost = ").append(counter++).append(";" + LS)
                .append("   int32  clientPort = ").append(counter++).append(";" + LS)
                .append("}");
        sb.append(LS + LS + "message FormValues {" + LS)
                .append("   repeated string formValues_field = ").append(counter++).append(";" + LS)
                .append("}");
        sb.append(LS + LS + "message FormMap {" + LS)
                .append("   map<string, FormValues> formMap_field = ").append(counter++).append(";" + LS)
                .append("}");
        sb.append(LS + LS + "message GeneralEntityMessage {" + LS)
                .append("   ServletInfo servletInfo = ").append(counter++).append(";" + LS)
                .append("   string URL = ").append(counter++).append(";" + LS)
                .append("   map<string, gHeader> headers = ").append(counter++).append(";" + LS)
                .append("   repeated gCookie cookies = ").append(counter++).append(";" + LS)
                .append("   string httpMethod = ").append(counter++).append(";" + LS)
                .append("   oneof messageType {" + LS);
        for (String messageType : entityMessageTypes) {
            sb.append("      ")
                    .append(messageType)
                    .append(" ")
                    .append(namify(messageType)).append("_field")
                    .append(" = ")
                    .append(counter++)
                    .append(";" + LS);
        }
        sb.append("      FormMap form_field = ").append(counter++).append(";" + LS);
        sb.append("   }" + LS + "}" + LS);
    }

    private static void createGeneralReturnMessageType(StringBuilder sb) {
        sb.append(LS + "message GeneralReturnMessage {" + LS)
                .append("   map<string, gHeader> headers = ").append(counter++).append(";" + LS)
                .append("   repeated gNewCookie cookies = ").append(counter++).append(";" + LS)
                .append("   gInteger status = ").append(counter++).append(";" + LS)
                .append("   oneof messageType {" + LS);
        for (String messageType : returnMessageTypes) {
            sb.append("      ")
                    .append(messageType)
                    .append(" ")
                    .append(namify(messageType)).append("_field")
                    .append(" = ")
                    .append(counter++)
                    .append(";" + LS);
        }
        sb.append("   }" + LS + "}" + LS);
    }

    private static void writeProtoFile(String[] args, StringBuilder sb) throws IOException {
        Path path = Files.createDirectories(Path.of(args[0], "src", "main", "proto"));
        Files.writeString(path.resolve(args[3] + ".proto"), sb.toString(), StandardCharsets.UTF_8);
    }

    private static void createProtobufDirectory(String[] args) {
        String path = args[0] + "/target/generatedSources";
        for (String s : args[1].split("\\.")) {
            path += "/" + s;
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdir();
            }
        }
    }

    /****************************************************************************/
    /********************************
     * classes ***********************************
     * /
     ****************************************************************************/

    /**
     * Visits each class in the transitive closure of all classes referenced in the
     * signatures of resource methods. Creates a service with an rpc declaration for
     * each resource method or locator.
     */
    static class JakartaRESTResourceVisitor extends VoidVisitorAdapter<StringBuilder> {

        public void visit(final ClassOrInterfaceDeclaration subClass, StringBuilder sb) {
            // Don't process gRPC server
            if (subClass.getFullyQualifiedName().orElse("").startsWith("grpc.server")) {
                return;
            }
            Optional<AnnotationExpr> opt = subClass.getAnnotationByName("Path");
            SingleMemberAnnotationExpr annotationExpr = opt.isPresent() ? (SingleMemberAnnotationExpr) opt.get() : null;
            String classPath = "";
            if (annotationExpr != null) {
                classPath = annotationExpr.getMemberValue().toString();
                classPath = classPath.substring(1, classPath.length() - 1);
            }
            for (BodyDeclaration<?> bd : subClass.getMembers()) {
                if (bd instanceof MethodDeclaration) {
                    MethodDeclaration md = (MethodDeclaration) bd;
                    if (!isResourceOrLocatorMethod(md)) {
                        continue;
                    }
                    String methodPath = "";
                    opt = md.getAnnotationByName("Path");
                    annotationExpr = opt.isPresent() ? (SingleMemberAnnotationExpr) opt.get() : null;
                    if (annotationExpr != null) {
                        methodPath = annotationExpr.getMemberValue().toString();
                        methodPath = methodPath.substring(1, methodPath.length() - 1);
                    }
                    String httpMethod = getHttpMethod(md);
                    // Add service with a method for each resource method in class.
                    if (!started) {
                        sb.append(LS + "service ")
                                .append(prefix)
                                .append("Service {" + LS);
                        started = true;
                    }
                    String entityType = getEntityParameter(md, httpMethod);
                    String returnType = getReturnType(md, httpMethod);
                    String syncType = isSuspended(md) ? "suspended"
                            : (isCompletionStage(md) ? "completionStage" : (isSSE(md) ? "sse" : "sync"));
                    isSuspended(md);
                    sb.append("// ");
                    if (!("".equals(classPath))) {
                        sb.append(classPath).append("/");
                    }
                    sb.append(methodPath).append(" ")
                            .append(entityType).append(" ")
                            .append(returnType).append(" ")
                            .append(httpMethod).append(" ")
                            .append(syncType).append("" + LS);
                    entityMessageTypes.add(entityType);
                    returnMessageTypes.add(returnType);
                    sb.append("  rpc ")
                            .append(md.getNameAsString())
                            .append(" (")
                            .append("GeneralEntityMessage")
                            .append(") returns (")
                            .append("sse".equals(syncType) ? "stream " : "")
                            .append("sse".equals(syncType) ? SSE_EVENT_CLASSNAME : "GeneralReturnMessage")
                            .append(");" + LS);

                    // Add each parameter and return type to resolvedTypes for further processing.
                    for (Parameter p : md.getParameters()) {
                        if (!isEntity(p)) {
                            continue;
                        }
                        if (p.getType().resolve().isPrimitive()) {
                            continue;
                        }
                        ReferenceTypeImpl rt = (ReferenceTypeImpl) p.getType().resolve();
                        ResolvedReferenceTypeDeclaration rrtd = rt.getTypeDeclaration().get();
                        String type = rt.asReferenceType().getQualifiedName();
                        if (!visited.contains(type)) {
                            resolvedTypes.add(rrtd);
                        }
                    }
                }
            }
        }
    }

    /**
     * Visit all classes discovered by JakartaRESTResourceVisitor in the process of visiting all JAX-RS resources
     */
    static class ClassVisitor extends VoidVisitorAdapter<StringBuilder> {

        /**
         * For each class, create a message type with a field for each variable in the class.
         */
        public void visit(ResolvedReferenceTypeDeclaration clazz, StringBuilder sb) {
            resolvedTypes.remove(clazz);
            if (clazz.isInterface()) {
                return;
            }
            if (clazz.getPackageName().startsWith("java")) {
                return;
            }
            if (PRIMITIVE_WRAPPER_DEFINITIONS.containsKey(clazz.getClassName())) {
                return;
            }
            if (Response.class.getName().equals(clazz.getQualifiedName())) {
                return;
            }
            String fqn = clazz.getQualifiedName();
            if (visited.contains(fqn)) {
                return;
            }
            visited.add(fqn);

            // Begin protobuf message definition.
            sb.append(LS + "message ").append(fqnifyClass(fqn, isInnerClass(clazz))).append(" {" + LS);

            // Scan all variables in class.
            for (ResolvedFieldDeclaration rfd : clazz.getDeclaredFields()) {
                String type = null;
                if (rfd.getType().isPrimitive() || rfd.getType().isReferenceType()
                        && String.class.getName().equals(rfd.getType().asReferenceType().getQualifiedName())) {
                    type = TYPE_MAP.get(rfd.getType().describe());
                } else if (rfd.getType() instanceof ResolvedArrayType) {
                    ResolvedArrayType rat = (ResolvedArrayType) rfd.getType();
                    ResolvedType ct = rat.getComponentType();
                    if ("byte".equals(ct.describe())) {
                        type = "bytes";
                    } else if (ct.isPrimitive()) {
                        type = "repeated " + TYPE_MAP.get(removeTypeVariables(ct.describe()));
                    } else {
                        fqn = removeTypeVariables(ct.describe());
                        if (!ct.isReferenceType()) {
                            continue;
                        }
                        if (!visited.contains(fqn)) {
                            resolvedTypes.add(ct.asReferenceType().getTypeDeclaration().get());
                        }
                        type = "repeated " + fqnifyClass(fqn, isInnerClass(ct.asReferenceType().getTypeDeclaration().get()));
                    }
                } else { // Defined type
                    if (rfd.getType().isReferenceType()) {
                        ResolvedReferenceTypeDeclaration rrtd = (ResolvedReferenceTypeDeclaration) rfd.getType()
                                .asReferenceType().getTypeDeclaration().get();
                        fqn = rrtd.getPackageName() + "." + rrtd.getClassName();
                        if (!visited.contains(fqn)) {
                            resolvedTypes.add(rrtd);
                        }
                        type = fqnifyClass(fqn, isInnerClass(rrtd));
                    } else if (rfd.getType().isTypeVariable()) {
                        type = "bytes ";
                    }
                }
                if (type != null) {
                    sb.append("  ")
                            .append(type)
                            .append(" ")
                            .append(rfd.getName())
                            .append(" = ")
                            .append(counter++)
                            .append(";" + LS);
                }
            }

            // Add field for superclass.
            for (ResolvedReferenceType rrt : clazz.getAncestors()) {
                if (rrt.getTypeDeclaration().get() instanceof ReflectionClassDeclaration) {
                    ReflectionClassDeclaration rcd = (ReflectionClassDeclaration) rrt.getTypeDeclaration().get();
                    if (Object.class.getName().equals(rcd.getQualifiedName())) {
                        continue;
                    }
                    fqn = fqnifyClass(rcd.getPackageName() + "." + rcd.getName(), isInnerClass(rrt.getTypeDeclaration().get()));
                    if (!visited.contains(fqn)) {
                        resolvedTypes.add(rcd);
                    }
                    String superClassName = rcd.getName();
                    String superClassVariableName = Character.toString(Character.toLowerCase(superClassName.charAt(0)))
                            .concat(superClassName.substring(1)) + "___super";
                    sb.append("  ")
                            .append(fqn)
                            .append(" ")
                            .append(superClassVariableName)
                            .append(" = ")
                            .append(counter++)
                            .append(";" + LS);
                    break;
                } else if (rrt.getTypeDeclaration().get() instanceof JavaParserClassDeclaration) {
                    JavaParserClassDeclaration jpcd = (JavaParserClassDeclaration) rrt.getTypeDeclaration().get();
                    ResolvedClassDeclaration rcd = jpcd.asClass();
                    if (Object.class.getName().equals(rcd.getClassName())) {
                        continue;
                    }
                    fqn = rcd.getPackageName() + "." + rcd.getName();
                    if (!visited.contains(fqn)) {
                        resolvedTypes.add(rcd);
                    }
                    fqn = fqnifyClass(fqn, isInnerClass(rrt.getTypeDeclaration().get()));
                    String superClassName = rcd.getName();
                    String superClassVariableName = Character.toString(Character.toLowerCase(superClassName.charAt(0)))
                            .concat(superClassName.substring(1)) + "___super";
                    sb.append("  ")
                            .append(fqn)
                            .append(" ")
                            .append(superClassVariableName)
                            .append(" = ")
                            .append(counter++)
                            .append(";" + LS);
                    break;

                }
            }
            sb.append("}" + LS);
        }
    }

    /**
     * Visit all classes discovered by JakartaRESTResourceVisitor in the process of visiting all JAX-RS resources
     */
    static class AdditionalClassVisitor extends VoidVisitorAdapter<StringBuilder> {
        private String dir;

        AdditionalClassVisitor(final String dir) {
            this.dir = dir;
        }

        /**
         * For each class, create a message type with a field for each variable in the class.
         */
        public void visit(ClassOrInterfaceDeclaration clazz, StringBuilder sb) {
            if (PRIMITIVE_WRAPPER_DEFINITIONS.containsKey(clazz.getName().asString())) {
                return;
            }
            String packageName = getPackageName(clazz);
            String fqn = packageName + "." + clazz.getNameAsString();
            String filename = dir + ":" + fqn;
            additionalClasses.remove(filename);

            if (visited.contains(fqn)) {
                return;
            }
            visited.add(fqn);

            // Begin protobuf message definition.
            sb.append(LS + "message ").append(fqnifyClass(fqn, isInnerClass(clazz))).append(" {" + LS);

            // Scan all variables in class.
            for (FieldDeclaration fd : clazz.getFields()) {
                ResolvedFieldDeclaration rfd = fd.resolve();
                ResolvedType type = rfd.getType();
                String typeName = type.describe();
                if (TYPE_MAP.containsKey(typeName)) {
                    typeName = TYPE_MAP.get(typeName);
                } else if (type.isArray()) {
                    ResolvedType ct = type.asArrayType().getComponentType();
                    if ("byte".equals(ct.describe())) {
                        typeName = "bytes";
                    } else if (ct.isPrimitive()) {
                        typeName = "repeated " + typeName;
                    } else {
                        fqn = type.describe();
                        additionalClasses.add(dir + ":" + fqn);
                        typeName = "repeated "
                                + fqnifyClass(fqn, isInnerClass(type.asReferenceType().getTypeDeclaration().get()));
                    }
                } else { // Defined type
                    fqn = type.describe();
                    additionalClasses.add(dir + ":" + fqn);
                    typeName = fqnifyClass(type.describe(), isInnerClass(type.asReferenceType().getTypeDeclaration().get()));
                }
                if (type != null) {
                    sb.append("  ")
                            .append(typeName)
                            .append(" ")
                            .append(rfd.getName())
                            .append(" = ")
                            .append(counter++)
                            .append(";" + LS);
                }
            }

            // Add field for superclass.
            for (ResolvedReferenceType rrt : clazz.resolve().getAllAncestors()) {
                if (Object.class.getName().equals(rrt.getQualifiedName())) {
                    continue;
                }
                if (rrt.getTypeDeclaration().get() instanceof JavaParserClassDeclaration) {
                    JavaParserClassDeclaration jpcd = (JavaParserClassDeclaration) rrt.getTypeDeclaration().get();
                    ResolvedClassDeclaration rcd = jpcd.asClass();
                    if (Object.class.getName().equals(rcd.getClassName())) {
                        continue;
                    }
                    fqn = rcd.getPackageName() + "." + rcd.getName();
                    if (!visited.contains(fqn)) { // should fqn be fqnifyed?
                        additionalClasses.add(dir + ":" + fqn); // add to additionalClasses
                    }
                    fqn = fqnifyClass(fqn, isInnerClass(rcd));
                    String superClassName = rcd.getName();
                    String superClassVariableName = Character.toString(Character.toLowerCase(superClassName.charAt(0)))
                            .concat(superClassName.substring(1)) + "___super";
                    sb.append("  ")
                            .append(fqn)
                            .append(" ")
                            .append(superClassVariableName)
                            .append(" = ")
                            .append(counter++)
                            .append(";" + LS);
                    break;

                }
            }
            sb.append("}" + LS);
        }
    }

    private static String getPackageName(ClassOrInterfaceDeclaration clazz) {
        String fqn = clazz.getFullyQualifiedName().orElse(null);
        if (fqn == null) {
            return null;
        }
        int index = fqn.lastIndexOf(".");
        return fqn.substring(0, index);
    }

    /****************************************************************************/
    /******************************
     * utility methods *****************************
     * /
     *****************************************************************************/
    private static String getEntityParameter(MethodDeclaration md, String httpMethod) {
        if (HttpServletRequestImpl.LOCATOR.equals(httpMethod)) {
            return "google.protobuf.Any";
        }
        for (Parameter p : md.getParameters()) {
            if (isEntity(p)) {
                String rawType = p.getTypeAsString();
                if (PRIMITIVE_WRAPPER_TYPES.containsKey(rawType)) {
                    return PRIMITIVE_WRAPPER_TYPES.get(rawType);
                }
                // array?
                ResolvedType rt = p.getType().resolve();
                resolvedTypes.add(rt.asReferenceType().getTypeDeclaration().get());
                String type = rt.describe();
                return fqnifyClass(type, isInnerClass(rt.asReferenceType().getTypeDeclaration().get()));
            }
        }
        needEmpty = true;
        return "gEmpty";
    }

    private static boolean isEntity(Parameter p) {
        for (AnnotationExpr ae : p.getAnnotations()) {
            if (ANNOTATIONS.contains(ae.getNameAsString())) {
                return false;
            }
        }
        String name = p.getTypeAsString();
        if (AsyncResponse.class.getName().equals(name) || AsyncResponse.class.getSimpleName().equals(name)) {
            return false;
        }
        return true;
    }

    private static String getReturnType(MethodDeclaration md, String httpMethod) {
        if (isSuspended(md) || HttpServletRequestImpl.LOCATOR.equals(httpMethod)) {
            return "google.protobuf.Any";
        }
        if (isSSE(md)) {
            return SSE_EVENT_CLASSNAME;
        }
        for (Node node : md.getChildNodes()) {
            if (node instanceof Type) {
                if (node instanceof VoidType) {
                    return "google.protobuf.Any"; // ??
                }
                String rawType = ((Type) node).asString();
                int open = rawType.indexOf("<");
                int close = rawType.indexOf(">");
                if (open >= 0 && close > open) {
                    String type = rawType.substring(0, open);
                    String parameterType = rawType.substring(open + 1, close);
                    if (CompletionStage.class.getCanonicalName().contentEquals(type)
                            || CompletionStage.class.getSimpleName().contentEquals(type)) {
                        rawType = parameterType;
                    } else {
                        rawType = type;
                    }
                }
                if (PRIMITIVE_WRAPPER_TYPES.containsKey(rawType)) {
                    return PRIMITIVE_WRAPPER_TYPES.get(rawType);
                }
                if ("jakarta.ws.rs.core.Response".equals(rawType) || "Response".equals(rawType)) {
                    return "google.protobuf.Any";
                }
                // array?
                ResolvedType rt = ((Type) node).resolve();
                resolvedTypes.add(rt.asReferenceType().getTypeDeclaration().get());
                String type = ((Type) node).resolve().describe();
                return fqnifyClass(type, isInnerClass(rt.asReferenceType().getTypeDeclaration().get()));
            }
        }
        needEmpty = true;
        return "gEmpty";
    }

    private static boolean isSuspended(MethodDeclaration md) {
        for (Parameter p : md.getParameters()) {
            for (AnnotationExpr ae : p.getAnnotations()) {
                if ("Suspended".equals(ae.getNameAsString())) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isCompletionStage(MethodDeclaration md) {
        for (Node node : md.getChildNodes()) {
            if (node instanceof Type) {
                String rawType = ((Type) node).asString();
                int open = rawType.indexOf("<");
                int close = rawType.indexOf(">");
                if (open >= 0 && close > open) {
                    String type = rawType.substring(0, open);
                    if (CompletionStage.class.getCanonicalName().contentEquals(type)
                            || CompletionStage.class.getSimpleName().contentEquals(type)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean isSSE(MethodDeclaration md) {
        Optional<AnnotationExpr> opt = md.getAnnotationByName("Produces");
        if (opt.isEmpty()) {
            return false;
        }
        AnnotationExpr ae = opt.get();
        List<StringLiteralExpr> list1 = ae.findAll(StringLiteralExpr.class);
        for (Iterator<StringLiteralExpr> it = list1.iterator(); it.hasNext();) {
            StringLiteralExpr sle = it.next();
            if (MediaType.SERVER_SENT_EVENTS.equals(sle.getValue())) {
                isSSE = true;
                return true;
            }
        }
        List<FieldAccessExpr> list2 = ae.findAll(FieldAccessExpr.class);
        for (Iterator<FieldAccessExpr> it = list2.iterator(); it.hasNext();) {
            FieldAccessExpr fae = it.next();
            List<Node> list3 = fae.getChildNodes();
            if (list3.size() >= 2 && list3.get(0) instanceof NameExpr && list3.get(1) instanceof SimpleName) {
                NameExpr ne = (NameExpr) list3.get(0);
                SimpleName sn = (SimpleName) list3.get(1);
                if ("MediaType".equals(ne.getName().asString()) && "SERVER_SENT_EVENTS".equals(sn.asString())) {
                    isSSE = true;
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isResourceOrLocatorMethod(MethodDeclaration md) {
        for (AnnotationExpr ae : md.getAnnotations()) {
            if (HTTP_VERBS.contains(ae.getNameAsString().toUpperCase()) || "Path".equals(ae.getNameAsString())) {
                return true;
            }
        }
        return false;
    }

    private static String removeTypeVariables(String classType) {
        int left = classType.indexOf('<');
        if (left < 0) {
            return classType;
        }
        return classType.substring(0, left);
    }

    private static boolean isInnerClass(ResolvedReferenceTypeDeclaration clazz) {
        try {
            Optional<?> opt = clazz.containerType();
            if (opt.isEmpty()) {
                return false;
            }
            ResolvedTypeDeclaration rtd = clazz.containerType().get();
            return rtd.isClass();
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isInnerClass(ClassOrInterfaceDeclaration clazz) {
        return clazz.isNestedType();
    }

    private static String fqnifyClass(String s, boolean isInnerClass) {
        int l = s.lastIndexOf(".");
        String sPackage = s.substring(0, l).replace(".", "_");
        String separator = isInnerClass ? "_INNER_" : "___";
        String className = s.substring(l + 1);
        return sPackage + separator + className;
    }

    private static String namify(String s) {
        return s.replace(".", "_");
    }

    private static String getHttpMethod(MethodDeclaration md) {
        if (!md.getAnnotationByName("DELETE").isEmpty()) {
            return "DELETE";
        }
        if (!md.getAnnotationByName("GET").isEmpty()) {
            return "GET";
        }
        if (!md.getAnnotationByName("HEAD").isEmpty()) {
            return "HEAD";
        }
        if (!md.getAnnotationByName("OPTIONS").isEmpty()) {
            return "OPTIONS";
        }
        if (!md.getAnnotationByName("PATCH").isEmpty()) {
            return "PATCH";
        }
        if (!md.getAnnotationByName("POST").isEmpty()) {
            return "POST";
        }
        if (!md.getAnnotationByName("PUT").isEmpty()) {
            return "PUT";
        }
        return HttpServletRequestImpl.LOCATOR;
    }
}
