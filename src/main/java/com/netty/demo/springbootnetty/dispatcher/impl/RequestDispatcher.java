package com.netty.demo.springbootnetty.dispatcher.impl;

import com.netty.demo.springbootnetty.dispatcher.IRequestDispatcher;
import com.netty.demo.springbootnetty.factory.IProductFactory;
import com.netty.demo.springbootnetty.factory.product.ICmdHandlerProduct;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 转发器
 */
@Component
@Slf4j
public class RequestDispatcher implements IRequestDispatcher {

    @Autowired
    private IProductFactory productFactory;

    @Override
    public void messageDispatch(ChannelHandlerContext ctx, String message) throws Exception {
        log.info("messageDispatch: {}", message);
        ICmdHandlerProduct product = null;
        product = productFactory.createProduct(message);
        if (product != null) {
            product.cmdHandler(ctx, message);
        }
    }

}














