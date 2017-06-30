package com.cloudwise.sap.niping.common.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
public class Page<T> {
    private long totalCount;
    private long totalPageSize;
    @Getter
    private long pageSize;
    @Getter
    private long pageNo;
    @Setter
    private List<T> data;

    public Page(Long pageNo, Long pageSize) {

        if (null == pageSize) {
            pageSize = 10L;
        } else {
            this.pageSize = pageSize;
        }
        if (null == pageNo) {
            pageNo = 1L;
        } else {
            this.pageNo = pageNo;
        }
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
        if (totalCount > 0 && pageSize > 0) {
            this.totalPageSize = ((totalCount + pageSize - 1) / pageSize);
        }
    }

    public long getOffset() {
        return (this.pageNo - 1) * this.pageSize;
    }

}