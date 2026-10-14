package com.riwi.eventifyt.service;

import com.riwi.eventifyt.DTO.CategoryResponse;
import com.riwi.eventifyt.mapper.EventMapper;
import com.riwi.eventifyt.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;

    public List<CategoryResponse> listAll() {
        return categoryRepository.findAll(Sort.by("name")).stream()
                .map(eventMapper::toCategoryResponse)
                .toList();
    }
}
