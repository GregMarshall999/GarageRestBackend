package com.example.demo.service.functionnal;

import com.example.demo.dto.BaseDTO;
import com.example.demo.tools.paging.PageRequest;
import com.example.demo.tools.paging.PagedResponse;

public interface IListingService<DTO extends BaseDTO> {
    PagedResponse<DTO> list(PageRequest pageRequest);
}
