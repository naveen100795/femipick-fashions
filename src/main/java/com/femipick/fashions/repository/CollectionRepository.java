package com.femipick.fashions.repository;

import com.femipick.fashions.entity.CollectionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CollectionRepository extends JpaRepository<CollectionItem, Long> {
	 List<CollectionItem> findByCategory_Id(Long categoryId);
	 List<CollectionItem> findByCategory_Name(String name);
}