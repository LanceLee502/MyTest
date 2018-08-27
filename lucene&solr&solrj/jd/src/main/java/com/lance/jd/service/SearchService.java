package com.lance.jd.service;

import com.lance.jd.pojo.Product;
import com.lance.jd.pojo.QueryVo;
import com.lance.jd.pojo.Result;

/**
 * @author Lance_Lee
 */
public interface SearchService {
    /**
     * 查找产品列表
     * @param queryVo
     * @return Result
     */
    Result<Product> findProductList(QueryVo queryVo);
}
