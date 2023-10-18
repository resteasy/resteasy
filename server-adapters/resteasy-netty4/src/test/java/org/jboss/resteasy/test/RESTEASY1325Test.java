package org.jboss.resteasy.test;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.jboss.resteasy.core.ResteasyDeploymentImpl;
import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

public class RESTEASY1325Test {
    static String BASE_URI = generateURL("");

    static final int IDLE_TIMEOUT = 10;

    @BeforeAll
    public static void setupSuite() throws Exception {
    }

    @AfterAll
    public static void tearDownSuite() throws Exception {
    }

    @BeforeEach
    public void setupTest() throws Exception {
    }

    @AfterEach
    public void tearDownTest() throws Exception {
    }

    @Test
    public void testIdleCloseConnection() throws Exception {
        Assertions.assertTimeout(Duration.ofMillis(IDLE_TIMEOUT * 1000 + 1000), () -> {
            NettyJaxrsServer netty = new NettyJaxrsServer();
            ResteasyDeployment deployment = new ResteasyDeploymentImpl();
            netty.setDeployment(deployment);
            netty.setPort(TestPortProvider.getPort());
            netty.setRootResourcePath("");
            netty.setSecurityDomain(null);
            netty.setIdleTimeout(IDLE_TIMEOUT);
            netty.start();
            deployment.getRegistry().addSingletonResource(new Resource());
            callAndIdle();
            netty.stop();
        });
    }

    /**
     * Test case
     *
     * @throws InterruptedException
     * @throws MalformedURLException
     */
    private void callAndIdle() throws InterruptedException, MalformedURLException {

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
                                    //                               System.out.println("HTTP response from resteasy: "+msg);
                                    Assertions.assertEquals(HttpResponseStatus.OK, msg.status());
                                }
                            });
                        }
                    });

            // first request;
            URL url = new URL(BASE_URI + "/test");
            // Make the connection attempt.
            final Channel ch = b.connect(url.getHost(), url.getPort()).sync().channel();

            // Prepare the HTTP request.
            HttpRequest request = new DefaultFullHttpRequest(
                    HttpVersion.HTTP_1_1, HttpMethod.GET, url.getFile());
            request.headers().set(HttpHeaderNames.HOST, url.getHost());
            request.headers().set(HttpHeaderNames.CONNECTION, "keep-alive");
            // Send the HTTP request.
            ch.writeAndFlush(request);

            // waiting for server close connection after idle.
            ch.closeFuture().await();
        } finally {
            // Shut down executor threads to exit.
            group.shutdownGracefully();
        }
    }

    @Path("/")
    public static class Resource {
        @GET
        @Path("/test")
        @Produces(MediaType.TEXT_PLAIN)
        public String get() {
            return "hello world";
        }
    }
}