package com.riwi.eventifyt.service;

import com.riwi.eventifyt.DTO.CategoryResponseDTO;
import com.riwi.eventifyt.domain.Category;
import com.riwi.eventifyt.mapper.CategoryMapper;
import com.riwi.eventifyt.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryResponseDTO> listAll() {
        List<CategoryResponseDTO> result = new ArrayList<>();
        for (Category category : categoryRepository.findAll(Sort.by("name"))) {
            result.add(categoryMapper.toResponseDTO(category));
        }
        return result;
    }
}
