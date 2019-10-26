/**
 * Author:   xiongkai
 * Date:     2019-10-26 15:10
 */
package com.netty.demo.springbootnetty.factory.impl;

import com.netty.demo.springbootnetty.factory.IProductFactory;
import com.netty.demo.springbootnetty.factory.product.ICmdHandlerProduct;
import com.netty.demo.springbootnetty.factory.product.handler.HeartBeatHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductFactory implements IProductFactory {

    @Autowired
    private HeartBeatHandler heartBeatHandler;

    @Override
    public ICmdHandlerProduct createProduct(String cmd) {
        if ("heart_beat".equals(cmd)) {
            return heartBeatHandler;
        }
        return null;
    }

}
