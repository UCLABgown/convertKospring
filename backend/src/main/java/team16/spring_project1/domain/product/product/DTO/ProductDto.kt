package team16.spring_project1.domain.product.product.DTO

import lombok.Getter
import org.springframework.lang.NonNull
import team16.spring_project1.domain.product.product.entity.Product
import java.time.LocalDateTime

class ProductDto {
    @field:NonNull
    var id: Long = 0

    @field:NonNull
    var createDate: LocalDateTime? = null

    @field:NonNull
    var modifyDate: LocalDateTime? = null

    @field:NonNull
    var productName: String? = null

    @field:NonNull
    var price = 0
    var imageUrl: String? = null

    @field:NonNull
    var category: String? = null

    constructor()
    constructor(category: String){
        this.category = category
    }
    constructor(product: Product) {
        this.id = product.id!!
        this.createDate = product.createDate
        this.modifyDate = product.modifyDate
        this.productName = product.productName
        this.price = product.price
        this.imageUrl = product.imageUrl
        this.category = product.category
    }

}
