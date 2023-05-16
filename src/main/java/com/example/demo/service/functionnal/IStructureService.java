package com.example.demo.service.functionnal;

import com.example.demo.dto.BaseDTO;

public interface IStructureService<DTO extends BaseDTO> {
    DTO getStructure();
}
