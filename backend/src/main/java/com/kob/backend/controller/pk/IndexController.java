package com.kob.backend.controller.pk;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
/**
 * 这个controller下所有链接都是在pk目录下
 * 127.0.0.1：3000/pk/
 * 用父目录requestmapping
 */
//@RequestMapping("/pk/")
public class IndexController {

    @RequestMapping("/")
    public String index() {
        return "pk/index.html";
    }
}
