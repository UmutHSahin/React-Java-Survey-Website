package com.anket.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Test Controller - Basit test endpoint'i
 * Controller'ların çalışıp çalışmadığını test etmek için
 */
@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello from TestController! 🎉";
    }

    @GetMapping("/status")
    public String status() {
        return "Backend is running! ✅";
    }
}

