package com.femipick.fashions.controller;

import org.springframework.stereotype.Controller;


import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.femipick.fashions.entity.Category;
import com.femipick.fashions.entity.CollectionItem;
import com.femipick.fashions.repository.CategoryRepository;
import com.femipick.fashions.repository.CollectionRepository;

import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;


@Controller
public class HomeController {

	@Autowired
    private CategoryRepository categoryRepository = null;
	
	 @Autowired
	    private CollectionRepository collectionRepository;
	
    @Value("${app.admin.secret}")
	private String adminSecret;

    @GetMapping("/")
    public String home(@RequestParam(required = false) String admin,
                       HttpSession session,
                       Model model) {
        if (admin != null && admin.equals(adminSecret)) {
            session.setAttribute("isAdmin", true);
        }

        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        model.addAttribute("isAdmin", Boolean.TRUE.equals(isAdmin));

        // 1. Fetch all images uploaded under "Trending Collections"
        List<CollectionItem> trendingItems = collectionRepository.findByCategory_Name("Trending Collections");
        model.addAttribute("trendingItems", trendingItems);

        // 2. Fetch remaining categories for the "Our Collections" cards below
        List<Category> otherCategories = categoryRepository.findAll().stream()
                .filter(c -> !"Trending Collections".equalsIgnoreCase(c.getName()))
                .toList();
        model.addAttribute("categories", otherCategories);

        return "index";
    }
    
    @GetMapping("/upload")
    public String showUploadPage(HttpSession session, Model model) {
        if (!Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            return "redirect:/";
        }
        System.out.println("In Admin Uplaod Page");
        model.addAttribute("categories",  categoryRepository.findAll());
        return "upload";
    }

    // 2. Save collection name and image to MySQL
    @PostMapping("/upload")
    public String handleUpload(@RequestParam("name") String name,
                               @RequestParam("price") Double price,
                               @RequestParam("description") String description,
                               @RequestParam("image") MultipartFile file,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) throws IOException {
        if (!Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            return "redirect:/";
        }

        Category category = categoryRepository.findById(name)
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + name));

        CollectionItem item = new CollectionItem();
        item.setCategory(category);
        item.setPrice(price);
        item.setDescription(description);
        item.setContentType(file.getContentType());
        item.setImageData(file.getBytes());
        collectionRepository.save(item);

        redirectAttributes.addFlashAttribute("message", "Collection uploaded successfully!");
        return "redirect:/upload";
    }

    // 3. Endpoint to display saved images on index.html
  
}