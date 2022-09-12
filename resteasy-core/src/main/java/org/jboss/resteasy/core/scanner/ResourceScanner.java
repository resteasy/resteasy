/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2022 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.resteasy.core.scanner;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.CompositeIndex;
import org.jboss.jandex.DotName;
import org.jboss.jandex.Index;
import org.jboss.jandex.IndexReader;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.Indexer;

/**
 * A scanner for locating resources.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 * @since 6.1
 */
@SuppressWarnings("unused")
public class ResourceScanner {
    private static final DotName JAKARTA = DotName.createComponentized(null, "jakarta");

    // Jakarta Annotations
    private static final DotName ANNOTATION = DotName.createComponentized(JAKARTA, "annotation");
    private static final DotName PRIORITY = DotName.createComponentized(ANNOTATION, "Priority");

    // Jakarta REST
    private static final DotName WS = DotName.createComponentized(JAKARTA, "ws");
    private static final DotName RS = DotName.createComponentized(WS, "rs");
    private static final DotName APPLICATION_PATH = DotName.createComponentized(RS, "ApplicationPath");
    private static final DotName PATH = DotName.createComponentized(RS, "Path");
    private static final DotName CORE = DotName.createComponentized(RS, "core");
    private static final DotName APPLICATION = DotName.createComponentized(CORE, "Application");
    private static final DotName EXT = DotName.createComponentized(RS, "ext");
    private static final DotName PROVIDER = DotName.createComponentized(EXT, "Provider");

    private final IndexView index;
    private final Map<DotName, Set<String>> scanned;

    private ResourceScanner(final IndexView index) {
        this.index = index;
        scanned = new ConcurrentHashMap<>();
    }

    /**
     * Creates a new scanner. This first searches for {@code META-INF/jandex.idx} resources on the class path. If no
     * index resources are found, the class path itself is indexed. Note that scanning the class path could have
     * significant performance impacts.
     *
     * @param cl the class loader to find the indexes on
     *
     * @return a new resource scanner
     *
     * @throws IOException if there is an error reading the index
     */
    public static ResourceScanner fromClassPath(final ClassLoader cl) throws IOException {
        return fromClassPath(cl, null);
    }

