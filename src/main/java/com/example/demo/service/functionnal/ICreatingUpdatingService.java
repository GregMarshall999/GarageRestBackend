package com.example.demo.service.functionnal;

import com.example.demo.dto.BaseDTO;

public interface ICreatingUpdatingService<DTO extends BaseDTO> {
    DTO createOrUpdate(DTO dto);
}
