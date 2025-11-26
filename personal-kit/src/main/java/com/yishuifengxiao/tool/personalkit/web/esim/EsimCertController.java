package com.yishuifengxiao.tool.personalkit.web.esim;

import com.alibaba.excel.EasyExcel;
import com.yishuifengxiao.common.tool.bean.BeanUtil;
import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.common.tool.io.IoUtil;
import com.yishuifengxiao.common.tool.random.IdWorker;
import com.yishuifengxiao.tool.personalkit.aspect.Trim;
import com.yishuifengxiao.tool.personalkit.domain.bo.EsimCertExcel;
import com.yishuifengxiao.tool.personalkit.domain.entity.esim.EsimCert;
import com.yishuifengxiao.tool.personalkit.domain.request.IdReq;
import com.yishuifengxiao.tool.personalkit.service.esim.EsimCertService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2024/1/2 19:02
 * @since 1.0.0
 */
@Tag(name = "Esim证书管理")
@Valid
@RestController
@RequestMapping("/api/esim/cert")
@RequiredArgsConstructor
@Trim
@Slf4j
public class EsimCertController {

    @Autowired
    private EsimCertService esimCertService;

    @PostMapping("/page")
    @ResponseBody
    public Page<EsimCert> findPage(@RequestBody PageQuery<EsimCert> pageQuery) {
        return esimCertService.findPage(pageQuery);
    }

    @PostMapping(value = "/upload")
    @ResponseBody
    public void upload(@RequestPart("file") MultipartFile file) throws IOException {


        // 创建保存目录（如果不存在）
        File uploadDir = new File(IdWorker.snowflakeStringId());
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        // 保存文件
        File savedFile = new File(uploadDir, file.getOriginalFilename());

        try {
            IoUtil.copy(file.getInputStream(), new FileOutputStream(savedFile));
            // 解析Excel文件
            List<EsimCertExcel> list = EasyExcel.read(savedFile).head(EsimCertExcel.class).sheet().doReadSync();
            if (!CollectionUtils.isEmpty(list)) {
                for (EsimCertExcel cert : list) {
                    try {
                        EsimCert esimCert = BeanUtil.copy(cert, new EsimCert());
                        esimCertService.save(esimCert);
                    } catch (Exception e) {
                        log.warn("---------> 获取文件 ={} 的失败 ，失败的原因为 {}", cert, e.getMessage());
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            FileUtils.forceDelete(uploadDir);
        }

    }

    @PostMapping(value = "/save", produces = {"application/json;charset=utf-8"})
    @ResponseBody
    public EsimCert save(@RequestBody EsimCert param) {
        return esimCertService.save(param);
    }

    @PostMapping("/update")
    @ResponseBody
    public void update(@RequestBody EsimCert param) {
        esimCertService.update(param);
    }

    @PostMapping("/delete")
    @ResponseBody
    public void delete(@Valid @RequestBody IdReq param) {
        esimCertService.deleteById(Long.parseLong(param.getId()));
    }

    @PostMapping("/detail")
    @ResponseBody
    public Object detail(@Valid @RequestBody IdReq param) {
        return esimCertService.findById(Long.parseLong(param.getId()));
    }
}
