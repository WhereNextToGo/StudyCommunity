package com.cs_liudi.community.entity;

public class Page {
    //网页传输参数获得
    //当前页数
    private int current = 1;
    //一页显示的行数
    private int limit = 10;
    //后台计算获得
    //总行数，用于计算总页数
    private int rows;
    //跳转路径
    private String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if(current >= 1){
            this.current = current;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if (limit >= 1 && limit <= 100){
            this.limit = limit;
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if (rows >= 0){
            this.rows = rows;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 获取当前页的起始行数
     * @return
     */
    public int getOffset(){
        return (current - 1)*limit;
    }

    /**
     * 获取总页数
     */
    public int getTotal(){
        if (rows % limit == 0){
            return rows/limit;
        }else{
            return rows/limit+1;
        }
    }

    /**
     * 获取起始页码
     * @return
     */
    public int getFrom(){
        return Math.max(current - 2, 1);
    }

    /**
     * 获取结束页码
     * @return
     */
    public int getTo(){
        return Math.min(current + 2, getTotal());
    }
}
