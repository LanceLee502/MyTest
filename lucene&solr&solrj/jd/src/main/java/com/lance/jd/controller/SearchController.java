package com.lance.jd.controller;

import com.lance.jd.pojo.QueryVo;
import com.lance.jd.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 搜索处理器
 *
 * @author Lance_Lee
 */
@Controller
public class SearchController {

    @Autowired
    private SearchService searchService;
    /**
     * 搜索商品
     */
    @RequestMapping("list")
    public String list(Model model, QueryVo queryVo) {
        model.addAttribute("result",searchService.findProductList(queryVo));

        //搜索条件回显
        model.addAttribute("queryString", queryVo.getQueryString());
        model.addAttribute("catalog_name", queryVo.getCatalog_name());
        model.addAttribute("price", queryVo.getPrice());
        model.addAttribute("sort", queryVo.getSort());

        return "product_list";
    }
}
