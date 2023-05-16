package com.example.demo.service.functionnal;

import com.example.demo.dto.BaseDTO;
import com.example.demo.tools.paging.PageRequest;
import com.example.demo.tools.paging.PagedResponse;
import com.example.demo.tools.searching.SearchRequest;
import com.example.demo.tools.sorting.SortRequest;

public interface ISortSearchingService<DTO extends BaseDTO> extends ISortingService<DTO>, ISearchingService<DTO> {
    PagedResponse<DTO> sortSearch(SortRequest sortRequest, SearchRequest searchRequest, PageRequest pageRequest);
}
