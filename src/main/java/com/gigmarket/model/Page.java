package com.gigmarket.model;

import java.util.List;

/** One page of results plus what the pager needs. */
public final class Page<T> {
    private final List<T> items;
    private final int page;
    private final int pageSize;
    private final int total;

    public Page(List<T> items, int page, int pageSize, int total) {
        this.items = items;
        this.page = page;
        this.pageSize = pageSize;
        this.total = total;
    }

    public List<T> getItems() { return items; }
    public int getPage() { return page; }
    public int getPageSize() { return pageSize; }
    public int getTotal() { return total; }

    public int getTotalPages() {
        return Math.max(1, (total + pageSize - 1) / pageSize);
    }

    public boolean isHasPrevious() { return page > 1; }
    public boolean isHasNext() { return page < getTotalPages(); }
}
