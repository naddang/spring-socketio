package com.dev_cbj.springsocketio.web.controller;

import com.dev_cbj.springsocketio.socket.handler.EngineIoHandler;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@AllArgsConstructor
public class WebController {

    private final EngineIoHandler engineIoHandler;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @ResponseBody
    @GetMapping("/private_message")
    public ResponseEntity<String> privateMessage() {
        return ResponseEntity.ok("OK");
    }
}
