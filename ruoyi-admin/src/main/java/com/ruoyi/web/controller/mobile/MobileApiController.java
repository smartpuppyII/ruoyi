package com.ruoyi.web.controller.mobile;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.framework.shiro.service.SysLoginService;
import com.ruoyi.common.utils.ShiroUtils;
import com.ruoyi.common.constant.ShiroConstants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 移动端API控制器
 *
 * @author ruoyi
 */
@Api(tags = "移动端API")
@RestController
@RequestMapping("/mobile/api")
public class MobileApiController extends BaseController
{
    @Autowired
    private SysLoginService loginService;

    /**
     * 移动端登录
     */
    @ApiOperation("移动端登录")
    @PostMapping("/login")
    public AjaxResult login(@RequestBody LoginRequest loginRequest, HttpServletRequest request)
    {
        AjaxResult ajax = AjaxResult.success();
        try
        {
            // 移动端跳过验证码验证
            // 确保不是CAPTCHA_ERROR，这样就不会触发验证码错误
            request.removeAttribute(ShiroConstants.CURRENT_CAPTCHA);

            // 执行登录
            SysUser user = loginService.login(loginRequest.getUsername(), loginRequest.getPassword());

            // 生成会话ID作为token
            String token = (String) ShiroUtils.getSession().getId();
            ajax.put("token", token);
            ajax.put("user", user);
        }
        catch (Exception e)
        {
            return AjaxResult.error(e.getMessage());
        }
        return ajax;
    }

    /**
     * 获取用户信息
     */
    @ApiOperation("获取用户信息")
    @GetMapping("/getInfo")
    public AjaxResult getInfo()
    {
        SysUser user = ShiroUtils.getSysUser();
        AjaxResult ajax = AjaxResult.success();
        ajax.put("user", user);
        ajax.put("roles", user.getRoles());
        return ajax;
    }

    /**
     * 移动端路由信息
     */
    @ApiOperation("获取路由信息")
    @GetMapping("/getRouters")
    public AjaxResult getRouters()
    {
        // 移动端简化路由，只返回必要的菜单信息
        AjaxResult ajax = AjaxResult.success();
        ajax.put("data", "移动端路由信息");
        return ajax;
    }

    /**
     * 登出
     */
    @ApiOperation("登出")
    @Log(title = "登出", businessType = BusinessType.OTHER)
    @PostMapping("/logout")
    public AjaxResult logout()
    {
        ShiroUtils.logout();
        return AjaxResult.success();
    }

    /**
     * 登录请求对象
     */
    public static class LoginRequest
    {
        private String username;
        private String password;
        private String code;
        private String uuid;

        public String getUsername()
        {
            return username;
        }

        public void setUsername(String username)
        {
            this.username = username;
        }

        public String getPassword()
        {
            return password;
        }

        public void setPassword(String password)
        {
            this.password = password;
        }

        public String getCode()
        {
            return code;
        }

        public void setCode(String code)
        {
            this.code = code;
        }

        public String getUuid()
        {
            return uuid;
        }

        public void setUuid(String uuid)
        {
            this.uuid = uuid;
        }
    }
}
