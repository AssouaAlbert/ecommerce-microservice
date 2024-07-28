package com.aassoua.productservice;

import com.aassoua.productservice.dto.ProductRequest;
import com.aassoua.productservice.repo.ProductRepo;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@Slf4j
class ProductserviceApplicationTests {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    ProductRepo productRepo;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }


    @Test
    public void shouldReadPructsFromFile() {
        List<ProductRequest> productRequests = getProductsFromJsonFile("src/test/resources/products.json");

        Assertions.assertNotNull(productRequests);
        Assertions.assertFalse(productRequests.isEmpty());
        Assertions.assertEquals(3, productRequests.size());
        productRequests.forEach(product -> {
            try {
                List<ProductRequest> productList = new ArrayList();
                productList.add(product);
                String productString = objectMapper.writeValueAsString(productList);
                mockMvc.perform(MockMvcRequestBuilders.post("/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(productString))
                        .andExpect(status().isOk());
            } catch (JsonProcessingException jsonProcessingException) {
                log.error("JsonProcessingException: " + jsonProcessingException.getMessage());
            } catch (Exception exception) {
                log.error("Exeption - shouldReadPructsFromFile: " + exception.getMessage());
                Assertions.fail("Exception occurred while saving product");
            }

        });
        Assertions.assertEquals(3, productRepo.findAll().size());
    }

    @Test
    public void shouldCreateProduct() {
        ProductRequest productRequest = getProductRequest();
        List<ProductRequest> productRequests = new ArrayList<>();
        productRequests.add(productRequest);
        try {
            String productRequestsString = objectMapper.writeValueAsString(productRequests);
            mockMvc.perform(MockMvcRequestBuilders.post("/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(productRequestsString))
                    .andExpect(status().isOk());
            Assertions.assertEquals(4, productRepo.findAll().size());
        } catch (JsonProcessingException jsonProcessingException) {
            log.error("JsonProcessingException: " + jsonProcessingException.getMessage());
        } catch (Exception error) {
            log.error("UnknownError: " + error.getMessage());
        }
    }

    @Test
    public void shouldFetchAllProducts() {
        try {
            // Perform the GET request to fetch all products
            mockMvc.perform(MockMvcRequestBuilders.get("/products")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isOk());

            // Verify the number of products fetched
            int expectedProductCount = 4;
            int actualProductCount = productRepo.findAll().size();
            Assertions.assertEquals(expectedProductCount, actualProductCount, "The number of products fetched is not as expected");

        } catch (Exception e) {
            // Log the exception and fail the test
            e.printStackTrace();
            Assertions.fail("Exception occurred while fetching products: " + e.getMessage());
        }
    }

    private List<ProductRequest> getProductsFromJsonFile(String filePath) {
        try {
            File file = new File(filePath);
            return objectMapper.readValue(file, new TypeReference<List<ProductRequest>>() {
            });
        } catch (IOException exception) {
            log.error("No file found" + exception.getMessage());
            Assertions.fail("Exception occurred while reading products from JSON file");
            return null;
        }

    }

    private ProductRequest getProductRequest() {
        return ProductRequest.builder().name("Test Phone").price(new BigDecimal("1300.00")).description("Test Product created").build();
    }
}
