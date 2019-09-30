package com.wzy.distribute.lock.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wzy.distribute.lock.service.ProductStoreService;

@RestController
@RequestMapping("/product")
public class ProductController {

	@Resource
	private ProductStoreService productStoreService;
	
	@RequestMapping("/addProduct/{productId}/{num}")
	public String addProduct(@PathVariable("productId")String productId , @PathVariable("num")Integer num) {
		boolean flag = productStoreService.addProduct(productId, num);
		if(flag) {
			return "添加商品成功";
		}else {
			return "添加商品失败";
		}
	}
	
	
	@RequestMapping("/queryProduct/{productId}")
	public Integer queryProduct(@PathVariable("productId")String productId) {
		return productStoreService.queryProduct(productId);
	}
	
	@RequestMapping("/decrementProduct/{productId}")
	public Integer decrementProduct(@PathVariable("productId")String productId) {
		return productStoreService.decrementProduct(productId);
	}
	
	@RequestMapping("/decrementProductStore/{productId}/{productQuantity}")
	public String decrementProductStore(@PathVariable("productId") String productId, @PathVariable("productQuantity") Integer productQuantity) {
		Boolean flag = productStoreService.decrementProductStore(productId, productQuantity);
		if(flag) {
			return "库存扣减成功";
		}else {
			return "库存扣减失败";
		}
	}
	
	
}
