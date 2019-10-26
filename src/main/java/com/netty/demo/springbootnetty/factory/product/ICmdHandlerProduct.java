/**
 * Author:   xiongkai
 * Date:     2019/7/6 16:42
 */
package com.netty.demo.springbootnetty.factory.product;

import io.netty.channel.ChannelHandlerContext;

public interface ICmdHandlerProduct {

    void cmdHandler(ChannelHandlerContext ctx, String message);

}
