package com.lance.jd.service.impl;

import com.lance.jd.pojo.Product;
import com.lance.jd.pojo.QueryVo;
import com.lance.jd.pojo.Result;
import com.lance.jd.service.SearchService;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Lance_Lee
 */
@Service
public class SearchServiceImpl implements SearchService {
    public Result<Product> findProductList(QueryVo queryVo) {

        try {
            //1.建立HttpSolrServer对象，连接solr服务
            HttpSolrServer server = new HttpSolrServer("http://localhost:8081/solr");

            //2.建立查询的对象（SolrQuery）
            SolrQuery query = new SolrQuery();
            if (StringUtils.isNotBlank(queryVo.getQueryString())) {
                //设置查询表达式
                query.setQuery(queryVo.getQueryString());
                //设置高亮hl
                // 先要开启高亮
                query.setHighlight(true);
                //设置高亮显示域
                query.addHighlightField("product_name");
                //设置高亮显示的HTML标签的开始和结束部分
                query.setHighlightSimplePre("<font color='red'>");
                query.setHighlightSimplePost("</font>");
            } else {
                query.setQuery("*:*");
            }
            //设置过滤条件
            //设置商品类别精确查询
            if (StringUtils.isNotBlank(queryVo.getCatalog_name())) {
                query.setFilterQueries("product_catalog_name:" + queryVo.getCatalog_name());
            }
            if (StringUtils.isNotBlank(queryVo.getPrice())) {
                String[] priceString = queryVo.getPrice().split("-");
                query.setFilterQueries("product_price:[" + priceString[0] + " TO " + priceString[1] + "]");
            }
            //设置排序sort
            if ("1".equals(queryVo.getSort())) {
                query.setSort("product_price", SolrQuery.ORDER.asc);
            } else if ("2".equals(queryVo.getSort())) {
                query.setSort("product_price", SolrQuery.ORDER.desc);
            }
            //设置分页,start在这里表示页数（从0开始的），当前页加1
            if (StringUtils.isNotBlank(queryVo.getPage())) {
                query.setStart(Integer.parseInt(queryVo.getPage()) + 1);
            } else {
                query.setStart(0);
            }
            query.setRows(queryVo.getPageSize());
            //设置显示列表
            query.setFields("id", "product_name", "product_price", "product_catalog", "product_catalog_name", "product_picture");
            //设置默认搜索域df
            query.set("df", "product_keywords");
            //设置响应格式wt
            query.set("wt", "json");

            //设置分片统计facet
            //开启分片统计
            query.setFacet(true);
            //添加分片统计的域的名称
            query.addFacetField("product_catalog_name");

            //3.使用HTTPSolrServer对象执行搜索，返回QueryResponse
            QueryResponse response = server.query(query);

            //4.从response获取搜索结果集（SolrDocumentList）
            //4.1 取出搜索结果集
            SolrDocumentList results = response.getResults();
            //4.2 获取高亮数据
            Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();


            //封装Result对象
            Result<Product> productResult = new Result<Product>();
            List<Product> productList = new ArrayList<Product>();

            //5.2打印文档内容
            for (SolrDocument doc : results) {
                String pid = (String) doc.get("id");
                //商品名称,从高亮的结果集中获取，如果高亮的结果集中没有则从搜索结果中获取。
                String pname = "";
                if (highlighting != null && highlighting.size() > 0) {
                    List<String> product_name = highlighting.get(pid).get("product_name");
                    if (product_name != null && product_name.size() > 0) {
                        pname = product_name.get(0).toString();
                    } else {
                        pname = doc.get("product_name").toString();
                    }
                } else {
                    pname = doc.get("product_name").toString();
                }

                //封装产品对象
                Product product = new Product();
                product.setPid(Long.parseLong(pid));
                product.setName(pname);
                product.setCatalog((Integer) doc.get("product_catalog"));
                product.setCatalog_name((String) doc.get("product_catalog_name"));
                product.setPrice((Double) doc.get("product_price"));
                product.setPicture((String) doc.get("product_picture"));

                System.out.println(product);

                //封装list
                productList.add(product);
            }
            productResult.setProductList(productList);
            if (StringUtils.isNotBlank(queryVo.getPage())) {
                productResult.setCurPage(Integer.parseInt(queryVo.getPage()));
            } else {
                productResult.setCurPage(1);
            }
            //计算总记录数
            int numFound = (int) results.getNumFound();
            //计算总页数
            int pageCount = numFound % queryVo.getPageSize() == 0 ? numFound / queryVo.getPageSize() : (numFound / queryVo.getPageSize()) + 1;
            productResult.setPageCount(pageCount);
            productResult.setRecordCount(numFound);

            return productResult;
        } catch (SolrServerException e) {
            e.printStackTrace();
            return null;
        }
    }
}
