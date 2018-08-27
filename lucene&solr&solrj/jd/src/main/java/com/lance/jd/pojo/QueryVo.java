package com.lance.jd.pojo;

/**
 * @author Lance_Lee
 */
public class QueryVo {

    private String queryString;
    private String catalog_name;
    private String price;
    private String page;
    private String sort;
    private Integer pageSize = 5;

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getCatalog_name() {
        return catalog_name;
    }

    public void setCatalog_name(String catalog_name) {
        this.catalog_name = catalog_name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    @Override
    public String toString() {
        return "QueryVo{" +
                "catalog_name='" + catalog_name + '\'' +
                ", price='" + price + '\'' +
                ", page='" + page + '\'' +
                ", sort='" + sort + '\'' +
                '}';
    }
}
