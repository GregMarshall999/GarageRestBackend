package com.example.demo.service.functionnal;

import com.example.demo.dto.BaseDTO;
import com.example.demo.tools.paging.PageRequest;
import com.example.demo.tools.paging.PagedResponse;
import com.example.demo.tools.sorting.SortRequest;

public interface ISortingService<DTO extends BaseDTO> {
    PagedResponse<DTO> sort(SortRequest sortRequest, PageRequest pageRequest);
}
