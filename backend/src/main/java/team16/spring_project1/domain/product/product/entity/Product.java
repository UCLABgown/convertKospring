package team16.spring_project1.domain.product.product.entity;

import jakarta.persistence.Entity;
import lombok.*;
import team16.spring_project1.global.jpa.entity.BaseTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product extends BaseTime {
    //test
    private String productName;
    private int price;
    private String imageUrl;
    private String category;
}
