package com.yishuifengxiao.tool.personalkit.service;

import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/12/3 18:36
 * @since 1.0.0
 */
@Component
@Transactional(rollbackOn = {Exception.class})
public class RecordService {


}
