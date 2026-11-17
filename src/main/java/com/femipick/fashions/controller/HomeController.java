package com.femipick.fashions.controller;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.femipick.fashions.entity.Category;
import com.femipick.fashions.repository.CategoryRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;


@Controller
public class HomeController {

	@Autowired
    private CategoryRepository categoryRepository = null;

    @GetMapping("/")
    public String home(Model model) {
    	
    	System.out.println(categoryRepository.findAll());
    	List<Category> categories = categoryRepository.findAll();
    	for (Category category : categories) {
    	    System.out.println(category.getId());
    	}
        model.addAttribute(
                "categories",
                categoryRepository.findAll()
        );

        return "index";
    }
}