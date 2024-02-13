package com.wyq.project_springboot.controller;

import com.alipay.api.AlipayApiException;
import com.wyq.project_springboot.service.ShopService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/alipay")
public class AliPayController {
    @Autowired
    private ShopService shopService;

    @GetMapping("/payOrder")
    public void payOrder(String orderNo, HttpServletResponse httpServletResponse) throws IOException {
        shopService.payOrder(orderNo,httpServletResponse);
    }

    @PostMapping("/payNotify")
    public void payNotify(HttpServletRequest httpServletRequest) throws AlipayApiException {
        shopService.payNotify(httpServletRequest);
    }
}
