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
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.IotDevice;
import com.ruoyi.system.domain.IotData;
import com.ruoyi.system.service.IIotDeviceService;
import com.ruoyi.system.service.IIotDataService;

/**
 * 波形显示Controller
 */
@Controller
@RequestMapping("/system/iot/waveform")
public class IotWaveformController extends BaseController {
    private String prefix = "system/iot/waveform";

    @Autowired
    private IIotDeviceService deviceService;

    @Autowired
    private IIotDataService dataService;

    @RequiresPermissions("system:iot:waveform:view")
    @GetMapping()
    public String waveform(Long deviceId, ModelMap mmap) {
        if (deviceId != null) {
            IotDevice device = deviceService.selectIotDeviceByDeviceId(deviceId);
            mmap.put("device", device);
        }
        mmap.put("devices", deviceService.selectIotDeviceList(new IotDevice()));
        return "system/iot/waveform";
    }

    /**
     * 获取设备最新数据
     */
    @RequiresPermissions("system:iot:waveform:data")
    @GetMapping("/latest/{deviceId}")
    @ResponseBody
    public AjaxResult getLatestData(@PathVariable("deviceId") Long deviceId) {
        return AjaxResult.success(dataService.selectLatestDataByDeviceId(deviceId));
    }

    /**
     * 获取设备历史数据
     */
    @RequiresPermissions("system:iot:waveform:data")
    @GetMapping("/history/{deviceId}")
    @ResponseBody
    public AjaxResult getHistoryData(@PathVariable("deviceId") Long deviceId) {
        List<IotData> historyData = dataService.selectHistoryDataByDeviceId(deviceId, 50);
        return AjaxResult.success(historyData);
    }
}