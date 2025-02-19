package team16.spring_project1.domain.product.product.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest{
    @NotBlank
    private String productName;
    @NotBlank
    private String category;
    @Min(1)
    private int price;
    private String imageUrl;
}
