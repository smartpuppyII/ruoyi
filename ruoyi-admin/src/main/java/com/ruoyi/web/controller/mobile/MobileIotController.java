package com.ruoyi.web.controller.mobile;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.IotDevice;
import com.ruoyi.system.domain.IotData;
import com.ruoyi.system.service.IIotDeviceService;
import com.ruoyi.system.service.IIotDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 移动端IoT设备API控制器
 *
 * @author ruoyi
 */
@Api(tags = "移动端IoT设备管理")
@RestController
@RequestMapping("/mobile/api/iot")
public class MobileIotController extends BaseController
{
    @Autowired
    private IIotDeviceService iotDeviceService;

    @Autowired
    private IIotDataService iotDataService;

    /**
     * 获取设备列表
     */
    @ApiOperation("获取设备列表")
    @GetMapping("/devices")
    public TableDataInfo getDeviceList(IotDevice iotDevice)
    {
        startPage();
        List<IotDevice> list = iotDeviceService.selectIotDeviceList(iotDevice);
        return getDataTable(list);
    }

    /**
     * 获取设备详情（包含最新传感器数据）
     */
    @ApiOperation("获取设备详情")
    @GetMapping("/device/{deviceId}")
    public AjaxResult getDeviceInfo(
            @ApiParam(value = "设备ID", required = true)
            @PathVariable("deviceId") Long deviceId)
    {
        IotDevice device = iotDeviceService.selectIotDeviceByDeviceId(deviceId);
        if (device != null)
        {
            // 获取最新的传感器数据
            IotData latestData = iotDataService.selectLatestDataByDeviceId(deviceId);

            AjaxResult result = AjaxResult.success(device);
            result.put("latestData", latestData);
            return result;
        }
        return AjaxResult.error("设备不存在");
    }

    /**
     * 添加设备
     */
    @ApiOperation("添加设备")
    @Log(title = "移动端添加设备", businessType = BusinessType.INSERT)
    @PostMapping("/device")
    public AjaxResult addDevice(@RequestBody IotDevice iotDevice)
    {
        try
        {
            return toAjax(iotDeviceService.insertIotDevice(iotDevice));
        }
        catch (Exception e)
        {
            return AjaxResult.error("添加设备失败: " + e.getMessage());
        }
    }

    /**
     * 更新设备
     */
    @ApiOperation("更新设备")
    @Log(title = "移动端更新设备", businessType = BusinessType.UPDATE)
    @PutMapping("/device")
    public AjaxResult updateDevice(@RequestBody IotDevice iotDevice)
    {
        try
        {
            return toAjax(iotDeviceService.updateIotDevice(iotDevice));
        }
        catch (Exception e)
        {
            return AjaxResult.error("更新设备失败: " + e.getMessage());
        }
    }

    /**
     * 删除设备
     */
    @ApiOperation("删除设备")
    @Log(title = "移动端删除设备", businessType = BusinessType.DELETE)
    @DeleteMapping("/device/{deviceIds}")
    public AjaxResult deleteDevice(
            @ApiParam(value = "设备ID列表", required = true)
            @PathVariable String deviceIds)
    {
        try
        {
            String[] idArray = deviceIds.split(",");
            Long[] longIds = new Long[idArray.length];
            for (int i = 0; i < idArray.length; i++)
            {
                longIds[i] = Long.parseLong(idArray[i]);
            }
            return toAjax(iotDeviceService.deleteIotDeviceByDeviceIds(longIds));
        }
        catch (Exception e)
        {
            return AjaxResult.error("删除设备失败: " + e.getMessage());
        }
    }

    /**
     * 获取在线设备数量
     */
    @ApiOperation("获取在线设备数量")
    @GetMapping("/devices/online/count")
    public AjaxResult getOnlineDeviceCount()
    {
        try
        {
            IotDevice query = new IotDevice();
            query.setStatus("1"); // 假设1表示在线
            List<IotDevice> list = iotDeviceService.selectIotDeviceList(query);
            return AjaxResult.success("在线设备数", list.size());
        }
        catch (Exception e)
        {
            return AjaxResult.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取设备统计信息
     */
    @ApiOperation("获取设备统计信息")
    @GetMapping("/devices/statistics")
    public AjaxResult getDeviceStatistics()
    {
        try
        {
            List<IotDevice> allDevices = iotDeviceService.selectIotDeviceList(new IotDevice());

            long totalCount = allDevices.size();
            long onlineCount = allDevices.stream().filter(d -> "1".equals(d.getStatus())).count();
            long offlineCount = totalCount - onlineCount;

            return AjaxResult.success()
                    .put("total", totalCount)
                    .put("online", onlineCount)
                    .put("offline", offlineCount);
        }
        catch (Exception e)
        {
            return AjaxResult.error("查询统计信息失败: " + e.getMessage());
        }
    }
}
