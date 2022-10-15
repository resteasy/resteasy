package org.jboss.resteasy.grpc.protobuf;

import org.jboss.resteasy.core.providerfactory.ResteasyProviderFactoryImpl;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.snapshot.SnapshotSet;

import jakarta.ws.rs.RuntimeType;

public class ResteasyGrpcProviderFactoryImpl extends ResteasyProviderFactoryImpl {

   public ResteasyGrpcProviderFactoryImpl(final ResteasyProviderFactory parent) {
      super(RuntimeType.SERVER, parent);
       providerClasses = new SnapshotSet<>(false);
       providerInstances = new SnapshotSet<>(false);
      registerBuiltins = false;
   }
}
