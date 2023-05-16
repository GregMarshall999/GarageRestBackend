package com.example.demo.tools.paging;

import org.springframework.data.domain.AbstractPageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PagingRequest extends AbstractPageRequest {
    public PagingRequest(int page, int size) {
        super(page, size);
    }

    @Override
    public Pageable next() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Pageable previous() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Pageable first() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Pageable withPage(int pageNumber) {
        return null;
    }

    @Override
    public Sort getSort() {
        return Sort.unsorted();
    }
}
