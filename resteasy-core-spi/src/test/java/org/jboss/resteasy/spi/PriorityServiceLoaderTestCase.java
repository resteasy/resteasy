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

package org.jboss.resteasy.spi;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import org.jboss.resteasy.spi.resources.AbstractResolver;
import org.jboss.resteasy.spi.resources.NoEntriesInterface;
import org.jboss.resteasy.spi.resources.TestImpl;
import org.jboss.resteasy.spi.resources.TestImplFirst;
import org.jboss.resteasy.spi.resources.TestImplLast;
import org.jboss.resteasy.spi.resources.TestImplNoPriority;
import org.jboss.resteasy.spi.resources.TestInterface;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class PriorityServiceLoaderTestCase {

    @Test
    public void firstAndLast() {
        final PriorityServiceLoader<TestInterface> loader1 = PriorityServiceLoader.load(TestInterface.class);
        check(true, TestImplFirst.class, loader1);
        check(false, TestImplNoPriority.class, loader1);

        final PriorityServiceLoader<TestInterface.InnerInterface> loader2 = PriorityServiceLoader
                .load(TestInterface.InnerInterface.class);
        check(true, TestInterface.InnerImpl.class, loader2);
        check(false, TestInterface.InnerImpl.class, loader2);

        final PriorityServiceLoader<NoEntriesInterface> loader3 = PriorityServiceLoader.load(NoEntriesInterface.class);
        Assertions.assertTrue(loader3.first().isEmpty(), () -> "Expected no entries");
        Assertions.assertTrue(loader3.last().isEmpty(), () -> "Expected no entries");
    }

    @Test
    public void iteratorOrder() {
        final Iterator<TestInterface> iterator = PriorityServiceLoader.load(TestInterface.class).iterator();
        // We should have 4 total
        checkNext(TestImplFirst.class, 1, iterator);
        checkNext(TestImpl.class, 2, iterator);
        checkNext(TestImplLast.class, 3, iterator);
        checkNext(TestImplNoPriority.class, 4, iterator);
        checkNoMore(iterator);
    }

    @Test
    public void innerInterface() {
        final Iterator<TestInterface.InnerInterface> iterator = PriorityServiceLoader.load(TestInterface.InnerInterface.class)
                .iterator();
        checkNext(TestInterface.InnerImpl.class, 1, iterator);
        checkNoMore(iterator);
    }

    @Test
    public void noImplementation() {
        checkNoMore(PriorityServiceLoader.load(NoEntriesInterface.class).iterator());
    }

    @Test
    public void types() throws Exception {
        final PriorityServiceLoader<TestInterface> loader = PriorityServiceLoader.load(TestInterface.class);
        final Set<Class<TestInterface>> found = loader.getTypes();
        // Should have 4 implementations
        Assertions.assertEquals(4, found.size(), () -> "Expected 4 implementations found " + found.size());
        final Iterator<Class<TestInterface>> iterator = found.iterator();
        // These should be in a specific order
        Assertions.assertEquals(TestImplFirst.class, iterator.next());
        Assertions.assertEquals(TestImpl.class, iterator.next());
        Assertions.assertEquals(TestImplLast.class, iterator.next());
        Assertions.assertEquals(TestImplNoPriority.class, iterator.next());
    }

    @Test
    public void constructorParameter() {
        final Optional<AbstractResolver> resolver = PriorityServiceLoader.load(AbstractResolver.class, (service) -> {
            try {
                return service.getConstructor(String.class).newInstance("test-value");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        })
                .first();
        Assertions.assertTrue(resolver.isPresent());
        Assertions.assertEquals("test-value", resolver.get().resolve());
    }

    private void checkNext(final Class<?> expected, final int count,
            final Iterator<?> iterator) {
        Assertions.assertTrue(iterator.hasNext(),
                () -> String.format("Entry %d is expected to be %s, but no more entries were found.", count,
                        expected.getName()));
        Object instance = iterator.next();
        Assertions.assertTrue(expected.isInstance(instance),
                () -> String.format("Expected entry %d to be %s but was %s", count, expected.getName(), instance.getClass()
                        .getName()));
    }

    private void checkNoMore(final Iterator<?> iterator) {
        Assertions.assertFalse(iterator.hasNext(), () -> "Expected no more entries");

        try {
            iterator.next();
            Assertions.fail("Expected a NoSuchElementException to be thrown");
        } catch (NoSuchElementException expected) {
        }
    }

    private void check(final boolean first, final Class<?> expected, final PriorityServiceLoader<?> loader) {
        final Optional<?> optional = first ? loader.first() : loader.last();
        Assertions.assertTrue(optional.isPresent(),
                () -> String.format("Expected %s to be present in %s", expected.getName(), loader));
        Assertions.assertTrue(expected.isInstance(optional.get()),
                () -> String.format("Expected instance %s to be an instance of %s", optional.get(), expected.getName()));
    }
}
