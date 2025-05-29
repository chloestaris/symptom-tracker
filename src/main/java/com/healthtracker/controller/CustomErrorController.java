package com.healthtracker.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class CustomErrorController implements ErrorController {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomErrorController.class);

    @RequestMapping("/error")
    public ResponseEntity<Map<String, Object>> handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        Object trace = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION_TYPE);
        
        logger.error("Error occurred: status={}, message={}, exception={}", status, message, exception);
        if (exception != null) {
            logger.error("Exception details:", (Throwable) exception);
        }

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", new Date());
        errorDetails.put("status", status != null ? status : 500);
        errorDetails.put("error", exception != null ? exception.toString() : "Unknown error");
        errorDetails.put("message", message != null ? message : "An unexpected error occurred");
        errorDetails.put("trace", trace != null ? trace.toString() : "No trace available");
        errorDetails.put("path", request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI));

        return ResponseEntity
            .status(status != null ? (Integer) status : 500)
            .body(errorDetails);
    }
} 