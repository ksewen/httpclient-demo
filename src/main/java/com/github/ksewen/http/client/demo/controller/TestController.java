package com.github.ksewen.http.client.demo.controller;
/**
 * @author ksewen
 * @date 19.09.2023 15:38
 */

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/ten")
    public String tenSecond() throws InterruptedException {
        Thread.sleep(10000);
        return "OK";
    }

    @GetMapping("")
    public String success()  {
        return "OK";
    }
}
