package com.example.my_chat_app;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // This annotation marks the class as a Spring MVC Controller
public class ChatController {

    @GetMapping("/chat") // This maps HTTP GET requests to the /chat URL to this method
    public String chatPage() {
        // Spring Boot, with Thymeleaf, will look for a template named "chat.html"
        // in src/main/resources/templates/
        return "chat";
    }

    // You can also add a redirect for the root URL
    @GetMapping("/")
    public String redirectToChat() {
        return "redirect:/chat";
    }
}
