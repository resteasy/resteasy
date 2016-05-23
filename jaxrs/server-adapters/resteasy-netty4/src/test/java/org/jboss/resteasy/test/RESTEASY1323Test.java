package org.jboss.resteasy.test;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.HttpHeaders.Values;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.junit.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Response;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.*;
import java.util.concurrent.CountDownLatch;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

public class RESTEASY1323Test
{
   static String BASE_URI = generateURL("");

   static final int REQUEST_TIMEOUT = 4000;

   @BeforeClass
   public static void setupSuite() throws Exception
   {
      NettyContainer.start().getRegistry().addSingletonResource(new AsyncJaxrsResource());
   }

   @AfterClass
   public static void tearDownSuite() throws Exception
   {
      NettyContainer.stop();
   }

   @Before
   public void setupTest() throws Exception
   {
   }

   @After
   public void tearDownTest() throws Exception
   {
   }

   @Test(timeout=REQUEST_TIMEOUT*5)
   public void testAsyncKeepConnection() throws Exception
   {
      callAsyncTwiceWithKeepAlive();
   }

   // use netty to better monitor channel connection.
   private void callAsyncTwiceWithKeepAlive() throws InterruptedException, MalformedURLException {

         final CountDownLatch responseLatch = new CountDownLatch(2);
         // Configure the client.
         EventLoopGroup group = new NioEventLoopGroup();
         try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<Channel>() {
                       @Override
                       protected void initChannel(Channel ch) throws Exception {
                          ch.pipeline().addLast(new HttpClientCodec());
                          ch.pipeline().addLast(new HttpObjectAggregator(4096));
                          ch.pipeline().addLast(new SimpleChannelInboundHandler<FullHttpResponse>() {
                             @Override
                             protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg) {
                                System.out.println("HTTP response from resteasy: "+msg);
                                responseLatch.countDown();
                             }

                          });
                       }
                    });

            // first request;
            URL url = new URL(BASE_URI+"/jaxrs");
            // Make the connection attempt.
            final Channel ch = b.connect(url.getHost(), url.getPort()).sync().channel();

            // Prepare the HTTP request.
            HttpRequest request = new DefaultFullHttpRequest(
                    HttpVersion.HTTP_1_1, HttpMethod.GET, url.getFile());
            request.headers().set(HttpHeaderNames.HOST, url.getHost());
            request.headers().set(HttpHeaderNames.CONNECTION, "keep-alive");

            // Send the HTTP request.
            ch.writeAndFlush(request).addListener(new ChannelFutureListener() {
               @Override
               public void operationComplete(ChannelFuture future) throws Exception {

                  // 2nd request
                  URL url = new URL(BASE_URI+"/jaxrs/empty");
                  HttpRequest request2 = new DefaultFullHttpRequest(
                          HttpVersion.HTTP_1_1, HttpMethod.GET, url.getFile());
                  request2.headers().set(HttpHeaderNames.HOST, url.getHost());
                  request2.headers().set(HttpHeaderNames.CONNECTION, "keep-alive");
                  ch.writeAndFlush(request2);
               }
            });

            responseLatch.await();
         } finally {
            // Shut down executor threads to exit.
            group.shutdownGracefully();
         }
   }

   @Test(timeout=REQUEST_TIMEOUT*5)
   public void testAsyncCloseConnection() throws Exception
   {
      callAsyncWithCloseConnection();
   }

   // use netty to better monitor channel connection.
   private void callAsyncWithCloseConnection() throws InterruptedException, MalformedURLException {

      final CountDownLatch responseLatch = new CountDownLatch(2);
      // Configure the client.
      EventLoopGroup group = new NioEventLoopGroup();
      try {
         Bootstrap b = new Bootstrap();
         b.group(group)
                 .channel(NioSocketChannel.class)
                 .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                       ch.pipeline().addLast(new HttpClientCodec());
                       ch.pipeline().addLast(new HttpObjectAggregator(4096));
                       ch.pipeline().addLast(new SimpleChannelInboundHandler<FullHttpResponse>() {
                          @Override
                          protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg) {
                             System.out.println("HTTP response from resteasy: "+msg);
                             responseLatch.countDown();
                          }

                       });
                    }
                 });

         // first request;
         URL url = new URL(BASE_URI+"/jaxrs");
         // Make the connection attempt.
         final Channel ch = b.connect(url.getHost(), url.getPort()).sync().channel();

         // Prepare the HTTP request.
         HttpRequest request = new DefaultFullHttpRequest(
                 HttpVersion.HTTP_1_1, HttpMethod.GET, url.getFile());
         request.headers().set(HttpHeaderNames.HOST, url.getHost());
         request.headers().set(HttpHeaderNames.CONNECTION, "close");

         // Send the HTTP request.
         ch.writeAndFlush(request);

         // waiting for server close connection after idle.
         ch.closeFuture().await();
      } finally {
         // Shut down executor threads to exit.
         group.shutdownGracefully();
      }
   }
}
