package org.jboss.resteasy.plugins.server.netty;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import jakarta.ws.rs.SeBootstrap.Configuration;

import org.jboss.resteasy.core.ResteasyDeploymentImpl;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.plugins.server.embedded.EmbeddedJaxrsServer;
import org.jboss.resteasy.plugins.server.embedded.SecurityDomain;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.util.EmbeddedServerHelper;
import org.jboss.resteasy.util.PortProvider;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.SniHandler;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.EventExecutor;

/**
 * An HTTP server that sends back the content of the received HTTP request
 * in a pretty plaintext form.
 *
 * @author <a href="http://www.jboss.org/netty/">The Netty Project</a>
 * @author Andy Taylor (andy.taylor@jboss.org)
 * @author <a href="http://gleamynode.net/">Trustin Lee</a>
 * @author Norman Maurer
 * @version $Rev: 2080 $, $Date: 2010-01-26 18:04:19 +0900 (Tue, 26 Jan 2010) $
 */
public class NettyJaxrsServer implements EmbeddedJaxrsServer<NettyJaxrsServer> {
    protected ServerBootstrap bootstrap = new ServerBootstrap();
    protected String hostname = null;
    protected int configuredPort = PortProvider.getPort();
    protected int runtimePort = -1;
    protected ResteasyDeployment deployment;
    protected String root = "";
    protected SecurityDomain domain;
    private EventLoopGroup eventLoopGroup;
    private EventLoopGroup eventExecutor;
    private int ioWorkerCount = Runtime.getRuntime().availableProcessors() * 2;
    private int executorThreadCount = 16;
    private SSLContext sslContext;
    private SniConfiguration sniConfiguration;
    private int maxRequestSize = 1024 * 1024 * 10;
    private int maxInitialLineLength = 4096;
    private int maxHeaderSize = 8192;
    private int maxChunkSize = 8192;
    private int backlog = 128;
    // default no idle timeout.
    private int idleTimeout = -1;
    private List<ChannelHandler> channelHandlers = Collections.emptyList();
    private Map<ChannelOption, Object> channelOptions = Collections.emptyMap();
    private Map<ChannelOption, Object> childChannelOptions = Collections.emptyMap();
    private List<ChannelHandler> httpChannelHandlers = Collections.emptyList();
    private EmbeddedServerHelper serverHelper = new EmbeddedServerHelper();

    @Override
    public NettyJaxrsServer deploy() {
        // no-op
        return this;
    }

