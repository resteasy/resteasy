package org.jboss.resteasy.util.snapshot;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class SnapshotSet<T> implements Set<T> {
    protected volatile Set<T> delegate;
    protected volatile boolean lockSnapshots;
    protected volatile boolean snapFirst;

    public SnapshotSet(final boolean lockSnapshots) {
        this.delegate = Collections.emptySet();
        this.lockSnapshots = lockSnapshots;
    }

   @SuppressWarnings({"rawtypes", "unchecked"})
   public SnapshotSet(final Set<T> set, final boolean shallow, final boolean lockSnapshots, final boolean snapFirst) {
        if (delegate instanceof SnapshotSet) {
            this.delegate = ((SnapshotSet)set).delegate;
        } else if (shallow) {
            this.delegate = set;
        } else {
            this.delegate = copy(set);
        }
        this.snapFirst = snapFirst && shallow;
        this.lockSnapshots = lockSnapshots;
    }

    protected HashSet<T> copy(Set<T> set) {
        snapFirst = false;
        return new HashSet<>(set);
    }

    private boolean delegateUpdate() {
        return !lockSnapshots && this.delegate != Collections.EMPTY_SET && !snapFirst;
    }


    public synchronized void lockSnapshots() {
        lockSnapshots = true;
    }

    protected Set<T> copy() {
        return copy(delegate);
    }

    @Override
    public synchronized boolean add(T t) {
        if (delegateUpdate()) return this.delegate.add(t);
        final Set<T> delegate = copy();
        if (!delegate.add(t)) return false;
        this.delegate = delegate;
        return true;
    }

    @Override
    public synchronized boolean remove(Object o) {
        if (delegateUpdate()) return this.delegate.remove(o);
        final Set<T> delegate = copy();
        if (!delegate.remove(o)) return false;
        this.delegate = delegate;
        return true;
    }

    @Override
    public synchronized boolean addAll(Collection<? extends T> collection) {
        if (delegateUpdate()) return this.delegate.addAll(collection);
        final Set<T> delegate = copy();
        if (!delegate.addAll(collection)) return false;
        this.delegate = delegate;
        return true;
    }

    @Override
    public synchronized boolean retainAll(Collection<?> collection) {
        if (delegateUpdate()) return this.delegate.retainAll(collection);
        final Set<T> delegate = copy();
        if (!delegate.retainAll(collection)) return false;
        this.delegate = delegate;
        return true;
    }

    @Override
    public synchronized boolean removeAll(Collection<?> collection) {
        if (delegateUpdate()) return this.delegate.removeAll(collection);
        final Set<T> delegate = copy();
        if (!delegate.removeAll(collection)) return false;
        this.delegate = delegate;
        return true;
    }

    @Override
    public synchronized void clear() {
        if (delegateUpdate()) this.delegate.clear();
        if (!this.delegate.isEmpty()) this.delegate = new HashSet<>();
    }

    @Override
    public synchronized boolean removeIf(Predicate<? super T> filter) {
        if (delegateUpdate()) return this.delegate.removeIf(filter);
        final Set<T> delegate = copy();
        if (!delegate.removeIf(filter)) return false;
        this.delegate = delegate;
        return true;
    }


// read methods

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return delegate.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return delegate.iterator();
    }

    @Override
    public Object[] toArray() {
        return delegate.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] t1s) {
        return delegate.toArray(t1s);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return delegate.containsAll(collection);
    }

    @Override
    public boolean equals(Object o) {
        return delegate.equals(o);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public Spliterator<T> spliterator() {
        return delegate.spliterator();
    }

    @Override
    public Stream<T> stream() {
        return delegate.stream();
    }

    @Override
    public Stream<T> parallelStream() {
        return delegate.parallelStream();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        delegate.forEach(action);
    }
}
