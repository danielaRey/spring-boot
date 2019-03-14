/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.app.models.service;

import com.example.app.models.dao.IProductDao;
import com.example.app.models.entity.Product;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author d-ani
 */
@Service //** throw error if i dont use it
public class ProductService implements IProductService{

    @Autowired
    private IProductDao productDao;
   
    public List<Product> findAll() {
        return (List<Product>) productDao.findAll();
    }

    @Override
    @Transactional
    public Page<Product> findAll(Pageable pageable) {
        return productDao.findAll(pageable);
    }

	@Override
	public Product findOne(Long id) {
		return productDao.findById(id).orElse(null);
	}

	@Override
	public void delete(Long id) {
		productDao.deleteById(id);
	}

	@Override
	public void save(Product product) {
		productDao.save(product);
	}

    
}
