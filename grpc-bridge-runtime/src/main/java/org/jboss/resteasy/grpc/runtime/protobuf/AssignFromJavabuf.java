package org.jboss.resteasy.grpc.runtime.protobuf;

import com.google.protobuf.Message;

/**
 * A method for translating a javabuf class to its original Java form.
 */
public interface AssignFromJavabuf {

    void assign(Message message, Object object);
}
