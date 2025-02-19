package team16.spring_project1.domain.product.product.DTO;

import lombok.Getter;
import org.springframework.lang.NonNull;
import team16.spring_project1.domain.product.product.entity.Product;

import java.time.LocalDateTime;

@Getter
public class ProductDto {
    @NonNull
    private long id;
    @NonNull
    private LocalDateTime createDate;
    @NonNull
    private LocalDateTime modifyDate;
    @NonNull
    private String productName;
    @NonNull
    private int price;
    private String imageUrl;
    @NonNull
    private String category;

    public ProductDto(Product product) {
        this.id = product.getId();
        this.createDate = product.getCreateDate();
        this.modifyDate = product.getModifyDate();
        this.productName = product.getProductName();
        this.price = product.getPrice();
        this.imageUrl = product.getImageUrl();
        this.category = product.getCategory();
    }
    public ProductDto(String category) {
        this.category = category;
    }
}
