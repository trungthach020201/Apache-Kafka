package com.study.repository;

import com.study.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductReposiroty extends JpaRepository<Product,Long> {
}
