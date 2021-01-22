package com.gs.service;

import org.jinterop.dcom.common.JIException;
import org.openscada.opc.lib.common.NotConnectedException;
import org.openscada.opc.lib.da.AddFailedException;
import org.openscada.opc.lib.da.DuplicateGroupException;

import java.net.UnknownHostException;

/**
 * opc对接服务
 */
public interface OPCService {
    /**
     * 从opcserver获取点位数据
     *
     * @param
     */
    void dataFromServer() throws DuplicateGroupException, NotConnectedException, JIException, UnknownHostException, AddFailedException, InterruptedException;
}
