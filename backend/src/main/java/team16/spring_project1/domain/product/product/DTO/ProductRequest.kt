package team16.spring_project1.domain.product.product.DTO

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import lombok.AllArgsConstructor
import lombok.Builder
import lombok.Data
import lombok.NoArgsConstructor

@Data
class ProductRequest {
    @field:NotBlank
    var productName:  String? = null
    @field:NotBlank
    var category:  String? = null
    @field:Min(1)
    var price:  Int = 0
    var imageUrl: String? = null
}
