package com.yishuifengxiao.tool.personalkit.event;

import com.google.common.eventbus.Subscribe;
import com.yishuifengxiao.common.guava.EventPublisher;
import com.yishuifengxiao.common.jdbc.JdbcUtil;
import com.yishuifengxiao.common.tool.encoder.Md5;
import com.yishuifengxiao.common.tool.io.IoUtil;
import com.yishuifengxiao.common.tool.random.IdWorker;
import com.yishuifengxiao.tool.personalkit.domain.bo.FileAnalysisEvent;
import com.yishuifengxiao.tool.personalkit.domain.entity.DiskFile;
import com.yishuifengxiao.tool.personalkit.domain.entity.DiskUploadRecord;
import com.yishuifengxiao.tool.personalkit.domain.enums.UploadMode;
import com.yishuifengxiao.tool.personalkit.domain.enums.UploadStat;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/11/29 21:59
 * @since 1.0.0
 */
@Component
public class FileAnalysisEventListener {
    @Autowired
    private EventPublisher eventPublisher;

    @Subscribe
    public void onFileAnalysisEvent(FileAnalysisEvent fileAnalysisEvent) {
        List<CompletableFuture> futures = new ArrayList<>();
        File file = new File(fileAnalysisEvent.getFilePath());
        AtomicReference<DiskFile> reference = new AtomicReference<>();
        AtomicReference<Throwable> throwable = new AtomicReference<>();
        final String fileId = IdWorker.snowflakeStringId();
        futures.add(CompletableFuture.runAsync(() -> {

            try {
                DiskFile diskFile = new DiskFile(fileId, file.getName(), fileAnalysisEvent.getDiskFolder().getId(), fileAnalysisEvent.getSysUser().getId(), null, Md5.md5Short(file), IoUtil.suffix(file), null, file.getName(), fileAnalysisEvent.getUploadRecord().getId(), fileAnalysisEvent.getUploadMode().getCode(), LocalDateTime.now());
                reference.set(diskFile);
            } catch (Throwable e) {
                throwable.set(e);
            }


        }));
        if (UploadMode.ANALYSIS.equals(fileAnalysisEvent.getUploadMode())) {
            //需要解析
            futures.add(CompletableFuture.runAsync(() -> {
                try {

                } catch (Throwable e) {
                    throwable.set(e);
                }

            }));
        }
        // 等待任务完成
        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
        JdbcUtil.jdbcHelper().insertSelective(reference.get());
        UploadStat uploadStat = null == throwable.get() ? UploadStat.UPLOAD_FAIL : UploadStat.UPLOAD_SUCCESS;
        String msg = null == throwable.get() ? null : StringUtils.substring(throwable.get().getMessage(), 0, 255);
        DiskUploadRecord uploadRecord = fileAnalysisEvent.getUploadRecord().setStat(uploadStat.getCode()).setMsg(msg).setFinishTime(LocalDateTime.now());
        JdbcUtil.jdbcHelper().updateByPrimaryKeySelective(uploadRecord);
    }


    @PostConstruct
    public void init() {
        eventPublisher.eventBus().register(this);
    }
}