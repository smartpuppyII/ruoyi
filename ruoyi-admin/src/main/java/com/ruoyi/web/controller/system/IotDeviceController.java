package com.ruoyi.web.controller.system;

import java.util.List;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.IotDevice;
import com.ruoyi.system.service.IIotDeviceService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.text.Convert;

/**
 * 物联网设备Controller
 */
@Controller
@RequestMapping("/system/iotdevice")
public class IotDeviceController extends BaseController {
    private String prefix = "system/iotdevice";

    @Autowired
    private IIotDeviceService iotDeviceService;

    @RequiresPermissions("system:iotdevice:view")
    @GetMapping()
    public String iotDevice() {
        return prefix + "/iotdevice";
    }

    /**
     * 查询物联网设备列表
     */
    @RequiresPermissions("system:iotdevice:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(IotDevice iotDevice) {
        startPage();
        List<IotDevice> list = iotDeviceService.selectIotDeviceList(iotDevice);
        return getDataTable(list);
    }

    /**
     * 获取物联网设备列表（GET请求，用于下拉菜单）
     */
    @GetMapping("/list")
    @ResponseBody
    public AjaxResult getDeviceList(IotDevice iotDevice) {
        try {
            List<IotDevice> list = iotDeviceService.selectIotDeviceList(iotDevice);
            return AjaxResult.success("查询成功", list);
        } catch (Exception e) {
            return AjaxResult.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 导出物联网设备列表
     */
    @RequiresPermissions("system:iotdevice:export")
    @Log(title = "物联网设备", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(IotDevice iotDevice) {
        List<IotDevice> list = iotDeviceService.selectIotDeviceList(iotDevice);
        ExcelUtil<IotDevice> util = new ExcelUtil<IotDevice>(IotDevice.class);
        return util.exportExcel(list, "物联网设备数据");
    }

    /**
     * 新增物联网设备
     */
    @GetMapping("/add")
    public String add() {
        return prefix + "/add";
    }

    /**
     * 新增保存物联网设备
     */
    @RequiresPermissions("system:iotdevice:add")
    @Log(title = "物联网设备", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(IotDevice iotDevice) {
        return toAjax(iotDeviceService.insertIotDevice(iotDevice));
    }

    /**
     * 修改物联网设备
     */
    @RequiresPermissions("system:iotdevice:edit")
    @GetMapping("/edit/{deviceId}")
    public String edit(@PathVariable("deviceId") Long deviceId, ModelMap mmap) {
        IotDevice iotDevice = iotDeviceService.selectIotDeviceByDeviceId(deviceId);
        mmap.put("iotDevice", iotDevice);
        return prefix + "/edit";
    }

    /**
     * 修改保存物联网设备
     */
    @RequiresPermissions("system:iotdevice:edit")
    @Log(title = "物联网设备", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(IotDevice iotDevice) {
        return toAjax(iotDeviceService.updateIotDevice(iotDevice));
    }

    /**
     * 删除物联网设备
     */
    @RequiresPermissions("system:iotdevice:remove")
    @Log(title = "物联网设备", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        return toAjax(iotDeviceService.deleteIotDeviceByDeviceIds(Convert.toLongArray(ids)));
    }

    /**
     * 获取设备详情（用于前端波形页）
     */
    @GetMapping("/{deviceId}")
    @ResponseBody
    public AjaxResult getInfo(@PathVariable("deviceId") Long deviceId) {
        try {
            IotDevice device = iotDeviceService.selectIotDeviceByDeviceId(deviceId);
            return AjaxResult.success(device);
        } catch (Exception e) {
            return AjaxResult.error("查询失败: " + e.getMessage());
        }
    }
}