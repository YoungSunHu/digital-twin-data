package com.gs.service;

import java.io.IOException;

public interface OPCScheduelService {

    /**
     * 点位数据上传
     *
     * @throws IOException
     */
    void itemDataUpload() throws IOException;

    /**
     * 从OPCServer采集数据
     */
    void itemDataGrap();
}
