package com.femipick.fashions.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import com.femipick.fashions.service.ProductService;

@Controller
public class ProductController {

	@Autowired
    private ProductService productService = new ProductService();

    @GetMapping("/products")
    public String products(
            @RequestParam(required = false) Long category,
            Model model) {
        if (category != null) {
            model.addAttribute(
                    "products",
                    productService.getProductsByCategory(category)
            );
        } else {
            model.addAttribute(
                    "products",
                    productService.getAllProducts()
            );
        }

        return "products";
    }

    @GetMapping("/products/{id}")
    public String productDetails(
            @PathVariable Long id,
            Model model) {

        model.addAttribute(
                "product",
                productService.getProduct(id)
        );

        return "product-details";
    }
}