    @Override
    public NettyJaxrsServer start() {
        final Configuration.Builder builder = Configuration.builder()
                .host(hostname)
                .port(configuredPort)
                .rootPath(root)
                .sslContext(sslContext);
        start(builder.build());
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void start(final Configuration configuration) {

        final String hostname = configuration.host();
        final int configuredPort = configuration.port();
        String contextPath = configuration.rootPath();
        serverHelper.checkDeployment(deployment);

        eventLoopGroup = new NioEventLoopGroup(ioWorkerCount);
        eventExecutor = new NioEventLoopGroup(executorThreadCount);

        // dynamically set the root path (the user can rewrite it by calling setRootResourcePath)
        String appPath = serverHelper.checkAppDeployment(deployment);
        if (appPath != null && (contextPath == null || "".equals(contextPath) || "/".equals(contextPath))) {
            contextPath = appPath;
        }
        if (!contextPath.startsWith("/")) {
            contextPath = "/" + contextPath;
        }

        // Configure the server.
        bootstrap.group(eventLoopGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(createChannelInitializer(configuration, contextPath))
                .option(ChannelOption.SO_BACKLOG, backlog)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        for (Map.Entry<ChannelOption, Object> entry : channelOptions.entrySet()) {
            bootstrap.option(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<ChannelOption, Object> entry : childChannelOptions.entrySet()) {
            bootstrap.childOption(entry.getKey(), entry.getValue());
        }

        final InetSocketAddress socketAddress;
        if (null == hostname || hostname.isEmpty()) {
            socketAddress = new InetSocketAddress(configuredPort);
        } else {
            socketAddress = new InetSocketAddress(hostname, configuredPort);
        }

        Channel channel = bootstrap.bind(socketAddress).syncUninterruptibly().channel();
        runtimePort = ((InetSocketAddress) channel.localAddress()).getPort();
    }

    @Override
    public void stop() {
        runtimePort = -1;
        eventLoopGroup.shutdownGracefully();
        eventExecutor.shutdownGracefully();

        if (deployment != null) {
            deployment.stop();
        }
    }

    @Override
    public ResteasyDeployment getDeployment() {
        if (deployment == null) {
            deployment = new ResteasyDeploymentImpl();
        }
        return deployment;
    }

    @Override
    public NettyJaxrsServer setDeployment(ResteasyDeployment deployment) {
        this.deployment = deployment;
        return this;
    }

    @Override
    public NettyJaxrsServer setPort(int port) {
        this.configuredPort = port;
        return this;
    }

    public int getPort() {
        return runtimePort > 0 ? runtimePort : configuredPort;
    }

    @Override
    public NettyJaxrsServer setHostname(String hostname) {
        this.hostname = hostname;
        return this;
    }

    public String getHostname() {
        return hostname;
    }

    @Override
    public NettyJaxrsServer setRootResourcePath(String rootResourcePath) {
        if (rootResourcePath == null || rootResourcePath.equals("/")) {
            root = "";
        } else if (!rootResourcePath.startsWith("/")) {
            root = "/" + rootResourcePath;
        } else {
            root = rootResourcePath;
        }
        return this;
    }

    @Override
    public NettyJaxrsServer setSecurityDomain(SecurityDomain sc) {
        this.domain = sc;
        return this;
    }

    public NettyJaxrsServer setSSLContext(SSLContext sslContext) {
        this.sslContext = sslContext;
        return this;
    }

    public NettyJaxrsServer setSniConfiguration(SniConfiguration sniConfiguration) {
        this.sniConfiguration = sniConfiguration;
        return this;
    }

    public SniConfiguration getSniConfiguration() {
        return sniConfiguration;
    }

    /**
     * Specify the worker count to use. For more information about this please see the javadocs of {@link EventLoopGroup}
     *
     * @param ioWorkerCount worker count
     */
    public NettyJaxrsServer setIoWorkerCount(int ioWorkerCount) {
        this.ioWorkerCount = ioWorkerCount;
        return this;
    }

    /**
     * Set the number of threads to use for the EventExecutor. For more information please see the javadocs of
     * {@link EventExecutor}.
     * If you want to disable the use of the {@link EventExecutor} specify a value {@literal <=} 0. This should only be done if
     * you are 100% sure that you don't have any blocking
     * code in there.
     *
     * @param executorThreadCount thread count
     */
    public NettyJaxrsServer setExecutorThreadCount(int executorThreadCount) {
        this.executorThreadCount = executorThreadCount;
        return this;
    }

    /**
     * Set the max. request size in bytes. If this size is exceed we will send a "413 Request Entity Too Large" to the client.
     *
     * @param maxRequestSize the max request size. This is 10mb by default.
     */
    public NettyJaxrsServer setMaxRequestSize(int maxRequestSize) {
        this.maxRequestSize = maxRequestSize;
        return this;
    }

    public NettyJaxrsServer setMaxInitialLineLength(int maxInitialLineLength) {
        this.maxInitialLineLength = maxInitialLineLength;
        return this;
    }

    public NettyJaxrsServer setMaxHeaderSize(int maxHeaderSize) {
        this.maxHeaderSize = maxHeaderSize;
        return this;
    }

    public NettyJaxrsServer setMaxChunkSize(int maxChunkSize) {
        this.maxChunkSize = maxChunkSize;
        return this;
    }

    public NettyJaxrsServer setBacklog(int backlog) {
        this.backlog = backlog;
        return this;
    }

    public int getIdleTimeout() {
        return idleTimeout;
    }

    /**
     * Set the idle timeout.
     * Set this value to turn on idle connection cleanup.
     * If there is no traffic within idleTimeoutSeconds, it'll close connection.
     *
     * @param idleTimeoutSeconds - How many seconds to cleanup client connection. default value -1 meaning no idle timeout.
     */
    public NettyJaxrsServer setIdleTimeout(int idleTimeoutSeconds) {
        this.idleTimeout = idleTimeoutSeconds;
        return this;
    }

    /**
     * Add additional {@link io.netty.channel.ChannelHandler}s to the {@link io.netty.bootstrap.ServerBootstrap}.
     * <p>
     * The additional channel handlers are being added <em>before</em> the HTTP handling.
     * </p>
     *
     * @param channelHandlers the additional {@link io.netty.channel.ChannelHandler}s.
     */
    public NettyJaxrsServer setChannelHandlers(final List<ChannelHandler> channelHandlers) {
        this.channelHandlers = channelHandlers == null ? Collections.<ChannelHandler> emptyList() : channelHandlers;
        return this;
    }

    /**
     * Add additional {@link io.netty.channel.ChannelHandler}s to the {@link io.netty.bootstrap.ServerBootstrap}.
     * <p>
     * The additional channel handlers are being added <em>after</em> the HTTP handling.
     * </p>
     *
     * @param httpChannelHandlers the additional {@link io.netty.channel.ChannelHandler}s.
     */
    public NettyJaxrsServer setHttpChannelHandlers(final List<ChannelHandler> httpChannelHandlers) {
        this.httpChannelHandlers = httpChannelHandlers == null ? Collections.<ChannelHandler> emptyList() : httpChannelHandlers;
        return this;
    }

    /**
     * Add Netty {@link io.netty.channel.ChannelOption}s to the {@link io.netty.bootstrap.ServerBootstrap}.
     *
     * @param channelOptions the additional {@link io.netty.channel.ChannelOption}s.
     * @see io.netty.bootstrap.ServerBootstrap#option(io.netty.channel.ChannelOption, Object)
     */
    public NettyJaxrsServer setChannelOptions(final Map<ChannelOption, Object> channelOptions) {
        this.channelOptions = channelOptions == null ? Collections.<ChannelOption, Object> emptyMap() : channelOptions;
        return this;
    }

    /**
     * Add child options to the {@link io.netty.bootstrap.ServerBootstrap}.
     *
     * @param channelOptions the additional child {@link io.netty.channel.ChannelOption}s.
     * @see io.netty.bootstrap.ServerBootstrap#childOption(io.netty.channel.ChannelOption, Object)
     */
    public NettyJaxrsServer setChildChannelOptions(final Map<ChannelOption, Object> channelOptions) {
        this.childChannelOptions = channelOptions == null ? Collections.<ChannelOption, Object> emptyMap() : channelOptions;
        return this;
    }

    protected RequestDispatcher createRequestDispatcher() {
        if (deployment == null) {
            throw new IllegalArgumentException("A ResteasyDeployment object required");
        }

        return new RequestDispatcher((SynchronousDispatcher) deployment.getDispatcher(),
                deployment.getProviderFactory(), domain);
    }

    private ChannelInitializer<SocketChannel> createChannelInitializer(final Configuration configuration,
            final String contextPath) {
        final RequestDispatcher dispatcher = createRequestDispatcher();
        final String protocol = configuration.protocol().toLowerCase(Locale.ROOT);
        final SSLContext sslContext;
        if ("https".equals(protocol)) {
            sslContext = configuration.sslContext();
        } else {
            sslContext = null;
        }
        if (sslContext == null && sniConfiguration == null) {
            return new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    setupHandlers(ch, dispatcher, protocol, contextPath);
                }
            };
        } else if (sniConfiguration == null) {
            return new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    SSLEngine engine = sslContext.createSSLEngine();
                    engine.setUseClientMode(false);
                    engine.setWantClientAuth(
                            configuration.sslClientAuthentication() == Configuration.SSLClientAuthentication.OPTIONAL);
                    engine.setNeedClientAuth(
                            configuration.sslClientAuthentication() == Configuration.SSLClientAuthentication.MANDATORY);
                    ch.pipeline().addFirst(new SslHandler(engine));
                    setupHandlers(ch, dispatcher, protocol, contextPath);
                }
            };
        } else {
            return new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addFirst(new SniHandler(sniConfiguration.buildMapping()));
                    setupHandlers(ch, dispatcher, protocol, contextPath);
                }
            };
        }
    }

    private void setupHandlers(SocketChannel ch, RequestDispatcher dispatcher, String protocol, final String contextPath) {
        ChannelPipeline channelPipeline = ch.pipeline();
        channelPipeline.addLast(channelHandlers.toArray(new ChannelHandler[channelHandlers.size()]));
        if (idleTimeout > 0) {
            channelPipeline.addLast("idleStateHandler", new IdleStateHandler(0, 0, idleTimeout));
        }
        channelPipeline.addLast(new HttpRequestDecoder(maxInitialLineLength, maxHeaderSize, maxChunkSize));
        channelPipeline.addLast(new HttpResponseEncoder());
        channelPipeline.addLast(new HttpObjectAggregator(maxRequestSize));
        channelPipeline.addLast(httpChannelHandlers.toArray(new ChannelHandler[httpChannelHandlers.size()]));
        channelPipeline.addLast(new RestEasyHttpRequestDecoder(dispatcher.getDispatcher(), contextPath, protocol));
        channelPipeline.addLast(new RestEasyHttpResponseEncoder());
        channelPipeline.addLast(eventExecutor, new RequestHandler(dispatcher));
    }

}
