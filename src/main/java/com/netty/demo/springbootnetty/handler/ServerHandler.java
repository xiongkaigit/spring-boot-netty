/**
 * Author:   xiongkai
 * Date:     2019-10-26 13:56
 */
package com.netty.demo.springbootnetty.handler;

import com.netty.demo.springbootnetty.config.NettyConfig;
import com.netty.demo.springbootnetty.dispatcher.IRequestDispatcher;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ServerHandler implements ChannelInboundHandler {

    @Autowired
    private IRequestDispatcher dispatcher;

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        log.info("channelRegistered");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        log.info("channelUnregistered");
    }


    /**
     * 客户端与服务端连接
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("客户端与服务端连接开始...");
        NettyConfig.group.add(ctx.channel());
    }

    /**
     * 客户端与服务端断开连接
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("客户端与服务端连接关闭...");
        NettyConfig.group.remove(ctx.channel());
    }

    /**
     * 业务处理
     * @param ctx
     * @param msg
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            if (msg instanceof String) {
                log.info("channelRead: {}", msg);
                dispatcher.messageDispatch(ctx, msg.toString());
            }
        } catch (Exception e) {
            log.error("channelRead exception", e);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

    }

    /**
     * 触发器(通道空闲逻辑处理方法)
     * @param ctx
     * @param evt
     * @throws Exception exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.READER_IDLE) {
                log.info("[释放不活跃通道] {}", ctx.channel().id());
                ctx.close();
            }
        }
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {

    }

    /**
     * 异常处理
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("{} -> [连接异常] {}通道异常，异常原因：{}", this.getClass().getName(),
                ctx.channel().id(), cause);
        ctx.close();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {

    }

}
