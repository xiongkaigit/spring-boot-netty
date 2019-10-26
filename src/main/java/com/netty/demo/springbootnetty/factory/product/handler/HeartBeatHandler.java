/**
 * Author:   xiongkai
 * Date:     2019-10-26 15:46
 */
package com.netty.demo.springbootnetty.factory.product.handler;

import com.netty.demo.springbootnetty.factory.product.ICmdHandlerProduct;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HeartBeatHandler implements ICmdHandlerProduct {

    @Override
    public void cmdHandler(ChannelHandlerContext ctx, String message) {
        log.info("HeartBeatHandler-cmdHandler:", message);
    }

}
