package com.fedorizvekov.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
@TestMethodOrder(OrderAnnotation.class)
public class EnumControllerTest {

    @InjectMocks
    private EnumController enumController;

    @Autowired
    private MockMvc mockMvc;


    @DisplayName("Should invoke addEnum and return OK")
    @Test
    @Order(1)
    void should_invoke_addEnum_and_return_OK() throws Exception {
        mockMvc.perform(put("/enums/{newEnum}", "test_enum"))
                .andDo(print())
                .andExpect(status().isOk());
    }


    @DisplayName("Should return OK and DynamicEnum values as string")
    @Test
    @Order(2)
    void should_return_OK_and_DynamicEnum_values_as_string() throws Exception {
        mockMvc.perform(get("/enums"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("[FIRST_ENUM, TEST_ENUM, SECOND_ENUM]"));
    }

}
