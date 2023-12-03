package com.yishuifengxiao.tool.personalkit.web;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/12/3 18:35
 * @since 1.0.0
 */
@Api(tags = {"记录接口"})
@Valid
@RestController
@RequestMapping("/record")
@RequiredArgsConstructor
public class RecordController {
}
