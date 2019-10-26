/**
 * Author:   xiongkai
 * Date:     2019/7/6 16:40
 */
package com.netty.demo.springbootnetty.factory;

import com.netty.demo.springbootnetty.factory.product.ICmdHandlerProduct;
import org.springframework.stereotype.Component;

@Component
public interface IProductFactory {

    ICmdHandlerProduct createProduct(String cmd);

}
