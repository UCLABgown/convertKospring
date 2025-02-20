package team16.spring_project1.domain.product.product.Repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import team16.spring_project1.domain.product.product.entity.Product
import java.util.*

interface ProductRepository : JpaRepository<Product, Long> {
    fun findFirstByOrderByIdDesc(): Optional<Product>
    fun findByProductNameLike(searchKeyword: String, pageRequest: PageRequest): Page<Product>
    fun findByCategory(category: String, pageRequest: PageRequest): Page<Product>

    @Query("SELECT DISTINCT category FROM Product")
    fun findDistinctByCategory(): List<String>
}
