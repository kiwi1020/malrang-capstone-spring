package com.malrang.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ErrorHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
    }

    @DisplayName("Handle 404 Error")
    @Test
    void testHandleErrorNotFound() throws Exception {
        // Simulate a 404 error
        request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, HttpStatus.NOT_FOUND.value());

        mockMvc.perform(request(HttpMethod.valueOf("GET"), "/error").servletPath("/error").requestAttr(RequestDispatcher.ERROR_STATUS_CODE, HttpStatus.NOT_FOUND.value()))
                .andExpect(status().isOk()) // 200 OK
                .andExpect(view().name("errorPage")); // "errorPage" 뷰 리턴 확인
    }

    @DisplayName("Handle 500 Error")
    @Test
    void testHandleErrorInternalServerError() throws Exception {
        // Simulate a 500 error
        request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, HttpStatus.INTERNAL_SERVER_ERROR.value());

        mockMvc.perform(request(HttpMethod.valueOf("GET"), "/error").servletPath("/error").requestAttr(RequestDispatcher.ERROR_STATUS_CODE, HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .andExpect(status().isOk()) // 200 OK
                .andExpect(view().name("errorPage")); // "errorPage" 뷰 리턴 확인
    }

    @DisplayName("Handle Unknown Error")
    @Test
    void testHandleErrorOther() throws Exception {
        // Simulate an unknown error
        request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, 418); // 예를 들어 418 I'm a teapot

        mockMvc.perform(request(HttpMethod.valueOf("GET"), "/error").servletPath("/error").requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 418))
                .andExpect(status().isOk()) // 200 OK
                .andExpect(view().name("errorPage")); // "errorPage" 뷰 리턴 확인
    }
}
