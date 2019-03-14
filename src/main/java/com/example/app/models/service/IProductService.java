/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.app.models.service;

import com.example.app.models.entity.Product;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 *
 * @author d-ani
 */
public interface IProductService {
    public List<Product> findAll();
    public Page<Product> findAll(Pageable pageable);
    public Product findOne(Long id);
    public void delete(Long id);
    public void save(Product product);

}
