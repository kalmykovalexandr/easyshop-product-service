package com.easyshop.product.web;

import com.easyshop.product.config.ProductSecurityConfig;
import com.easyshop.product.service.ProductService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProductController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ProductSecurityConfig.class))
@Disabled
class ProductControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ProductService service;

    @Test
    void healthEndpointWorks() throws Exception {
        mvc.perform(get("/healthz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ok").value(true));
    }

    @Test
    void readyEndpointWorks() throws Exception {
        mvc.perform(get("/readyz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ok").value(true));
    }
  
    @Test
    void reserveProductSuccessfully() throws Exception {
        when(service.reserve(1L, 2)).thenReturn(ProductService.ReserveResult.OK);

        mvc.perform(post("/api/admin/products/1/reserve").param("qty", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ok").value(true));
    }

    @Test
    void reserveProductNotFound() throws Exception {
        when(service.reserve(1L, 2)).thenReturn(ProductService.ReserveResult.NOT_FOUND);

        mvc.perform(post("/api/admin/products/1/reserve").param("qty", "2"))
                .andExpect(status().isNotFound());
    }

    @Test
    void reserveProductNotEnoughStock() throws Exception {
        when(service.reserve(1L, 2)).thenReturn(ProductService.ReserveResult.NOT_ENOUGH_STOCK);

        mvc.perform(post("/api/admin/products/1/reserve").param("qty", "2"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.ok").value(false))
                .andExpect(jsonPath("$.message").value("Not enough stock"));
    }
}
