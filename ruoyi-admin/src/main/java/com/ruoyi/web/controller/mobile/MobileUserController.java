package com.ruoyi.web.controller.mobile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.system.service.ISysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 移动端用户管理
 *
 * @author ruoyi
 */
@Api(tags = "移动端用户管理")
@RestController
@RequestMapping("/mobile/api/user")
public class MobileUserController extends BaseController
{
    @Autowired
    private ISysUserService userService;

    /**
     * 获取当前用户信息
     */
    @ApiOperation("获取当前用户信息")
    @GetMapping("/info")
    public AjaxResult getUserInfo()
    {
        try
        {
            SysUser user = getSysUser();
            if (user == null)
            {
                return AjaxResult.error("未登录或登录已过期");
            }

            // 查询完整用户信息
            SysUser userInfo = userService.selectUserById(user.getUserId());

            if (userInfo != null)
            {
                // 清除敏感信息
                userInfo.setPassword(null);
                userInfo.setSalt(null);

                return AjaxResult.success().put("user", userInfo);
            }
            else
            {
                return AjaxResult.error("用户信息不存在");
            }
        }
        catch (Exception e)
        {
            logger.error("获取用户信息失败", e);
            return AjaxResult.error("获取用户信息失败");
        }
    }
}
