package com.wzy.distribute.lock.service;

public interface ProductStoreService {

	//扣减库存
	public boolean decrementProductStore(String productId, Integer productQuantity);
	
	//查询产品库存
	public Integer queryProduct(String productId);
	
	//添加产品
	public Boolean addProduct(String productId , Integer num);
	
	//扣减商品
	public Integer decrementProduct(String productId);
	
}
