package team16.spring_project1.domain.product.product.Controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import lombok.RequiredArgsConstructor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import team16.spring_project1.domain.product.product.DTO.ProductDto
import team16.spring_project1.domain.product.product.DTO.ProductRequest
import team16.spring_project1.domain.product.product.Service.ProductService
import team16.spring_project1.domain.product.product.entity.Product
import team16.spring_project1.global.apiResponse.ApiResponse
import team16.spring_project1.global.enums.SearchKeywordType
import team16.spring_project1.standard.page.dto.PageDto


@RequiredArgsConstructor
@RequestMapping("/products")
@RestController
@Tag(name = "ProductController", description = "상품 관리 API")
class ProductController (
    val productService: ProductService
){

    @Operation(summary = "Create Product", description = "새로운 상품을 등록합니다.")
    @PostMapping
    @Transactional
    fun create(@Valid@RequestBody productRequest:  ProductRequest): ResponseEntity<ApiResponse<String>> {
        productService.create(productRequest)
        return ResponseEntity.ok(ApiResponse.success("상품 등록에 성공했습니다."))
    }

    @Operation(summary = "Get All Products", description = "모든 상품 목록을 가져옵니다.")
    @GetMapping
    @Transactional(readOnly = true)
    fun items(
        @RequestParam(defaultValue = "productName") searchKeywordType: SearchKeywordType,
        @RequestParam(defaultValue = "") searchKeyword: String,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "8") pageSize: Int
    ): ResponseEntity<ApiResponse<PageDto<ProductDto>>> {
        return ResponseEntity.ok(
            ApiResponse.success(
                PageDto(
                    productService.findByPaged(searchKeywordType, searchKeyword, page, pageSize)
                        .map { product: Product ->
                            ProductDto(
                                product
                            )
                        }
                )
            ))
    }

    @Operation(summary = "Get All Categories", description = "모든 카테고리를 가져옵니다.")
    @GetMapping("/categories")
    @Transactional
    fun categories(): ResponseEntity<ApiResponse<List<ProductDto>>> {
        return ResponseEntity.ok(
            ApiResponse.success(
                productService.findAllCategory()
            )
        )
    }

    @Operation(summary = "Get Product by ID", description = "상품 ID를 기준으로 특정 상품을 가져옵니다.")
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    fun item(@PathVariable id: Long): ResponseEntity<ApiResponse<ProductDto>> {
        val product = productService.findById(id).orElseThrow { NoSuchElementException("해당 상품은 존재하지 않습니다.") }

        return ResponseEntity.ok(ApiResponse.success(ProductDto(product)))
    }


    @Operation(summary = "Update Product Status", description = "상품 정보를 업데이트합니다.")
    @PutMapping("/{id}")
    @Transactional
    fun modify(
        @Valid@RequestBody productRequest: ProductRequest,
        @PathVariable("id") id: Long
    ): ResponseEntity<ApiResponse<String>> {
        val product = productService.findById(id).orElseThrow { NoSuchElementException("해당 상품은 존재하지 않습니다.") }

        productService.modify(product, productRequest)
        return ResponseEntity.ok(ApiResponse.success("상품정보가 성공적으로 수정되었습니다."))
    }

    @Operation(summary = "Delete Product", description = "상품을 삭제합니다.")
    @DeleteMapping("/{id}")
    @Transactional
    fun delete(@PathVariable id: Long): ResponseEntity<ApiResponse<String>> {
        val product =
            productService.findById(id).orElseThrow { NoSuchElementException("해당 상품은 이미 삭제되었거나 존재하지 않습니다.") }
        productService.delete(product)
        return ResponseEntity.ok(ApiResponse.success("상품이 성공적으로 삭제되었습니다."))
    }

    @Operation(summary = "Upload Image", description = "상품 이미지를 추가합니다.")
    @PostMapping("/image")
    @Transactional
    fun upload(
        @RequestBody @RequestParam(
            value = "file",
            required = false
        ) file: MultipartFile
    ): ResponseEntity<ApiResponse<String>> {
        val response = productService.upload(file)
        if (response.containsKey(false)) return ResponseEntity.ok(
            ApiResponse.failure(
                response[false]
            )
        )

        return ResponseEntity.ok(
            ApiResponse.success(
                response[true]
            )
        )
    }
}
