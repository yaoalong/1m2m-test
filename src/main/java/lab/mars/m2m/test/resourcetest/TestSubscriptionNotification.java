package lab.mars.m2m.test.resourcetest;

import lab.mars.m2m.protocol.common.m2m_childResourceRef;
import lab.mars.m2m.protocol.resource.m2m_ContentInstance;
import lab.mars.m2m.protocol.resource.m2m_resource;
import lab.mars.m2m.reflection.ResourceReflection;
import lab.mars.m2m.testthread.AddSubscrptionNotificationThread;
import lab.mars.onem2m.pojo.NotificationUtils;
import lab.mars.onem2m.pojo.ResourceDO;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;

/**
 * Author:yaoalong.
 * Date:2016/5/27.
 * Email:yaoalong@foxmail.com
 */
public class TestSubscriptionNotification extends PreCreateResource {

    @Override
    public void handleNotify(m2m_childResourceRef ref) {
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        executorService.submit(() -> {
            m2m_resource response = null;
            try {
                response = testRetrieve(ref.v, null, OK, 0);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            if (response instanceof m2m_ContentInstance) {
                m2m_ContentInstance cin = (m2m_ContentInstance) response;
                Object object = ResourceReflection.deserializeKryo(cin.con);
                if (object instanceof ResourceDO) {
                    ResourceDO resourceDO = (ResourceDO) object;
                    System.out.println("cost time:" + (System.currentTimeMillis() - NotificationUtils.zxidMapStartTime.get(resourceDO.getId())));
                }

            } else {
                System.out.println("classs:" + response.getClass());
            }
        });
    }

    @Test
    public void testNotification() throws IOException {
        List<String> containerUri = new ArrayList<>();
        for (int i = 0; i < maxCount; i++) {
            try {
                String ae = createSyncAEResource();
                String container = createSyncContainer(ae);
                containerUri.add(container);
                createSyncSubScription(container);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 4; i++) {
            executorService.submit(new AddSubscrptionNotificationThread(this, senderCount, maxCount, containerUri));
        }
        System.in.read();
    }
}
