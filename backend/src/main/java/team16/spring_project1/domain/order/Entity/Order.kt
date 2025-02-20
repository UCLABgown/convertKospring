package team16.spring_project1.domain.order.Entity

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import team16.spring_project1.global.enums.DeliveryStatus
import team16.spring_project1.global.jpa.entity.BaseTime

@Entity
@Table(name = "orders")

class Order: BaseTime {

    @Email
    @field:NotBlank
    @Schema(description = "이메일", example = "test@gmail.com")
    var email:String? = null

    @Schema(description = "주문 금액", example = "1000")
    @field:NotNull
    @Min(1)
    var totalPrice: Int = 0

    @Enumerated(EnumType.STRING)
    @field:NotNull
    @Schema(description = "상품 상태, 기본 값으로 PAYMENT_COMPLETED 입니다.", example = "PAYMENT_COMPLETED", accessMode = Schema.AccessMode.READ_ONLY)
    var status :DeliveryStatus = DeliveryStatus.PAYMENT_COMPLETED

    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL])
    @Schema(description = "주문에 포함된 상품 목록")
    var orderItems:MutableList<OrderItem> = mutableListOf()
    constructor(){}

    constructor(email:String,totalPrice:Int,orderItems:MutableList<OrderItem>){
        this.email = email
        this.totalPrice = totalPrice
        this.orderItems = orderItems
    }

}
