package org.jboss.resteasy.test.microprofile.restclient.resource;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import org.reactivestreams.Publisher;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Path("theService")
public class SimplestPublisherService {
    @GET
    @Path("strings")
    @Produces({MediaType.SERVER_SENT_EVENTS})
    public Publisher<String> getStrings() {
        List<String> list = generatedStringList();

        return Flowable.create(
                new FlowableOnSubscribe<String>() {
                    @Override
                    public void subscribe(FlowableEmitter<String> emitter) throws Exception {
                        emitter.onNext(list.get(0));
                        emitter.onNext(list.get(1));
                        emitter.onNext(list.get(2));
                        emitter.onComplete();
                    }
                },
                BackpressureStrategy.BUFFER);
    }

    public static List<String> generatedStringList() {
        List<String> list = new ArrayList<>();
        list.add("one");
        list.add("two");
        list.add("three");
        return list;
    }
}
