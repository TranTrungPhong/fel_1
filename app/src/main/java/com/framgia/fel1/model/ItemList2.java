package com.framgia.fel1.model;

/**
 * Created by vuduychuong1994 on 5/2/16.
 */
public class ItemList2 {
    private String id;
    private String content;
    private String detail;

    public ItemList2(String id, String content, String detail) {
        this.id = id;
        this.content = content;
        this.detail = detail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
