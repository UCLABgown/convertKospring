package team16.spring_project1.domain.product.product.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import team16.spring_project1.domain.product.product.entity.Product;

import java.util.Optional;

public interface  ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findFirstByOrderByIdDesc();
    Page<Product> findByProductNameLike(String searchKeyword, PageRequest pageRequest);
    Page<Product> findByCategory(String category, PageRequest pageRequest);
    @Query("SELECT DISTINCT category FROM Product")
    List<String> findDistinctByCategory();

}
