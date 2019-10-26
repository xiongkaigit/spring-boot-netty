package com.netty.demo.springbootnetty.config;

import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "netty")
@Data
public class NettyConfig {

    private int port;

    private int idleTime;
    /**
     * 最大线程数
     */
    private int maxThreads;
    /**
     * 最大数据包长度
     */
    private int maxFrameLength;

    /**
     * 存储每一个客户端接入进来时的channel对象
     */
    public static ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

}
