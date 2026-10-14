package com.riwi.eventifyt.mapper;

import com.riwi.eventifyt.DTO.CategoryResponseDTO;
import com.riwi.eventifyt.domain.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryResponseDTO toResponseDTO(Category category);
}
