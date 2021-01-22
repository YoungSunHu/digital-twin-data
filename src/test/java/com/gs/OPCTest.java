package com.gs;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.JIString;
import org.jinterop.dcom.core.JIVariant;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openscada.opc.dcom.common.Result;
import org.openscada.opc.dcom.da.OPCITEMRESULT;
import org.openscada.opc.lib.common.AlreadyConnectedException;
import org.openscada.opc.lib.common.ConnectionInformation;
import org.openscada.opc.lib.common.NotConnectedException;
import org.openscada.opc.lib.da.*;
import org.openscada.opc.lib.da.browser.TreeBrowser;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * @author ：YoungSun
 * @date ：Created in 2021/1/14 9:06
 * @modified By：
 */
@SpringBootTest(classes = DigitalTwinDataApplication.class)
@RunWith(SpringRunner.class)
public class OPCTest {
    @Test
    public void opcTest() {
        // 连接信息
        final ConnectionInformation ci = new ConnectionInformation();
        ci.setHost("192.168.9.12");         // 电脑IP
        ci.setDomain("");                  // 域，为空就行
        ci.setUser("Administrator");             // 电脑上自己建好的用户名
        ci.setPassword("HollySys2008");          // 用户名的密码

        // 使用MatrikonOPC Server的配置
        // ci.setClsid("F8582CF2-88FB-11D0-B850-00C0F0104305"); // MatrikonOPC的注册表ID，可以在“组件服务”里看到
        // final String itemId = "u.u";    // MatrikonOPC Server上配置的项的名字按实际

        // 使用KEPServer的配置
        ci.setClsid("E25E3A99-EB1A-40A9-9C49-42BFB0E439F0"); // KEPServer的注册表ID，可以在“组件服务”里看到
        final String itemId = "_PublicGroup.fhf6.";    // KEPServer上配置的项的名字，没有实际PLC，用的模拟器：simulator
        // final String itemId = "通道 1.设备 1.标记 1";

        // 启动服务
        final Server server = new Server(ci, Executors.newSingleThreadScheduledExecutor());

        try {
            // 连接到服务
            server.connect();
            // add sync access, poll every 500 ms，启动一个同步的access用来读取地址上的值，线程池每500ms读值一次
            // 这个是用来循环读值的，只读一次值不用这样
            final AccessBase access = new SyncAccess(server, 500);
            // 这是个回调函数，就是读到值后执行这个打印，是用匿名类写的，当然也可以写到外面去
            access.addItem(itemId, new DataCallback() {
                @Override
                public void changed(Item item, ItemState itemState) {
                    int type = 0;
                    try {
                        type = itemState.getValue().getType(); // 类型实际是数字，用常量定义的
                    } catch (JIException e) {
                        e.printStackTrace();
                    }
                    System.out.println("监控项的数据类型是：-----" + type);
                    System.out.println("监控项的时间戳是：-----" + itemState.getTimestamp().getTime());
                    System.out.println("监控项的详细信息是：-----" + itemState);
                    System.out.println("监控项的详细信息的value：-----" + itemState.getValue().toString().replace("[", "").replace("]", ""));
                    String s = itemState.getValue().toString();


                    // 如果读到是short类型的值
                    if (type == JIVariant.VT_I2) {
                        short n = 0;
                        try {
                            n = itemState.getValue().getObjectAsShort();
                        } catch (JIException e) {
                            e.printStackTrace();
                        }
                        System.out.println("-----short类型值： " + n);
                    }

                    // 如果读到是字符串类型的值
                    if (type == JIVariant.VT_BSTR) {  // 字符串的类型是8
                        JIString value = null;
                        try {
                            value = itemState.getValue().getObjectAsString();
                        } catch (JIException e) {
                            e.printStackTrace();
                        } // 按字符串读取
                        String str = value.getString(); // 得到字符串
                        System.out.println("-----String类型值： " + str);
                    }
                }
            });
            // start reading，开始读值
            access.bind();
            // wait a little bit，有个10秒延时
            Thread.sleep(10000);
            // stop reading，停止读取
            access.unbind();
        } catch (final JIException | AddFailedException e) {
            // System.out.println(String.format("%08X: %s", e.getErrorCode(), server.getErrorMessage(e.getErrorCode())));
        } catch (AlreadyConnectedException e) {
            e.printStackTrace();
        } catch (DuplicateGroupException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 单次获取item值
     *
     * @throws DuplicateGroupException
     * @throws NotConnectedException
     * @throws JIException
     * @throws UnknownHostException
     */
    @Test
    public void opcTest02() throws DuplicateGroupException, NotConnectedException, JIException, UnknownHostException {
        // 连接信息
        final ConnectionInformation ci = new ConnectionInformation();
        ci.setHost("192.168.9.12");         // 电脑IP
        ci.setDomain("");                  // 域，为空就行
        ci.setUser("Administrator");             // 电脑上自己建好的用户名
        ci.setPassword("HollySys2008");          // 用户名的密码

        // 使用MatrikonOPC Server的配置
        // ci.setClsid("F8582CF2-88FB-11D0-B850-00C0F0104305"); // MatrikonOPC的注册表ID，可以在“组件服务”里看到
        // final String itemId = "u.u";    // MatrikonOPC Server上配置的项的名字按实际

        // 使用KEPServer的配置
        ci.setClsid("E25E3A99-EB1A-40A9-9C49-42BFB0E439F0"); // KEPServer的注册表ID，可以在“组件服务”里看到
        //final String itemId = "_PublicGroup.fhf6";    // KEPServer上配置的项的名字，没有实际PLC，用的模拟器：simulator
        // final String itemId = "通道 1.设备 1.标记 1";

        // 启动服务
        final Server server = new Server(ci, Executors.newSingleThreadScheduledExecutor());
        // 连接到服务
        try {
            server.connect();
            Group group = server.addGroup();
            TreeBrowser treeBrowser = server.getTreeBrowser();
            System.out.println(treeBrowser);
            Map<String, Result<OPCITEMRESULT>> stringResultMap = group.validateItems();
            System.out.println(stringResultMap);
            Item item = group.addItem("_PublicGroup.fhf6.F104_tValue");
            System.out.println(item.getId() + ":" + item.read(false).getValue().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * kepservice测试
     *
     * @throws DuplicateGroupException
     * @throws NotConnectedException
     * @throws JIException
     * @throws UnknownHostException
     */
    @Test
    public void opcTest03() throws DuplicateGroupException, NotConnectedException, JIException, UnknownHostException {
        // 连接信息
        final ConnectionInformation ci = new ConnectionInformation();
        ci.setHost("192.168.31.43");         // 电脑IP
        ci.setDomain("");                  // 域，为空就行
        ci.setUser("Administrator");             // 电脑上自己建好的用户名
        ci.setPassword("HollySys2008");          // 用户名的密码

        // 使用MatrikonOPC Server的配置
        // ci.setClsid("F8582CF2-88FB-11D0-B850-00C0F0104305"); // MatrikonOPC的注册表ID，可以在“组件服务”里看到
        // final String itemId = "u.u";    // MatrikonOPC Server上配置的项的名字按实际

        // 使用KEPServer的配置
        ci.setClsid("7BC0CC8E-482C-47CA-ABDC-0FE7F9C6E729"); // KEPServer的注册表ID，可以在“组件服务”里看到
        //final String itemId = "_PublicGroup.fhf6";    // KEPServer上配置的项的名字，没有实际PLC，用的模拟器：simulator
        // final String itemId = "通道 1.设备 1.标记 1";

        // 启动服务
        final Server server = new Server(ci, Executors.newSingleThreadScheduledExecutor());
        // 连接到服务
        try {
            server.connect();
            Group group = server.addGroup();
            TreeBrowser treeBrowser = server.getTreeBrowser();
            System.out.println(treeBrowser);
            Map<String, Result<OPCITEMRESULT>> stringResultMap = group.validateItems();
            System.out.println(stringResultMap);
            Item item = group.addItem("channel01.equ01.point01");
            while (true) {
                JIVariant value = item.read(false).getValue();
                int type = value.getType();
                short objectAsShort = value.getObjectAsShort();
                System.out.println(item.getId() + ":" + objectAsShort);
                Thread.sleep(5000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void opcTest04() {
        // 连接信息
        final ConnectionInformation ci = new ConnectionInformation();
        ci.setHost("192.168.31.43");         // 电脑IP
        ci.setDomain("");                  // 域，为空就行
        ci.setUser("Administrator");             // 电脑上自己建好的用户名
        ci.setPassword("HollySys2008");          // 用户名的密码

        // 使用MatrikonOPC Server的配置
        // ci.setClsid("F8582CF2-88FB-11D0-B850-00C0F0104305"); // MatrikonOPC的注册表ID，可以在“组件服务”里看到
        // final String itemId = "u.u";    // MatrikonOPC Server上配置的项的名字按实际

        // 使用KEPServer的配置
        ci.setClsid("7BC0CC8E-482C-47CA-ABDC-0FE7F9C6E729"); // KEPServer的注册表ID，可以在“组件服务”里看到
        final String itemId = "channel01.equ01.point01";    // KEPServer上配置的项的名字，没有实际PLC，用的模拟器：simulator
        // final String itemId = "通道 1.设备 1.标记 1";

        // 启动服务
        final Server server = new Server(ci, Executors.newSingleThreadScheduledExecutor());

        try {
            // 连接到服务
            server.connect();
            // add sync access, poll every 500 ms，启动一个同步的access用来读取地址上的值，线程池每500ms读值一次
            // 这个是用来循环读值的，只读一次值不用这样
            final AccessBase access = new SyncAccess(server, 500);
            // 这是个回调函数，就是读到值后执行这个打印，是用匿名类写的，当然也可以写到外面去
            access.addItem(itemId, new DataCallback() {
                @Override
                public void changed(Item item, ItemState itemState) {
                    int type = 0;
                    try {
                        type = itemState.getValue().getType(); // 类型实际是数字，用常量定义的
                    } catch (JIException e) {
                        e.printStackTrace();
                    }
                    System.out.println("监控项的数据类型是：-----" + type);
                    System.out.println("监控项的时间戳是：-----" + itemState.getTimestamp().getTime());
                    System.out.println("监控项的详细信息是：-----" + itemState);
                    System.out.println("监控项的详细信息的value：-----" + itemState.getValue().toString().replace("[", "").replace("]", ""));
                    String s = itemState.getValue().toString();


                    // 如果读到是short类型的值
                    if (type == JIVariant.VT_I2) {
                        short n = 0;
                        try {
                            n = itemState.getValue().getObjectAsShort();
                        } catch (JIException e) {
                            e.printStackTrace();
                        }
                        System.out.println("-----short类型值： " + n);
                    }

                    // 如果读到是字符串类型的值
                    if (type == JIVariant.VT_BSTR) {  // 字符串的类型是8
                        JIString value = null;
                        try {
                            value = itemState.getValue().getObjectAsString();
                        } catch (JIException e) {
                            e.printStackTrace();
                        } // 按字符串读取
                        String str = value.getString(); // 得到字符串
                        System.out.println("-----String类型值： " + str);
                    }
                }
            });
            // start reading，开始读值
            access.bind();
            // wait a little bit，有个10秒延时
            Thread.sleep(10000);
            // stop reading，停止读取
            access.unbind();
        } catch (final JIException | AddFailedException e) {
            // System.out.println(String.format("%08X: %s", e.getErrorCode(), server.getErrorMessage(e.getErrorCode())));
        } catch (AlreadyConnectedException e) {
            e.printStackTrace();
        } catch (DuplicateGroupException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}

