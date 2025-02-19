package team16.spring_project1.domain.product.product.Controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import team16.spring_project1.domain.member.Entity.Member;
import team16.spring_project1.domain.member.Service.MemberService;
import team16.spring_project1.domain.product.product.Service.ProductService;
import team16.spring_project1.domain.product.product.entity.Product;
import team16.spring_project1.global.enums.SearchKeywordType;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class ProductControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ProductService productService;
    @Autowired
    MemberService memberService;

    @Test
    @DisplayName("상품 등록")
    void t1() throws Exception {
        Member member = memberService.findByUsername("admin").get();
        String memberAuthToken = memberService.getAuthToken(member);

        ResultActions resultActions = mvc
                .perform(post("/products")
                        .header("Authorization", "Bearer " + memberAuthToken)
                        .content("""
                                {
                                    "category": "테스트 카테고리",
                                    "imageUrl": "https://naver.com",
                                    "productName": "테스트 제품 이름",
                                    "price": 5000
                                }
                                """.stripIndent())
                        .contentType(
                                new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                        )
                )
                .andDo(print());

        Product product = productService.findLatest().get();

        assertThat(product.getProductName()).isEqualTo("테스트 제품 이름");
        assertThat(product.getImageUrl()).isEqualTo("https://naver.com");
        assertThat(product.getCategory()).isEqualTo("테스트 카테고리");
        assertThat(product.getPrice()).isEqualTo(5000);

        resultActions
                .andExpect(handler().handlerType(ProductController.class))
                .andExpect(handler().methodName("create"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("상품 등록에 성공했습니다."));
    }

    @Test
    @DisplayName("상품 등록, with no input")
    void t1_1() throws Exception {
        Member member = memberService.findByUsername("admin").get();
        String memberAuthToken = memberService.getAuthToken(member);

        ResultActions resultActions = mvc
                .perform(post("/products")
                        .header("Authorization", "Bearer " + memberAuthToken)
                        .content("""
                                {
                                    "category": "",
                                    "imageUrl": "",
                                    "productName": "",
                                    "price": 0
                                }
                                """.stripIndent())
                        .contentType(
                                new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                        )
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ProductController.class))
                .andExpect(handler().methodName("create"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("""
                        category-NotBlank-must not be blank
                        price-Min-must be greater than or equal to 1
                        productName-NotBlank-must not be blank
                        """.stripIndent().trim()));
    }

    @Test
    @DisplayName("상품 다건 조회")
    void t2() throws Exception {
        ResultActions resultActions = mvc
                .perform(get("/products"))
                .andDo(print());

        Page<Product> productPage = productService
                .findAll(1, 8);

        resultActions
                .andExpect(handler().handlerType(ProductController.class))
                .andExpect(handler().methodName("items"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("요청이 성공했습니다."))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.totalItems").value(productPage.getTotalElements()))
                .andExpect(jsonPath("$.content.totalPages").value(productPage.getTotalPages()))
                .andExpect(jsonPath("$.content.currentPageNumber").value(1))
                .andExpect(jsonPath("$.content.pageSize").value(8));

        List<Product> products = productPage.getContent();

        for(int i = 0; i < products.size(); i++) {
            Product product = products.get(i);

            resultActions
                    .andExpect(jsonPath("$.content.items[%d].id".formatted(i)).value(product.getId()))
                    .andExpect(jsonPath("$.content.items[%d].createDate".formatted(i)).exists())
                    .andExpect(jsonPath("$.content.items[%d].modifyDate".formatted(i)).exists())
                    .andExpect(jsonPath("$.content.items[%d].productName".formatted(i)).value(product.getProductName()))
                    .andExpect(jsonPath("$.content.items[%d].price".formatted(i)).value(product.getPrice()))
                    .andExpect(jsonPath("$.content.items[%d].imageUrl".formatted(i)).value(product.getImageUrl()))
                    .andExpect(jsonPath("$.content.items[%d].category".formatted(i)).value(product.getCategory()));
        }
    }

    @Test
    @DisplayName("상품 다건 조회 with searchKeyword=에티오피아")
    void t2_1() throws Exception {
        ResultActions resultActions = mvc
                .perform(get("/products?page=1&pageSize=8&searchKeyword=에티오피아"))
                .andDo(print());

        Page<Product> productPage = productService
                .findByPaged(SearchKeywordType.productName, "에티오피아", 1, 8);

        resultActions
                .andExpect(handler().handlerType(ProductController.class))
                .andExpect(handler().methodName("items"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("요청이 성공했습니다."))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.totalItems").value(productPage.getTotalElements()))
                .andExpect(jsonPath("$.content.totalPages").value(productPage.getTotalPages()))
                .andExpect(jsonPath("$.content.currentPageNumber").value(1))
                .andExpect(jsonPath("$.content.pageSize").value(8));

        List<Product> products = productPage.getContent();

        for(int i = 0; i < products.size(); i++) {
            Product product = products.get(i);

            resultActions
                    .andExpect(jsonPath("$.content.items[%d].id".formatted(i)).value(product.getId()))
                    .andExpect(jsonPath("$.content.items[%d].createDate".formatted(i)).exists())
                    .andExpect(jsonPath("$.content.items[%d].modifyDate".formatted(i)).exists())
                    .andExpect(jsonPath("$.content.items[%d].productName".formatted(i)).value(product.getProductName()))
                    .andExpect(jsonPath("$.content.items[%d].price".formatted(i)).value(product.getPrice()))
                    .andExpect(jsonPath("$.content.items[%d].imageUrl".formatted(i)).value(product.getImageUrl()))
                    .andExpect(jsonPath("$.content.items[%d].category".formatted(i)).value(product.getCategory()));
        }
    }

    @Test
    @DisplayName("상품 다건 조회 with searchKeywordType=category&searchKeyword=쥬스")
    void t2_2() throws Exception {
        ResultActions resultActions = mvc
                .perform(get("/products?page=1&pageSize=8&searchKeywordType=category&searchKeyword=쥬스"))
                .andDo(print());

        Page<Product> productPage = productService
                .findByPaged(SearchKeywordType.category, "쥬스", 1, 8);

        resultActions
                .andExpect(handler().handlerType(ProductController.class))
                .andExpect(handler().methodName("items"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("요청이 성공했습니다."))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.totalItems").value(productPage.getTotalElements()))
                .andExpect(jsonPath("$.content.totalPages").value(productPage.getTotalPages()))
                .andExpect(jsonPath("$.content.currentPageNumber").value(1))
                .andExpect(jsonPath("$.content.pageSize").value(8));

        List<Product> products = productPage.getContent();

        for(int i = 0; i < products.size(); i++) {
            Product product = products.get(i);

            resultActions
                    .andExpect(jsonPath("$.content.items[%d].id".formatted(i)).value(product.getId()))
                    .andExpect(jsonPath("$.content.items[%d].createDate".formatted(i)).exists())
                    .andExpect(jsonPath("$.content.items[%d].modifyDate".formatted(i)).exists())
                    .andExpect(jsonPath("$.content.items[%d].productName".formatted(i)).value(product.getProductName()))
                    .andExpect(jsonPath("$.content.items[%d].price".formatted(i)).value(product.getPrice()))
                    .andExpect(jsonPath("$.content.items[%d].imageUrl".formatted(i)).value(product.getImageUrl()))
                    .andExpect(jsonPath("$.content.items[%d].category".formatted(i)).value(product.getCategory()));
        }
    }

    @Test
    @DisplayName("상품 다건 조회 with searchKeywordType=category&searchKeyword=전체")
    void t2_3() throws Exception {
        ResultActions resultActions = mvc
                .perform(get("/products?page=1&pageSize=8&searchKeywordType=category&searchKeyword=전체"))
                .andDo(print());

        Page<Product> productPage = productService
                .findByPaged(SearchKeywordType.category, "전체", 1, 8);

        resultActions
                .andExpect(handler().handlerType(ProductController.class))
                .andExpect(handler().methodName("items"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("요청이 성공했습니다."))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.totalItems").value(productPage.getTotalElements()))
                .andExpect(jsonPath("$.content.totalPages").value(productPage.getTotalPages()))
                .andExpect(jsonPath("$.content.currentPageNumber").value(1))
                .andExpect(jsonPath("$.content.pageSize").value(8));

        List<Product> products = productPage.getContent();

        for(int i = 0; i < products.size(); i++) {
            Product product = products.get(i);

            resultActions
                    .andExpect(jsonPath("$.content.items[%d].id".formatted(i)).value(product.getId()))
                    .andExpect(jsonPath("$.content.items[%d].createDate".formatted(i)).exists())
                    .andExpect(jsonPath("$.content.items[%d].modifyDate".formatted(i)).exists())
                    .andExpect(jsonPath("$.content.items[%d].productName".formatted(i)).value(product.getProductName()))
                    .andExpect(jsonPath("$.content.items[%d].price".formatted(i)).value(product.getPrice()))
                    .andExpect(jsonPath("$.content.items[%d].imageUrl".formatted(i)).value(product.getImageUrl()))
                    .andExpect(jsonPath("$.content.items[%d].category".formatted(i)).value(product.getCategory()));
        }
    }

    @Test
    @DisplayName("상품 수정 성공")
    void t3_1() throws Exception {
        Member member = memberService.findByUsername("admin").get();
        String memberAuthToken = memberService.getAuthToken(member);

        mvc.perform(put("/products/1")
                        .header("Authorization", "Bearer " + memberAuthToken)
                        .content("""
                                {
                                    "category": "수정된커피콩",
                                    "imageUrl": "/",
                                    "productName": "수정된 커피콩",
                                    "price": 9999
                                }
                                """.stripIndent())
                        .contentType(
                                new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                        ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("상품정보가 성공적으로 수정되었습니다."));


    }
    @Test
    @DisplayName("상품 수정 실패")
    void t3_2() throws Exception {
        Member member = memberService.findByUsername("admin").get();
        String memberAuthToken = memberService.getAuthToken(member);

        mvc.perform(put("/products/9999")
                        .header("Authorization", "Bearer " + memberAuthToken)
                        .content("""
                                {
                                    "category": "수정된커피콩",
                                    "imageUrl": "/",
                                    "productName": "수정된 커피콩",
                                    "price": 9999
                                }
                                """.stripIndent())
                        .contentType(
                                new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                        ))
                .andExpect(jsonPath("$.message").value("해당 상품은 존재하지 않습니다."));


    }

    @Test
    @DisplayName("상품 단건 조회")
    void t4() throws Exception {
        ResultActions resultActions = mvc
                .perform(get("/products/1"))
                .andDo(print());

        Product product = productService.findById(1).get();

        resultActions
                .andExpect(handler().handlerType(ProductController.class))
                .andExpect(handler().methodName("item"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("요청이 성공했습니다."))
                .andExpect(jsonPath("$.content.id").value(product.getId()))
                .andExpect(jsonPath("$.content.createDate").exists())
                .andExpect(jsonPath("$.content.modifyDate").exists())
                .andExpect(jsonPath("$.content.productName").value(product.getProductName()))
                .andExpect(jsonPath("$.content.price").value(product.getPrice()))
                .andExpect(jsonPath("$.content.imageUrl").value(product.getImageUrl()))
                .andExpect(jsonPath("$.content.category").value(product.getCategory()));
    }

    @Test
    @DisplayName("상품 단건 조회, with not exist id")
    void t4_1() throws Exception {
        ResultActions resultActions = mvc
                .perform(get("/products/100000"))
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ProductController.class))
                .andExpect(handler().methodName("item"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("해당 상품은 존재하지 않습니다."));
    }

    @Test
    @DisplayName("상품 삭제 성공")
    void t5_1() throws Exception {
        Member member = memberService.findByUsername("admin").get();
        String memberAuthToken = memberService.getAuthToken(member);

        mvc.perform(delete("/products/1").header("Authorization", "Bearer " + memberAuthToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("상품이 성공적으로 삭제되었습니다."));
    }
    @Test
    @DisplayName("상품 삭제 실패 없는 번호")
    void t5_2() throws Exception {
        Member member = memberService.findByUsername("admin").get();
        String memberAuthToken = memberService.getAuthToken(member);

        mvc.perform(delete("/products/-1").header("Authorization", "Bearer " + memberAuthToken))
                .andExpect(jsonPath("$.message").value("해당 상품은 이미 삭제되었거나 존재하지 않습니다."));

    }
    @Test
    @DisplayName("상품 삭제 실패 삭제된 상품")
    void t5_3() throws Exception {
        Member member = memberService.findByUsername("admin").get();
        String memberAuthToken = memberService.getAuthToken(member);

        mvc.perform(delete("/products/1").header("Authorization", "Bearer " + memberAuthToken))
                .andExpect(jsonPath("$.message").value("상품이 성공적으로 삭제되었습니다."));
        mvc.perform(delete("/products/1").header("Authorization", "Bearer " + memberAuthToken))
                .andExpect(jsonPath("$.message").value("해당 상품은 이미 삭제되었거나 존재하지 않습니다."));
    }
}
