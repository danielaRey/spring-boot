/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.app.controller;

import com.example.app.models.entity.Product;
import com.example.app.models.service.IProductService;
import com.example.app.models.service.IUploadFileService;
import com.example.app.util.paginator.PageRender;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


/**
 *
 * @author d-ani
 */
@Controller
@SessionAttributes("product")
public class ProductController {

	@Autowired
	private IProductService productService;

	@Autowired
	private IUploadFileService uploadFileService;

	@GetMapping(value = "/uploads/{filename:.+}")
	public ResponseEntity<Resource> showPhoto(@PathVariable String filename) {

		Resource resource = null;

		try {
			resource = (Resource) uploadFileService.load(filename);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + ((org.springframework.core.io.Resource) resource).getFilename() + "\"")
				.body(resource);
	}

	@GetMapping(value = "/show/{id}")
	public String show(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash) {

		Product product= productService.findOne(id);
		if (product == null) {
			flash.addFlashAttribute("error", "Product does not exist on db");
			return "redirect:/index";
		}
		model.put("product", product);
		model.put("tittle", "Product Detail: " + product.getName());
		return "show";
	}

	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {

		Pageable pageRequest = PageRequest.of(page, 4);

		Page<Product> products = productService.findAll(pageRequest);

		PageRender<Product> pageRender = new PageRender<Product>("/index", products);
		model.addAttribute("tittle", "Product List");
		model.addAttribute("products", products);
		model.addAttribute("page", pageRender);
		return "index";

	}

	@RequestMapping(value = "/form")
	public String create(Model model) {

		Product product = new Product();
		model.addAttribute("product", product);
		model.addAttribute("tittle", "Create Product");
		return "form";
	}

	@RequestMapping(value = "/form/{id}")
	public String edit(@PathVariable(value = "id") Long id,Model model, RedirectAttributes flash) {

		Product product = null;

		if (id > 0) {
			product = productService.findOne(id);
			if (product == null) {
				flash.addFlashAttribute("error", "El ID del product no existe en la BBDD!");
				return "redirect:/index";
			}
		} else {
			flash.addFlashAttribute("error", "ID can not be  cero");
			return "redirect:/index";
		}
		model.addAttribute("product", product);
		model.addAttribute("tittle", "Edit Product");
		return "form";
	}

	
	@RequestMapping(value = "/form", method = RequestMethod.POST)
	public String save(@Valid Product product, BindingResult result, Model model,
			@RequestParam("file") MultipartFile foto, RedirectAttributes flash, SessionStatus status) {

		if (result.hasErrors()) {
			model.addAttribute("tittle", "Product Form");
			return "form";
		}

		if (!foto.isEmpty()) {

			if (product.getId() != null && product.getId() > 0 && product.getPhoto() != null
					&& product.getPhoto().length() > 0) {

				uploadFileService.delete(product.getPhoto());
			}

			String uniqueFilename = null;
			try {
				uniqueFilename = uploadFileService.copy(foto);
			} catch (IOException e) {
				e.printStackTrace();
			}

			flash.addFlashAttribute("info", "correctly uploaded '" + uniqueFilename + "'");

			product.setPhoto(uniqueFilename);

		}else product.setPhoto("");

		String mensajeFlash = (product.getId() != null) ? "Product edited successfully!" : "Product created successfully!";

		productService.save(product);
		status.setComplete();
		flash.addFlashAttribute("success", mensajeFlash);
		return "redirect:index";
	}

	@RequestMapping(value = "/delete/{id}")
	public String delete(@PathVariable(value = "id") Long id, RedirectAttributes flash) {
		if (id > 0) {
			Product product = productService.findOne(id);

			productService.delete(id);
			flash.addFlashAttribute("success", "Product deleted successfully!");

			if (uploadFileService.delete(product.getPhoto())) {
				flash.addFlashAttribute("info", "Photo " + product.getPhoto() + " deleted Successfully!");
			}

		}
		return "redirect:/index";
//		productService.delete(id);
//		return "redirect:/index";
	}

}
