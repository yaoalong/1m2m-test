package lab.mars.onem2m.pojo;

import lab.mars.m2m.reality.pojo.Machine;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Author:yaoalong.
 * Date:2016/4/26.
 * Email:yaoalong@foxmail.com
 */
public class NotificationUtils {

    public static AtomicLong zxid = new AtomicLong(0);

    public static ConcurrentHashMap<Long, ResourceDO> sensors = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Long, Long> zxidMapStartTime = new ConcurrentHashMap<>();
    /**
     * 设备对应的containerURI
     */
    public static ConcurrentHashMap<String, Machine> cntMapMachine = new ConcurrentHashMap<>();
}

