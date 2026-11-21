package com.femipick.fashions.controller;

import com.femipick.fashions.entity.Category;
import com.femipick.fashions.entity.CollectionItem;
import com.femipick.fashions.repository.CategoryRepository;
import com.femipick.fashions.repository.CollectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class ProductController {

    @Autowired
    private CollectionRepository collectionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping("/products")
    public String products(
            @RequestParam(required = false) Long category,
            Model model) {

        List<Category> allCategories = categoryRepository.findAll();
        List<CollectionItem> items;
        String selectedCategoryName = "All Jewellery Collections";

        if (category != null) {
            items = collectionRepository.findByCategory_Id(category);
            // Find the matching category name for the top heading
            for (Category cat : allCategories) {
                if (cat.getId().equals(category)) {
                    selectedCategoryName = cat.getName();
                    break;
                }
            }
        } else {
            items = collectionRepository.findAll();
        }

        model.addAttribute("categories", allCategories);
        model.addAttribute("products", items);
        model.addAttribute("selectedCategoryId", category);
        model.addAttribute("selectedCategoryName", selectedCategoryName);

        return "products";
    }

    @GetMapping("/products/{id}")
    public String productDetails(@PathVariable Long id, Model model) {
        collectionRepository.findById(id)
                .ifPresent(item -> model.addAttribute("product", item));
        return "product-details";
    }

    @GetMapping("/collection/image/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getCollectionImage(@PathVariable Long id) {
        Optional<CollectionItem> itemOpt = collectionRepository.findById(id);

        if (itemOpt.isPresent()) {
            CollectionItem item = itemOpt.get();
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(item.getContentType()))
                    .body(item.getImageData());
        }
        return ResponseEntity.notFound().build();
    }
}
