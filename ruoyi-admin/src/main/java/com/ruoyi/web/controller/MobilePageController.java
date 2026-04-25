package com.ruoyi.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 移动端页面控制器
 * 
 * @author ruoyi
 */
@Controller
@RequestMapping("/m")
public class MobilePageController
{
    /**
     * 登录页面
     */
    @GetMapping("/login")
    public String login()
    {
        return "mobile/login";
    }

    /**
     * 首页
     */
    @GetMapping({"/", "/index"})
    public String index(ModelMap mmap)
    {
        return "mobile/index";
    }

    /**
     * 设备列表
     */
    @GetMapping("/device/list")
    public String deviceList()
    {
        return "mobile/device/list";
    }

    /**
     * 设备详情
     */
    @GetMapping("/device/detail")
    public String deviceDetail()
    {
        return "mobile/device/detail";
    }

    /**
     * 植被分析列表
     */
    @GetMapping("/vegetation/list")
    public String vegetationList()
    {
        return "mobile/vegetation/list";
    }

    /**
     * 植被分析 (拍照上传)
     */
    @GetMapping("/vegetation/analyze")
    public String vegetationAnalyze()
    {
        return "mobile/vegetation/analyze";
    }

    /**
     * 个人中心
     */
    @GetMapping("/profile")
    public String profile()
    {
        return "mobile/profile";
    }
}












