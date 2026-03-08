package com.example.stocker.repository;

import com.example.stocker.model.Product;
import com.example.stocker.model.Sale;
import com.example.stocker.model.SaleDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SaleDetailRepository extends JpaRepository<SaleDetail, Long>{

    List<SaleDetail> findBySale(Sale sale);

    @Query("SELECT SUM(sd.quantity) FROM SaleDetail sd WHERE sd.product = :product")
    Integer getTotalUnitsSold(@Param("product") Product product);
}
