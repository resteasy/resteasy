package org.jboss.resteasy.client.jaxrs;

import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;

public abstract class ProxyBuilder<T> {

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static <T> ProxyBuilder<T> builder(Class<T> iface, WebTarget webTarget) {
      try
      {
         Class clazz = ProxyBuilder.class.getClassLoader().loadClass("org.jboss.resteasy.client.jaxrs.internal.proxy.ProxyBuilderImpl");
         Constructor c = clazz.getConstructor(Class.class, WebTarget.class);
         return (ProxyBuilder<T>) c.newInstance(iface, webTarget);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

    public static <T> T proxy(final Class<T> iface, WebTarget base, final ProxyConfig config) {
        return builder(iface, base).build(config);
    }

    public abstract ProxyBuilder<T> classloader(ClassLoader cl);

    public abstract ProxyBuilder<T> defaultProduces(MediaType type);

    public abstract ProxyBuilder<T> defaultConsumes(MediaType type);

    public abstract ProxyBuilder<T> defaultProduces(String type);

    public abstract ProxyBuilder<T> defaultConsumes(String type);

    public abstract T build(ProxyConfig config);

    public abstract T build();

}
