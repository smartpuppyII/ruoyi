package com.ruoyi.web.controller.mobile;

import java.io.File;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.system.domain.VegetationAnalysis;
import com.ruoyi.system.service.IVegetationAnalysisService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 移动端植被分析API控制器
 *
 * @author ruoyi
 */
@Api(tags = "移动端植被分析管理")
@RestController
@RequestMapping("/mobile/api/vegetation")
public class MobileVegetationController extends BaseController
{
    @Autowired
    private IVegetationAnalysisService vegetationAnalysisService;

    @Value("${ruoyi.profile}")
    private String uploadPath;

    /**
     * 获取植被分析列表
     */
    @ApiOperation("获取植被分析列表")
    @GetMapping("/list")
    public TableDataInfo getAnalysisList(VegetationAnalysis vegetationAnalysis)
    {
        startPage();
        List<VegetationAnalysis> list = vegetationAnalysisService.selectVegetationAnalysisList(vegetationAnalysis);
        return getDataTable(list);
    }

    /**
     * 获取植被分析详情
     */
    @ApiOperation("获取植被分析详情")
    @GetMapping("/{id}")
    public AjaxResult getAnalysisInfo(
            @ApiParam(value = "分析ID", required = true)
            @PathVariable("id") Long id)
    {
        VegetationAnalysis analysis = vegetationAnalysisService.selectVegetationAnalysisById(id);
        if (analysis != null)
        {
            return AjaxResult.success(analysis);
        }
        return AjaxResult.error("分析记录不存在");
    }

    /**
     * 上传图片并进行植被分析
     */
    @ApiOperation("上传图片并分析")
    @Log(title = "移动端植被分析", businessType = BusinessType.INSERT)
    @PostMapping("/analyze")
    public AjaxResult analyzeImage(
            @ApiParam(value = "图片文件", required = true)
            @RequestParam("file") MultipartFile file,
            @ApiParam(value = "设备ID")
            @RequestParam(value = "deviceId", required = false) Long deviceId,
            @ApiParam(value = "备注")
            @RequestParam(value = "remark", required = false) String remark)
    {
        try
        {
            if (file.isEmpty())
            {
                return AjaxResult.error("上传文件不能为空");
            }

            // 验证文件类型
            String fileName = file.getOriginalFilename();
            String extension = fileName.substring(fileName.lastIndexOf("."));
            if (!extension.matches("\\.(jpg|jpeg|png|gif|bmp)$"))
            {
                return AjaxResult.error("只支持图片格式文件");
            }

            // 保存文件
            String uploadDir = uploadPath + "/vegetation/";
            File dir = new File(uploadDir);
            if (!dir.exists())
            {
                dir.mkdirs();
            }

            String newFileName = UUID.randomUUID().toString() + extension;
            String filePath = uploadDir + newFileName;
            File dest = new File(filePath);
            file.transferTo(dest);

            // 调用植被分析服务进行实际的图像处理
            VegetationAnalysis analysis = vegetationAnalysisService.analyzeVegetationCoverage(filePath, deviceId);

            if (analysis != null)
            {
                // 设置额外信息
                analysis.setImageName(fileName);
                analysis.setImagePath(filePath);
                if (remark != null && !remark.isEmpty())
                {
                    analysis.setRemark(remark);
                }

                // 更新分析记录
                vegetationAnalysisService.updateVegetationAnalysis(analysis);

                return AjaxResult.success("分析成功", analysis);
            }
            else
            {
                return AjaxResult.error("分析失败");
            }
        }
        catch (Exception e)
        {
            logger.error("植被分析失败", e);
            return AjaxResult.error("分析失败: " + e.getMessage());
        }
    }

    /**
     * 删除植被分析记录
     */
    @ApiOperation("删除植被分析记录")
    @Log(title = "移动端删除植被分析", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult deleteAnalysis(
            @ApiParam(value = "分析ID列表", required = true)
            @PathVariable String ids)
    {
        try
        {
            String[] idArray = ids.split(",");
            Long[] longIds = new Long[idArray.length];
            for (int i = 0; i < idArray.length; i++)
            {
                longIds[i] = Long.parseLong(idArray[i]);
            }
            return toAjax(vegetationAnalysisService.deleteVegetationAnalysisByIds(longIds));
        }
        catch (Exception e)
        {
            return AjaxResult.error("删除失败: " + e.getMessage());
        }
    }

    /**
     * 获取植被分析统计信息
     */
    @ApiOperation("获取植被分析统计信息")
    @GetMapping("/statistics")
    public AjaxResult getStatistics()
    {
        try
        {
            List<VegetationAnalysis> allAnalysis = vegetationAnalysisService.selectVegetationAnalysisList(new VegetationAnalysis());

            long totalCount = allAnalysis.size();

            // 计算平均覆盖率
            double avgCoverageRate = 0.0;
            if (totalCount > 0)
            {
                double sum = allAnalysis.stream()
                        .mapToDouble(a -> a.getCoverageRate() != null ? a.getCoverageRate() : 0.0)
                        .sum();
                avgCoverageRate = sum / totalCount;
            }

            // 获取最近一次分析
            VegetationAnalysis latestAnalysis = null;
            if (totalCount > 0)
            {
                latestAnalysis = allAnalysis.get(0);
            }

            return AjaxResult.success()
                    .put("total", totalCount)
                    .put("avgCoverageRate", String.format("%.2f", avgCoverageRate))
                    .put("latestAnalysis", latestAnalysis);
        }
        catch (Exception e)
        {
            return AjaxResult.error("查询统计信息失败: " + e.getMessage());
        }
    }
}
