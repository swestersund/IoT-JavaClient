package com.iotticket.api.v1.model;

import com.google.gson.annotations.SerializedName;

import java.util.Collection;

public class PagedResult<T> {

    @SerializedName("items")
    private Collection<T> results;

    @SerializedName("offset")
    private int skip;

    @SerializedName("limit")
    private int requestedCount;

    @SerializedName("fullSize")
    private int totalCount;

    public Collection<T> getResults() {
        return results;
    }

    public void setResults(Collection<T> results) {
        this.results = results;
    }


    public int getSkip() {
        return skip;
    }

    public void setSkip(int skip) {
        this.skip = skip;
    }

    public int getRequestedCount() {
        return requestedCount;
    }

    public void setRequestedCount(int requestedCount) {
        this.requestedCount = requestedCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}