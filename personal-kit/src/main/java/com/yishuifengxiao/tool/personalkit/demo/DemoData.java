package com.yishuifengxiao.tool.personalkit.demo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.yishuifengxiao.tool.personalkit.support.jackson.DynamicAddressSerializer;
import com.yishuifengxiao.tool.personalkit.support.jackson.DynamicSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DemoData implements Serializable {

    private String id;

    @DynamicSerialize(DemoEnum.class)
    @JsonSerialize(using = DynamicAddressSerializer.class)
    private Integer demoEnum;
}
