package com.anket;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Root Controller - Main package'da test controller
 */
@RestController
public class RootController {

    @GetMapping("/api/root/test")
    public String test() {
        return "RootController is working! 🎉";
    }
    
    @GetMapping("/test")
    public String simpleTest() {
        return "Simple test working!";
    }
}

