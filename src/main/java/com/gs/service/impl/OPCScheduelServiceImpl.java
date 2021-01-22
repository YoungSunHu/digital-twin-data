package com.gs.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gs.dao.entity.OPCItemValueRecordEntity;
import com.gs.dao.mapper.OPCItemValueRecordMapper;
import com.gs.service.OPCScheduelService;
import com.gs.service.OPCService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author ：YoungSun
 * @date ：Created in 2021/1/18 11:48
 * @modified By：
 */
@Service
@Slf4j
public class OPCScheduelServiceImpl implements OPCScheduelService {

    @Resource
    OPCItemValueRecordMapper opcItemValueRecordMapper;

    @Autowired
    OPCService opcService;

    @Value("${gs.datauploadurl}")
    String uploadUrl;

    @Override
    @Scheduled(cron = "*/30 * * * * ?")
    public void itemDataUpload() {
        //过去7天未成功上传的数据,一次最多处理1000条,30s执行一次
        IPage<OPCItemValueRecordEntity> page = opcItemValueRecordMapper.selectPage(new Page<>(1, 1000), new QueryWrapper<OPCItemValueRecordEntity>().eq("is_success", false).between("create_time", new Date(System.currentTimeMillis() - 604800000), new Date()));
        for (OPCItemValueRecordEntity opcItemValueRecordEntity : page.getRecords()) {
            this.upload(opcItemValueRecordEntity);
        }
        //未发送数据保存90天
        opcItemValueRecordMapper.delete(new QueryWrapper<OPCItemValueRecordEntity>().eq("is_success", false).lt("create_time", new Date(System.currentTimeMillis() - 7776000000L)));
        //已发送数据保存7天
        opcItemValueRecordMapper.delete(new QueryWrapper<OPCItemValueRecordEntity>().eq("is_success", true).lt("create_time", new Date(System.currentTimeMillis() - 604800000L)));
    }

    @Override
    @Scheduled(cron = "0 0/1 * * * ?")
    public void itemDataGrap() {
        try {
            opcService.dataFromServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Async
    void upload(OPCItemValueRecordEntity entity) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.SECONDS)
                .writeTimeout(3, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
                .build();
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"), JSONObject.toJSONString(entity));
        Request request = new Request.Builder().url(uploadUrl).post(body).build();
        Response execute = null;
        try {
            execute = okHttpClient.newCall(request).execute();
            String string = execute.body().string();
            JSONObject jsonObject = JSON.parseObject(string);
            if (jsonObject.getInteger("code") == 0) {
                entity.setIsSuccess(true);
                opcItemValueRecordMapper.updateById(entity);
            } else {
                log.error("点位数据:{}上传失败,原因:{}", entity.toString(), jsonObject.getString("msg"));
            }
        } catch (IOException e) {
            log.error("上传接口调用失败:{}", e.getMessage());
        }
    }
}
