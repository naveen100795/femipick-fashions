package com.femipick.fashions.repository;

import org.springframework.data.jpa.repository.JpaRepository;


import com.femipick.fashions.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, String> {
}