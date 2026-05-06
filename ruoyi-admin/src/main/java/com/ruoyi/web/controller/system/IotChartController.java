package com.ruoyi.web.controller.system;

import java.util.List;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.IotDevice;
import com.ruoyi.system.domain.IotData;
import com.ruoyi.system.service.IIotDeviceService;
import com.ruoyi.system.service.IIotDataService;

/**
 * 波形显示Controller（真正的图表功能）
 */
@Controller
@RequestMapping("/system/iot/chart")
public class IotChartController extends BaseController {
    private String prefix = "system/iot/chart";

    @Autowired
    private IIotDeviceService deviceService;

    @Autowired
    private IIotDataService dataService;

    @RequiresPermissions("system:iot:chart:view")
    @GetMapping()
    public String chart(Long deviceId, ModelMap mmap) {
        if (deviceId != null) {
            IotDevice device = deviceService.selectIotDeviceByDeviceId(deviceId);
            mmap.put("device", device);
        }
        mmap.put("devices", deviceService.selectIotDeviceList(new IotDevice()));
        return "system/iot/chart";
    }

    /**
     * 获取设备图表数据
     */
    @RequiresPermissions("system:iot:chart:data")
    @GetMapping("/data/{deviceId}")
    @ResponseBody
    public AjaxResult getChartData(@PathVariable("deviceId") Long deviceId) {
        List<IotData> chartData = dataService.selectHistoryDataByDeviceId(deviceId, 100);
        return AjaxResult.success(chartData);
    }

    /**
     * 获取实时数据用于图表更新
     */
    @RequiresPermissions("system:iot:chart:data")
    @GetMapping("/realtime/{deviceId}")
    @ResponseBody
    public AjaxResult getRealtimeData(@PathVariable("deviceId") Long deviceId) {
        IotData latestData = dataService.selectLatestDataByDeviceId(deviceId);
        return AjaxResult.success(latestData);
    }
}
