/**
 * Author:   xiongkai
 * Date:     2019/5/7 14:18
 */
package com.netty.demo.springbootnetty.dispatcher;

import io.netty.channel.ChannelHandlerContext;

public interface IRequestDispatcher {

    void messageDispatch(ChannelHandlerContext ctx, String message) throws Exception;

}
