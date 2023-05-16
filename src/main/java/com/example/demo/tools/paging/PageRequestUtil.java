package com.example.demo.tools.paging;

public final class PageRequestUtil {
    private static final int DEFAULT_PAGE_SIZE = 30;

    public static PagingRequest toPageRequest(final PageRequest request) {
        if(request == null) {
            return new PagingRequest(0, DEFAULT_PAGE_SIZE);
        }

        final int requestedSize = request.getSize();
        return new PagingRequest(request.getPage(), requestedSize == 0 ? DEFAULT_PAGE_SIZE : requestedSize);
    }
}
