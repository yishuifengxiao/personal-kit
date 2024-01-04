package com.yishuifengxiao.tool.personalkit.domain.mongo;

import com.yishuifengxiao.tool.personalkit.domain.enums.ScriptType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/12/2 12:53
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class RuntimeScript implements Serializable {


    @Id
    private String id;


    private ScriptType scriptType;


    private String content;
}
