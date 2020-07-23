package org.jboss.resteasy.microprofile.client.publisher;

import org.reactivestreams.Publisher;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.RxInvoker;
import javax.ws.rs.core.GenericType;

public interface PublisherRxInvoker extends RxInvoker<Publisher<?>> {
    @Override
    default Publisher<?> get() {
        return null;    // NO-OP
    }

    @Override
    default <R> Publisher<?> get(Class<R> responseType) {
        return null;    // NO-OP
    }

    @Override
    default <R> Publisher<?> get(GenericType<R> responseType) {
        return null;    // NO-OP
    }

    @Override
    default Publisher<?> put(Entity<?> entity) {
        return null;    // NO-OP
    }

    @Override
    default <R> Publisher<?> put(Entity<?> entity, Class<R> responseType) {
        return null;    // NO-OP
    }

    @Override
    default <R> Publisher<?> put(Entity<?> entity, GenericType<R> responseType) {
        return null;    // NO-OP
    }

    @Override
    default Publisher<?> post(Entity<?> entity) {
        return null;    // NO-OP
    }

    @Override
    default <R> Publisher<?> post(Entity<?> entity, Class<R> responseType) {
        return null;    // NO-OP
    }

    @Override
    default <R> Publisher<?> post(Entity<?> entity, GenericType<R> responseType) {
        return null;    // NO-OP
    }

    @Override
    default Publisher<?> delete() {
        return null;    // NO-OP
    }

    @Override
    default <R> Publisher<?> delete(Class<R> responseType) {
        return null;    // NO-OP
    }

    @Override
    default <R> Publisher<?> delete(GenericType<R> responseType) {
        return null;    // NO-OP
    }

    @Override
    default Publisher<?> head() {
        return null;    // NO-OP
    }

    @Override
    default Publisher<?> options() {
        return null;    // NO-OP
    }

    @Override
    default <R> Publisher<?> options(Class<R> responseType) {
        return null;    // NO-OP
    }

    @Override
    default <R> Publisher<?> options(GenericType<R> responseType) {
        return null;    // NO-OP
    }

    @Override
    default Publisher<?> trace() {
        return null;    // NO-OP
    }

    @Override
    default <R> Publisher<?> trace(Class<R> responseType) {
        return null;    // NO-OP
    }

    @Override
    default <R> Publisher<?> trace(GenericType<R> responseType) {
        return null;    // NO-OP
    }
}
