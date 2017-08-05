package com.fedorizvekov.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EnumControllerTest {

    @InjectMocks
    private EnumController enumController;

    @Autowired
    private MockMvc mockMvc;


    @Test
    public void should_invoke_addEnum_and_return_OK() throws Exception {
        mockMvc.perform(put("/enums/{newEnum}", "test_enum"))
                .andDo(print())
                .andExpect(status().isOk());
    }


    @Test
    public void should_return_OK_and_DynamicEnum_values_as_string() throws Exception {
        mockMvc.perform(get("/enums"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("[FIRST_ENUM, SECOND_ENUM, TEST_ENUM]"));
    }

}
