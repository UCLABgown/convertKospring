package team16.spring_project1.domain.product.product.entity

import jakarta.persistence.Entity
import team16.spring_project1.global.jpa.entity.BaseTime

@Entity
class Product : BaseTime {
    //test
    lateinit var productName: String
    var price = 0
    lateinit var imageUrl: String
    lateinit var category: String

    constructor()
    constructor(productName: String,price:Int,imageUrl:String,category:String){
        this.productName = productName
        this.price = price
        this.imageUrl = imageUrl
        this.category =category
    }
}
