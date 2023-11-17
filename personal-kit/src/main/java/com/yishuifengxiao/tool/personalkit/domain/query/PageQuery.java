//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.yishuifengxiao.tool.personalkit.domain.query;

import com.yishuifengxiao.common.tool.lang.CompareUtil;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Optional;

/**
 * @author yishui
 */
public class PageQuery<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int DEFAULT_PAGE_NUM = 1;
    @ApiModelProperty(
            value = "分页大小",
            example = "10"
    )
    protected Integer pageSize;
    @ApiModelProperty(
            value = "当前页页码,从1开始",
            example = "1"
    )
    protected Integer pageNum;

    protected T query;

    public T getQuery() {
        return this.query;
    }

    public Optional<T> queryBody() {
        return Optional.ofNullable(this.query);
    }

    public PageQuery<T> setQuery(T query) {
        this.query = query;
        return this;
    }

    public Integer getPageSize() {
        return this.pageSize;
    }

    public PageQuery<T> setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public Integer getPageNum() {
        return this.pageNum;
    }

    public PageQuery<T> setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
        return this;
    }

    public PageQuery(Integer pageSize, Integer pageNum) {
        this.pageSize = pageSize;
        this.pageNum = pageNum;
    }

    public int pageSize() {
        return CompareUtil.gtZero(this.pageSize) ? this.pageSize : 10;
    }

    public int pageNum() {
        return CompareUtil.gtZero(this.pageNum) ? this.pageNum : 1;
    }

    public PageQuery() {
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("BaseQuery [pageSize=");
        builder.append(this.pageSize);
        builder.append(", pageNum=");
        builder.append(this.pageNum);
        builder.append("]");
        return builder.toString();
    }

    @Override
    public int hashCode() {
        boolean prime = true;
        int result = 1;
        result = 31 * result + (this.pageNum == null ? 0 : this.pageNum.hashCode());
        result = 31 * result + (this.pageSize == null ? 0 : this.pageSize.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (this.getClass() != obj.getClass()) {
            return false;
        } else {
            PageQuery other = (PageQuery) obj;
            if (this.pageNum == null) {
                if (other.pageNum != null) {
                    return false;
                }
            } else if (!this.pageNum.equals(other.pageNum)) {
                return false;
            }

            if (this.pageSize == null) {
                if (other.pageSize != null) {
                    return false;
                }
            } else if (!this.pageSize.equals(other.pageSize)) {
                return false;
            }

            return true;
        }
    }
}
