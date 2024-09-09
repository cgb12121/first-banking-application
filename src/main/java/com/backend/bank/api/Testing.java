package com.backend.bank.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@CrossOrigin
@RequestMapping("/test")
public class Testing {

    @ResponseBody
    @GetMapping("/hi")
    public String test() {
        return "Hello World";
    }

    @ResponseBody
    @GetMapping("/hi/{username}")
    public String test(@PathVariable String username) {
        return "Hello " + username;
    }
}
