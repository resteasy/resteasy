/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2024 Red Hat, Inc., and individual contributors
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

package org.jboss.resteasy.test.providers.jackson2.objectmapper.resources;

import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;

import jakarta.ws.rs.NotFoundException;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
abstract class GenericRepository<T extends IdEntry> {
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final AtomicLong idGenerator = new AtomicLong();
    private final Set<T> repository;

    protected GenericRepository() {
        repository = new TreeSet<>();
    }

    Set<T> get() {
        lock.readLock().lock();
        try {
            return Set.copyOf(repository);
        } finally {
            lock.readLock().unlock();
        }
    }

    void add(final T entry) {
        lock.writeLock().lock();
        try {
            beforeAdd(idGenerator.incrementAndGet(), entry);
            repository.add(entry);
        } finally {
            lock.writeLock().unlock();
        }
    }

    void update(final T entry) {
        lock.writeLock().lock();
        try {
            if (!repository.contains(entry)) {
                throw new NotFoundException("Not found: " + entry);
            }
            repository.remove(entry);
            beforeUpdate(entry);
            repository.add(entry);
        } finally {
            lock.writeLock().unlock();
        }
    }

    T findById(final long id) {
        lock.readLock().lock();
        try {
            return repository.stream().filter(findByIdFilter(id)).findFirst().orElse(null);
        } finally {
            lock.readLock().unlock();
        }
    }

    protected abstract Predicate<T> findByIdFilter(long id);

    protected abstract void beforeAdd(long id, T entry);

    protected abstract void beforeUpdate(T entry);
}
