package org.jboss.resteasy.grpc.protobuf.runtime;

import com.google.protobuf.DynamicMessage;

/**
 * A method for translating a Java class to its protobuf representation.
 */
public interface AssignToJavabuf {

   void assign(Object from, DynamicMessage.Builder builder);
}
