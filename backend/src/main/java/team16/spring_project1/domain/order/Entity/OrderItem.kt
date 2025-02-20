package team16.spring_project1.domain.order.Entity

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import team16.spring_project1.global.jpa.entity.BaseTime

@Entity
@Table(name = "order_item")
class OrderItem: BaseTime {

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    var order:Order? = null

    @NotBlank
    @Schema(description = "상품 이름", example = "원두 1")
    lateinit var productName:String

    @NotNull
    @Min(1)
    @Schema(description = "상품 수량 (1 이상)", example = "1")
    var count:Int = 0

    @NotNull
    @Min(1)
    @Schema(description = "상품 가격 (1 이상)", example = "1000")
    var price:Int = 0

    constructor(){}

    constructor(productName:String,count:Int,price:Int){
        this.productName = productName
        this.count = count
        this.price = price
    }

    fun calculateTotalPrice():Int {
        return count * price;
    }
}
