/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.app.models.dao;

import com.example.app.models.entity.Product;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 *
 * @author d-ani
 */

public interface IProductDao extends PagingAndSortingRepository<Product, Long>  {
    
}
