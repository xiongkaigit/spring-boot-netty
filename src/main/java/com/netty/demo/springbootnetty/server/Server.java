/**
 * Author:   xiongkai
 * Date:     2019-10-26 13:49
 */
package com.netty.demo.springbootnetty.server;

import com.netty.demo.springbootnetty.config.NettyConfig;
import com.netty.demo.springbootnetty.handler.ServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class Server {

    ServerBootstrap serverBootstrap = new ServerBootstrap();
    /**
     * 连接线程处理
     */
    EventLoopGroup bossGroup = new NioEventLoopGroup();
    /**
     * 事件线程处理
     */
    EventLoopGroup workGroup = new NioEventLoopGroup();

    /**
     * 通道适配器
     */
    @Autowired
    private ServerHandler serverHandler;

    /**
     * netty服务器配置类
     */
    @Autowired
    private NettyConfig nettyConfig;

    /**
     * 关闭服务器方法
     */
    @PreDestroy
    public void close() {
        log.info("关闭服务....");
        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
    }

    public void start() {
        log.info("netty tcp服务开始启动...");
        int port = nettyConfig.getPort();   //tcp监听端口
        log.info("监听端口：{}", port);
        serverBootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)  //nio通道
                .option(ChannelOption.SO_BACKLOG, nettyConfig.getMaxThreads()) //连接数
                .option(ChannelOption.TCP_NODELAY, true)    //无延迟
                .childOption(ChannelOption.SO_KEEPALIVE, true) //长连接支持
                //.childOption(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(512, 1024, 2048))
                .handler(new LoggingHandler(LogLevel.INFO));
        try {
            //设置事件处理
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) {
                    ChannelPipeline pipeline = ch.pipeline();
                    // 添加心跳支持
                    pipeline.addLast(new IdleStateHandler(nettyConfig.getIdleTime(), 0, 0, TimeUnit.MINUTES));
                    /**
                     * 编码解码
                     */
                    //pipeline.addLast(new CustomizeDecoder());
                    pipeline.addLast("decoder", new StringDecoder(CharsetUtil.UTF_8));
                    pipeline.addLast("encoder", new StringEncoder(CharsetUtil.UTF_8));
                    //pipeline.addLast("decoder", new ByteArrayDecoder());
                    //pipeline.addLast("encoder", new ByteArrayEncoder());
                    // 序列化
                    //pipeline.addLast(new ObjectCodec());
                    pipeline.addLast(serverHandler);
                }
            });
            ChannelFuture f = serverBootstrap.bind(port);
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("netty tcp服务启动失败", e);
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        } finally {
            log.info("关闭服务....");
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

}
