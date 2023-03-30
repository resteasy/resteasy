package org.jboss.resteasy.test.grpc;

import jakarta.rest.example.CC1ServiceGrpcImpl;
import jakarta.rest.example.CC1_proto;
import jakarta.rest.example.CC1_proto.GeneralReturnMessage;
import jakarta.rest.example.CC1_proto.gString;

import io.grpc.stub.StreamObserver;

public class CC1ServiceGrpcImplSub extends CC1ServiceGrpcImpl {

    @java.lang.Override
    public void copy(CC1_proto.GeneralEntityMessage param,
            StreamObserver<jakarta.rest.example.CC1_proto.GeneralReturnMessage> responseObserver) {
        try {
            gString reply = gString.newBuilder().setValue("xyz").build();
            GeneralReturnMessage.Builder grmb = GeneralReturnMessage.newBuilder();
            grmb.setGStringField(reply);
            responseObserver.onNext(grmb.build());
        } catch (Exception e) {
            responseObserver.onError(e);
        } finally {
            responseObserver.onCompleted();
        }
    }
}
