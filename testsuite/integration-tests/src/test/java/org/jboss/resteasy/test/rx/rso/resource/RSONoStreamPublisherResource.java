package org.jboss.resteasy.test.rx.rso.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.reactivestreams.Publisher;

@Path("/")
public class RSONoStreamPublisherResource
{
   @Produces(MediaType.APPLICATION_JSON)
   @Path("publisher")
   @GET
   public Publisher<String> publisher()
   {
      return ReactiveStreams.of("one", "two").buildRs();
   }

//   @Produces(MediaType.APPLICATION_JSON)
//   @Path("context/publisher")
//   @GET
//   public Publisher<String> contextPublisher(@Context UriInfo uriInfo)
//   {
//      return Flowable.<String>create(foo -> {
//         ExecutorService executor = Executors.newSingleThreadExecutor();
//         executor.submit(new Runnable()
//         {
//            public void run()
//            {
//               foo.onNext("one");
//               foo.onNext("two");
//               foo.onComplete();
//            }
//         });
//      }, BackpressureStrategy.BUFFER).map(str -> {
//         uriInfo.getAbsolutePath();
//         return str;
//      });
//   }
}
