package com.riwi.eventifyt.repository;

import com.riwi.eventifyt.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