    /**
     * Creates a new scanner. This first searches for {@code META-INF/jandex.idx} resources on the class path ignoring
     * the  {@code filter}. If no index resources are found, the class path itself is indexed. Note that scanning the
     * class path could have significant performance impacts.
     * <p>
     * A filter can be used to exclude certain paths from being processed. For example if you want only {@code *.class}
     * files to be processed you could add a filter like:
     * <pre>{@code final ResourceScanner.fromClassPath(Thread.currentThread().getContextClassLoader(),
     *                  (path) -> path.getFileName().toString().endsWith(".class"));}</pre>
     * </p>
     *
     * @param cl     the class loader to find the indexes on
     * @param filter a filter to exclude paths, {@code null} can be passed to use no filter
     *
     * @return a new resource scanner
     *
     * @throws IOException if there is an error reading the index
     */
    public static ResourceScanner fromClassPath(final ClassLoader cl, final Predicate<Path> filter) throws IOException {
        // Check for a jandex.idx
        final Enumeration<URL> resources = cl.getResources("META-INF/jandex.idx");
        if (resources.hasMoreElements()) {
            final Collection<IndexView> indexes = new ArrayList<>();
            while (resources.hasMoreElements()) {
                try (InputStream in = resources.nextElement().openStream()) {
                    final IndexReader reader = new IndexReader(in);
                    indexes.add(reader.read());
                }
            }
            return new ResourceScanner(CompositeIndex.create(indexes));
        }
        final Indexer indexer = new Indexer();
        final String[] cpEntries = System.getProperty("java.class.path").split(File.pathSeparator);
        for (String entry : cpEntries) {
            final Path path = Paths.get(entry);
            if (Files.exists(path) && (filter == null || filter.test(path))) {
                if (Files.isDirectory(path)) {
                    Files.walkFileTree(path, new SimpleFileVisitor<>() {
                        @Override
                        public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs)
                                throws IOException {
                            if (file.getFileName().toString().endsWith(".class")) {
                                indexClass(indexer, file);
                            }
                            return FileVisitResult.CONTINUE;
                        }
                    });
                } else if (path.getFileName().toString().endsWith(".class")) {
                    indexClass(indexer, path);
                } else if (path.getFileName().toString().endsWith(".jar")) {
                    indexJar(indexer, path);
                }
            }
        }
        return new ResourceScanner(indexer.complete());
    }

    /**
     * Creates a new scanner based on the index.
     *
     * @param index the index to search resources on
     *
     * @return a new resource scanner
     */
    public static ResourceScanner of(final Index index) {
        return new ResourceScanner(index);
    }

    /**
     * Returns the class names for any {@linkplain jakarta.ws.rs.core.Application applications} found. This includes
     * applications annotated with {@link jakarta.ws.rs.ApplicationPath @ApplicationPath} or subclasses of
     * {@link jakarta.ws.rs.core.Application Application}.
     *
     * @return applications found in the index
     */
    public Set<String> getApplications() {
        final Set<String> applications = scanned.computeIfAbsent(APPLICATION_PATH, (annotation) -> {
            final List<ClassInfo> apps = new ArrayList<>();
            apps.addAll(resolveTypeFromAnnotation(annotation));
            apps.addAll(index.getAllKnownSubclasses(APPLICATION));
            apps.sort(PrioritySorter.INSTANCE);
            return apps.stream()
                    .map(classInfo -> classInfo.name().toString())
                    .collect(Collectors.toSet());
        });
        return Collections.unmodifiableSet(applications);
    }

    /**
     * Returns all classes annotated with {@link jakarta.ws.rs.ext.Provider @Provider}.
     *
     * @return all providers found
     */
    public Set<String> getProviders() {
        return getTypesAnnotatedWith(PROVIDER);
    }

    /**
     * Returns resources that are annotated with {@link jakarta.ws.rs.Path @Path}.
     *
     * @return all the resources found
     */
    public Set<String> getResources() {
        return getTypesAnnotatedWith(PATH);
    }

    /**
     * Returns the types with the provided annotation.
     *
     * @param annotation the annotation used to scan fort the types
     *
     * @return all the types found for the annotation
     */
    public Set<String> getTypesAnnotatedWith(final DotName annotation) {
        return Collections.unmodifiableSet(scanned.computeIfAbsent(annotation, this::resolveFromAnnotation));
    }

    private Set<String> resolveFromAnnotation(final DotName annotation) {
        final Set<String> result = new LinkedHashSet<>();
        resolveTypeFromAnnotation(annotation).forEach(classInfo -> result.add(classInfo.name().toString()));
        return result;
    }

    private Collection<ClassInfo> resolveTypeFromAnnotation(final DotName annotation) {
        final List<ClassInfo> results = new ArrayList<>();
        for (AnnotationInstance instance : index.getAnnotations(annotation)) {
            final AnnotationTarget target = instance.target();
            if (target instanceof ClassInfo) {
                results.add((ClassInfo) target);
            }
        }
        results.sort(PrioritySorter.INSTANCE);
        return results;
    }

    private static void indexJar(final Indexer indexer, final Path jar) throws IOException {
        try (FileSystem fs = getZipFs(jar)) {
            Files.walkFileTree(fs.getPath("/"), new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs)
                        throws IOException {
                    if (file.getFileName().toString().endsWith(".class")) {
                        indexClass(indexer, file);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }

    private static void indexClass(final Indexer indexer, final Path file) throws IOException {
        try (InputStream in = Files.newInputStream(file)) {
            indexer.index(in);
        }
    }

    private static FileSystem getZipFs(final Path path) throws IOException {
        final URI uri = URI.create("jar:" + path.toUri());
        try {
            return FileSystems.getFileSystem(uri);
        } catch (FileSystemNotFoundException ignore) {
        }
        return FileSystems.newFileSystem(uri, Collections.emptyMap());
    }

    private static class PrioritySorter implements Comparator<ClassInfo> {
        static final PrioritySorter INSTANCE = new PrioritySorter();

        @Override
        public int compare(final ClassInfo o1, final ClassInfo o2) {
            int p1 = Integer.MAX_VALUE;
            int p2 = Integer.MAX_VALUE;
            final AnnotationInstance pa1 = o1.classAnnotation(PRIORITY);
            final AnnotationInstance pa2 = o2.classAnnotation(PRIORITY);
            if (pa1 != null) {
                p1 = pa1.value().asInt();
            }
            if (pa2 != null) {
                p2 = pa2.value().asInt();
            }
            return Integer.compare(p1, p2);
        }
    }
}
