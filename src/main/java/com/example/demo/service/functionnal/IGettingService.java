package com.example.demo.service.functionnal;

import com.example.demo.dto.BaseDTO;

public interface IGettingService<DTO extends BaseDTO> {
    DTO getById(long id);
}
