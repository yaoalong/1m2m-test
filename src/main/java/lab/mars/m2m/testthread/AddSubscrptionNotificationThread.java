package lab.mars.m2m.testthread;

import lab.mars.m2m.protocol.primitive.m2m_primitiveContentType;
import lab.mars.m2m.protocol.resource.m2m_ContentInstance;
import lab.mars.m2m.reflection.ResourceReflection;
import lab.mars.m2m.test.resourcetest.ResourceTestBase;
import lab.mars.onem2m.pojo.NotificationUtils;
import lab.mars.onem2m.pojo.ResourceDO;

import java.io.StringWriter;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static lab.mars.m2m.protocol.enumeration.m2m_resourceType.contentInstance;
import static lab.mars.m2m.test.resourcetest.ResourceTestBase.ASYNC;

/**
 * Author:yaoalong.
 * Date:2016/5/27.
 * Email:yaoalong@foxmail.com
 */

/**
 * 用来创建AE
 */
public class AddSubscrptionNotificationThread extends ParentResourceThread implements Runnable {


    public AddSubscrptionNotificationThread(ResourceTestBase resourceTestBase, AtomicLong senderCount, long maxCount, List<String> resourceList) {
        super(resourceTestBase, senderCount, maxCount, resourceList);
    }

    public AddSubscrptionNotificationThread(ResourceTestBase resourceTestBase) {
        this.resourceTestBase = resourceTestBase;
    }

    @Override
    public void run() {
        while (true) {
            if (senderCount != null) {
                if (senderCount.get() >= maxCount*100) {
                    return;
                }

            }
            int index;
            try {
                Random random=new Random();
                index=random.nextInt(((int)maxCount));
                m2m_primitiveContentType m2m_primitiveContentType = new m2m_primitiveContentType();
                m2m_ContentInstance m2m_contentInstance = new m2m_ContentInstance();
                ResourceDO resourceDO = new ResourceDO();
                long zxid = NotificationUtils.zxid.getAndIncrement();
                resourceDO.setId(zxid);
                m2m_contentInstance.con = ResourceReflection.serializeKryo(resourceDO);
                m2m_primitiveContentType.value = m2m_contentInstance;
                StringWriter sw = new StringWriter();
                resourceTestBase.marshaller.get().marshal(m2m_primitiveContentType, sw);
                NotificationUtils.zxidMapStartTime.put(zxid,System.currentTimeMillis());
                resourceTestBase.testCreate(resourceList.get(index), contentInstance, sw.toString(), OK, ASYNC);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }
}
