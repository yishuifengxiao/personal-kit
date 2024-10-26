package com.yishuifengxiao.tool.personalkit.event;

import com.google.common.eventbus.Subscribe;
import com.yishuifengxiao.common.guava.EventPublisher;
import com.yishuifengxiao.common.jdbc.JdbcUtil;
import com.yishuifengxiao.common.tool.codec.Md5;
import com.yishuifengxiao.common.tool.io.IoUtil;
import com.yishuifengxiao.common.tool.random.IdWorker;
import com.yishuifengxiao.tool.personalkit.domain.bo.FileAnalysisEvent;
import com.yishuifengxiao.tool.personalkit.domain.entity.DiskFile;
import com.yishuifengxiao.tool.personalkit.domain.entity.DiskUploadRecord;
import com.yishuifengxiao.tool.personalkit.domain.enums.SupportedSuffix;
import com.yishuifengxiao.tool.personalkit.domain.enums.UploadMode;
import com.yishuifengxiao.tool.personalkit.domain.enums.UploadStat;
import com.yishuifengxiao.tool.personalkit.domain.mongo.VirtuallyFile;
import com.yishuifengxiao.tool.personalkit.domain.mongo.VirtuallyRow;
import com.yishuifengxiao.tool.personalkit.helper.data.FileParserHelper;
import com.yishuifengxiao.tool.personalkit.helper.data.ParserResult;
import com.yishuifengxiao.tool.personalkit.support.UploadClient;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

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
    @Autowired
    private UploadClient uploadClient;

    @Autowired
    private MongoTemplate mongotemplate;

    @Subscribe
    public void onFileAnalysisEvent(FileAnalysisEvent fileAnalysisEvent) {
        //@formatter:off
        List<CompletableFuture> futures = new ArrayList<>();
        File file = new File(fileAnalysisEvent.getFilePath());
        AtomicReference<DiskFile> reference = new AtomicReference<>();
        AtomicReference<Throwable> throwable = new AtomicReference<>();
        final String fileId = IdWorker.snowflakeStringId();
        futures.add(CompletableFuture.runAsync(() -> {

            try {
                String objectName = uploadClient.upload(fileAnalysisEvent.getSysUser(), file);
                DiskFile diskFile = new DiskFile(
                        fileId, file.getName(), fileAnalysisEvent.getDiskFolder().getId(),
                        fileAnalysisEvent.getSysUser().getId(), objectName, Md5.md5Short(file),
                        IoUtil.suffix(file), null, file.getName(), fileAnalysisEvent.getUploadRecord().getId(),
                        file.length(), fileAnalysisEvent.getUploadMode().getCode(), LocalDateTime.now());
                reference.set(diskFile);

                if (UploadMode.ANALYSIS.equals(fileAnalysisEvent.getUploadMode())) {
                    //需要解析
                    if (!SupportedSuffix.of(IoUtil.suffix(file)).isPresent()) {
                        return;
                    }
                    List<ParserResult> parserResults = FileParserHelper.parse(file);
                    for (ParserResult parserResult : parserResults) {

                        VirtuallyFile virtuallyFile = mongotemplate.save(new VirtuallyFile(
                                IdWorker.snowflakeStringId(), diskFile.getId(), fileAnalysisEvent.getSysUser().getId(), parserResult.getSheetName(), parserResult.getHeaders()));

                        parserResult.getRows().stream().map(s -> new VirtuallyRow(
                                s.getRowIndex(), s.getCells(), IdWorker.snowflakeStringId(), virtuallyFile.getFileId(),
                                fileAnalysisEvent.getSysUser().getId(), virtuallyFile.getId(),
                                s.getCells().stream().allMatch(v -> BooleanUtils.isNotFalse(v.getIsNormal())))
                                ).forEach(mongotemplate::save);

                    }
                }

            } catch (Throwable e) {
                throwable.set(e);
            }


        }));

        // 等待任务完成
        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
        DiskFile diskFile = reference.get();
        if (null != diskFile) {
            JdbcUtil.jdbcHelper().insertSelective(diskFile);
        }
        if (null != file) {
            file.delete();
        }
        UploadStat uploadStat = null == throwable.get() ? UploadStat.UPLOAD_SUCCESS : UploadStat.UPLOAD_FAIL;
        String msg = null == throwable.get() ? null : StringUtils.substring(throwable.get().getMessage(), 0, 255);
        DiskUploadRecord uploadRecord = fileAnalysisEvent.getUploadRecord().setStat(uploadStat.getCode()).setMsg(msg).setFinishTime(LocalDateTime.now());
        JdbcUtil.jdbcHelper().updateByPrimaryKeySelective(uploadRecord);
        //@formatter:on
    }


    @PostConstruct
    public void init() {
        eventPublisher.eventBus().register(this);
    }
}
