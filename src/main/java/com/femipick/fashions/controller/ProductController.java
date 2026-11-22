package com.femipick.fashions.controller;

import com.femipick.fashions.entity.Category;
import com.femipick.fashions.entity.CollectionItem;
import com.femipick.fashions.repository.CategoryRepository;
import com.femipick.fashions.repository.CollectionRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
public class ProductController {

    @Autowired
    private CollectionRepository collectionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping("/products")
    public String products(@RequestParam(required = false) Long category, Model model) {
        List<Category> allCategories = categoryRepository.findAll();
        List<CollectionItem> items;
        String selectedCategoryName = "All Jewellery Collections";

        if (category != null) {
            items = collectionRepository.findByCategory_Id(category);
            for (Category cat : allCategories) {
                if (cat.getId() != null && cat.getId().equals(category)) {
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
    public String productDetails(@PathVariable Long id, HttpSession session, Model model) {
        Optional<CollectionItem> itemOpt = collectionRepository.findById(id);
        if (itemOpt.isEmpty()) {
            return "redirect:/products";
        }

        boolean isAdmin = Boolean.TRUE.equals(session.getAttribute("isAdmin"));
        model.addAttribute("product", itemOpt.get());
        model.addAttribute("isAdmin", isAdmin);
        return "product-details";
    }

    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id, HttpSession session) {
        if (!Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            return "redirect:/";
        }

        Optional<CollectionItem> itemOpt = collectionRepository.findById(id);
        Long categoryId = null;
        if (itemOpt.isPresent()) {
            if (itemOpt.get().getCategory() != null) {
                categoryId = itemOpt.get().getCategory().getId();
            }
            collectionRepository.deleteById(id);
        }

        return (categoryId != null) ? "redirect:/products?category=" + categoryId : "redirect:/products";
    }

    @GetMapping("/products/edit/{id}")
    public String showEditPage(@PathVariable Long id, HttpSession session, Model model) {
        if (!Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            return "redirect:/";
        }

        Optional<CollectionItem> itemOpt = collectionRepository.findById(id);
        if (itemOpt.isEmpty()) {
            return "redirect:/products";
        }

        model.addAttribute("product", itemOpt.get());
        return "edit-product";
    }

    @PostMapping("/products/edit/{id}")
    public String handleEdit(@PathVariable Long id,
                             @RequestParam("price") Double price,
                             @RequestParam("description") String description,
                             @RequestParam(value = "image", required = false) MultipartFile file,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) throws IOException {
        if (!Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            return "redirect:/";
        }

        Optional<CollectionItem> itemOpt = collectionRepository.findById(id);
        if (itemOpt.isEmpty()) {
            return "redirect:/products";
        }

        CollectionItem item = itemOpt.get();
        item.setPrice(price);
        item.setDescription(description);

        if (file != null && !file.isEmpty()) {
            item.setContentType(file.getContentType());
            item.setImageData(file.getBytes());
        }

        collectionRepository.save(item);
        redirectAttributes.addFlashAttribute("message", "Collection updated successfully!");
        return "redirect:/products/" + id;
    }

    @GetMapping("/collection/image/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getCollectionImage(@PathVariable Long id) {
        Optional<CollectionItem> itemOpt = collectionRepository.findById(id);

        if (itemOpt.isPresent() && itemOpt.get().getImageData() != null) {
            CollectionItem item = itemOpt.get();
            MediaType mediaType = (item.getContentType() != null)
                    ? MediaType.parseMediaType(item.getContentType())
                    : MediaType.IMAGE_JPEG;
            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .body(item.getImageData());
        }
        return ResponseEntity.notFound().build();
    }
}
