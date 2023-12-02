package com.yishuifengxiao.tool.personalkit.web;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/12/2 13:01
 * @since 1.0.0
 */
@Api(value = "数据中心", tags = {"数据中心"})
@Controller
@Valid
@Validated
@Slf4j
@RequestMapping("/data")
public class DataController {
}
