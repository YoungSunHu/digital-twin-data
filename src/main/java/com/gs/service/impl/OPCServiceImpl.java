package com.gs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gs.context.OPCServerContext;
import com.gs.dao.entity.ConfigParamEntity;
import com.gs.dao.entity.OPCItemValueRecordEntity;
import com.gs.dao.mapper.ConfigParamMapper;
import com.gs.dao.mapper.OPCItemValueRecordMapper;
import com.gs.service.OPCService;
import lombok.extern.slf4j.Slf4j;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.JIVariant;
import org.openscada.opc.lib.common.NotConnectedException;
import org.openscada.opc.lib.da.AddFailedException;
import org.openscada.opc.lib.da.DuplicateGroupException;
import org.openscada.opc.lib.da.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.List;

/**
 * @author ：YoungSun
 * @date ：Created in 2021/1/14 9:03
 * @modified By：
 */
@Service
@Slf4j
public class OPCServiceImpl implements OPCService {

    @Autowired
    OPCServerContext opcServerContext;

    @Resource
    OPCItemValueRecordMapper opcItemValueRecordMapper;

    @Resource
    ConfigParamMapper configParamMapper;

    @Override
    public void dataFromServer() throws DuplicateGroupException, NotConnectedException, JIException, UnknownHostException, AddFailedException, InterruptedException {
        ConfigParamEntity configParamEntity = configParamMapper.selectOne(new QueryWrapper<ConfigParamEntity>().eq("param_type", "factory_config").eq("param_key", "factory_id"));
        List<Item> items = opcServerContext.getItems();
        if (opcServerContext.isConnected()) {
            //点位读取测试
            try {
                items.get(0).read(true).getValue();
            } catch (JIException e) {
                e.printStackTrace();
                //重连opcserver
                opcServerContext.reconnect();
                items = opcServerContext.getItems();
            }
            for (Item item : items) {
                JIVariant value = null;
                value = item.read(true).getValue();
                Calendar timestamp = item.read(true).getTimestamp();
                String valueStr = opcType2Str(value);
                OPCItemValueRecordEntity opcItemValueRecordEntity = new OPCItemValueRecordEntity();
                opcItemValueRecordEntity.setItemId(item.getId());
                opcItemValueRecordEntity.setItemType(value.getType());
                opcItemValueRecordEntity.setItemValue(valueStr);
                opcItemValueRecordEntity.setIsSuccess(false);
                opcItemValueRecordEntity.setFactoryId(configParamEntity.getParamValue());
                opcItemValueRecordEntity.setItemTimestamp(timestamp.getTime());
                opcItemValueRecordMapper.insert(opcItemValueRecordEntity);
            }
        }
    }

    private static String opcType2Str(JIVariant value) throws JIException {
        int opcType = value.getType();
        switch (opcType) {
            case JIVariant.VT_I2:
                return String.valueOf(value.getObjectAsShort());
            case JIVariant.VT_I4:
                return String.valueOf(value.getObjectAsLong());
            case JIVariant.VT_R4:
                return String.valueOf(value.getObjectAsFloat());
            case JIVariant.VT_BSTR:
                return value.getObjectAsString2();
        }
        return null;
    }

}
