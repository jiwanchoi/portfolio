package com.api.web.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ErrorHandlerController implements ErrorController {
    private static final String ERROR_MAPPING = "/error";

    @RequestMapping(value = ERROR_MAPPING)
    public ResponseEntity<Map>  handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        HttpStatus httpStatus = HttpStatus.valueOf(Integer.valueOf(status.toString()));
        
        Map<String, Object> resMap = new HashMap<>();
		resMap.put("code", "error");
    	resMap.put("message", "something is invalid");
        
        model.addAttribute("code", status.toString());
        model.addAttribute("msg", httpStatus.getReasonPhrase());
        model.addAttribute("timestamp", new Date());
        return ResponseEntity.ok().body(resMap);
    }    

    @Override
    public String getErrorPath() {
        return ERROR_MAPPING;
    }
}