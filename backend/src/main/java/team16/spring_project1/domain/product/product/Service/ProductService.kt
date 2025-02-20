package team16.spring_project1.domain.product.product.Service

import jakarta.transaction.Transactional
import lombok.RequiredArgsConstructor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import team16.spring_project1.domain.product.product.DTO.ProductDto
import team16.spring_project1.domain.product.product.DTO.ProductRequest
import team16.spring_project1.domain.product.product.Repository.ProductRepository
import team16.spring_project1.domain.product.product.entity.Product
import team16.spring_project1.global.configuration.AppConfig
import team16.spring_project1.global.enums.SearchKeywordType
import team16.spring_project1.standard.util.Ut
import java.io.File
import java.io.IOException
import java.util.*
import java.util.stream.Collectors


@RequiredArgsConstructor
@Service
class ProductService (
    val productRepository: ProductRepository
){


    fun create(productRequest: ProductRequest): Product {
        val product = Product(productRequest)
        productRepository.save(product)
        return product
    }

    fun create(productName: String, category: String, price: Int, imageUrl: String): Product {
        val product = Product()
        product.productName = productName
        product.category = category
        product.price = price
        product.imageUrl = imageUrl
        productRepository.save(product)
        return product
    }

    @Transactional
    fun modify(product: Product, productRequest: ProductRequest): Product {
        product.productName = productRequest.productName!!
        product.category = productRequest.category!!
        product.price = productRequest.price
        product.imageUrl = productRequest.imageUrl!!
        productRepository.save(product)
        return product
    }

    fun delete(product: Product?): Boolean {
        if (product == null) return false
        productRepository.delete(product)
        return true
    }

    fun upload(file: MultipartFile): Map<Boolean, String> {
        val response: MutableMap<Boolean, String> = HashMap()
        if (file.isEmpty) {
            response[false] = "이미지 업로드에 실패했습니다."
            return response
        }
        val name = makeFileName(Objects.requireNonNull(file.originalFilename))
        if (name.isEmpty()) {
            response[false] = "지원되지 않는 형식입니다."
            return response
        }
        val staticUrl = AppConfig.getImagesFolder() + name
        val saveUrl = AppConfig.getStaticDirectory() + staticUrl
        val destFile = File(saveUrl)
        if (!destFile.parentFile.exists()) {
            destFile.parentFile.mkdirs()
        }
        try {
            file.transferTo(destFile)
            response[true] = staticUrl
        } catch (e: IOException) {
            throw NoSuchElementException("이미지 업로드에 실패했습니다.")
        }

        return response
    }

    fun makeFileName(file: String): String {
        val attcFileNm = UUID.randomUUID().toString().replace("-".toRegex(), "")
        val attcFileOriExt = fileExtCheck(file.substring(file.lastIndexOf(".")))
        if (attcFileOriExt.isEmpty()) return ""
        return attcFileNm + attcFileOriExt
    }

    fun fileExtCheck(originalFileExtension: String): String {
        val convertFileExtension = originalFileExtension.lowercase(Locale.getDefault())
        if (convertFileExtension == ".jpg" || convertFileExtension == ".gif"
            || convertFileExtension== ".png" || convertFileExtension == ".jpeg"
            || convertFileExtension == ".bmp"
        ) {
            return convertFileExtension
        }
        return ""
    }

    fun count(): Long {
        return productRepository.count()
    }

    fun findLatest(): Optional<Product> {
        return productRepository.findFirstByOrderByIdDesc()
    }

    fun findAll(): List<Product> {
        return productRepository.findAll()
    }

    fun findById(id: Long): Optional<Product> {
        return productRepository.findById(id)
    }


    fun findAll(page: Int, pageSize: Int): Page<Product> {
        val pageRequest = PageRequest.of(
            page - 1, pageSize,
            Sort.by(Sort.Order.desc("id"))
        )

        return productRepository.findAll(pageRequest)
    }

    fun findByPaged(
        searchKeywordType: SearchKeywordType, searchKeyword: String,
        page: Int, pageSize: Int
    ): Page<Product> {
        if (Ut.str.isBlank(searchKeyword) || (searchKeywordType == SearchKeywordType.category
                    && "전체" == searchKeyword)
        ) return findAll(page, pageSize)

        val pageRequest = PageRequest.of(
            page - 1, pageSize,
            Sort.by(Sort.Order.desc("id"))
        )

        return when (searchKeywordType) {
            SearchKeywordType.category -> productRepository.findByCategory(searchKeyword, pageRequest)
            else -> {
                productRepository.findByProductNameLike(searchKeyword, pageRequest)
            }
        }
    }

    fun findAllCategory(): List<ProductDto> {
        val distinctCategories = productRepository.findDistinctByCategory()
        return distinctCategories.stream()
            .map { category: String? -> ProductDto(category!!) }  // String을 받는 생성자를 사용
            .collect(Collectors.toList())
    }
}