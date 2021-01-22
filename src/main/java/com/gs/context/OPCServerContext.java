package com.gs.context;

import com.gs.dao.entity.OPCItemEntity;
import com.gs.dao.mapper.OPCItemMapper;
import lombok.extern.slf4j.Slf4j;
import org.jinterop.dcom.common.JIException;
import org.openscada.opc.lib.common.ConnectionInformation;
import org.openscada.opc.lib.common.NotConnectedException;
import org.openscada.opc.lib.da.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * @author ：YoungSun
 * @date ：Created in 2021/1/18 9:28
 * @modified By：
 * OPC服务
 */
@Slf4j
@Component
public class OPCServerContext {

    private Server server;

    private Group group;

    @Autowired
    OPCItemMapper opcItemMapper;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    @Value("${opc.server.url}")
    private String serverUrl;

    @Value("${opc.server.id}")
    private String serverId;

    @Value("${opc.server.username}")
    private String serverUsername;

    @Value("${opc.server.password}")
    private String serverPassword;

    private List<OPCItemEntity> opcItemEntities;

    private List<Item> items = new ArrayList<>();

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    private boolean isConnected;

    @PostConstruct
    private void serverConnect() throws DuplicateGroupException, NotConnectedException, JIException, UnknownHostException, AddFailedException, InterruptedException {
        ConnectionInformation ci = new ConnectionInformation();
        ci.setClsid(serverId);
        ci.setDomain("");
        ci.setHost(serverUrl);
        ci.setPassword(serverPassword);
        ci.setUser(serverUsername);
        server = new Server(ci, Executors.newSingleThreadScheduledExecutor());
        do {
            try {
                server.connect();
                log.info("OPCServer连接成功");
                isConnected = true;
            } catch (Exception e) {
                log.error("OPCServer连接失败:{}", e.getCause());
                isConnected = false;
                Thread.sleep(5000);
            }
        } while (isConnected == false);
        group = server.addGroup();
        opcItemEntities = opcItemMapper.selectList(null);
        for (OPCItemEntity opcItemEntity : opcItemEntities) {
            try {
                Item item = group.addItem(opcItemEntity.getItemId());
                items.add(item);
            } catch (JIException e) {
                e.printStackTrace();
            } catch (AddFailedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void reconnect() throws DuplicateGroupException, NotConnectedException, JIException, UnknownHostException, InterruptedException {
        isConnected = false;
        ConnectionInformation ci = new ConnectionInformation();
        ci.setClsid(serverId);
        ci.setDomain("");
        ci.setHost(serverUrl);
        ci.setPassword(serverPassword);
        ci.setUser(serverUsername);
        server = new Server(ci, Executors.newSingleThreadScheduledExecutor());
        do {
            try {
                server.connect();
                log.info("OPCServer连接成功");
                isConnected = true;
            } catch (Exception e) {
                log.error("OPCServer连接失败:{}", e.getCause());
                isConnected = false;
                Thread.sleep(5000);
            }
        } while (isConnected == false);
        group = server.addGroup();
        opcItemEntities = opcItemMapper.selectList(null);
        items.clear();
        for (OPCItemEntity opcItemEntity : opcItemEntities) {
            try {
                Item item = group.addItem(opcItemEntity.getItemId());
                items.add(item);
            } catch (JIException e) {
                e.printStackTrace();
            } catch (AddFailedException e) {
                e.printStackTrace();
            }
        }
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getServerUsername() {
        return serverUsername;
    }

    public void setServerUsername(String serverUsername) {
        this.serverUsername = serverUsername;
    }

    public String getServerPassword() {
        return serverPassword;
    }

    public void setServerPassword(String serverPassword) {
        this.serverPassword = serverPassword;
    }

    public List<OPCItemEntity> getOpcItemEntities() {
        return opcItemEntities;
    }

    public void setOpcItemEntities(List<OPCItemEntity> opcItemEntities) {
        this.opcItemEntities = opcItemEntities;
    }
}
