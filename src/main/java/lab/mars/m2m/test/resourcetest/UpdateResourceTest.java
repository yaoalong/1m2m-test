package lab.mars.m2m.test.resourcetest;

import lab.mars.m2m.testthread.UpdateAEResourceThread;
import lab.mars.m2m.testthread.UpdateContainerResourceThread;
import lab.mars.m2m.testthread.UpdateContentInstanceResourceThread;
import lab.mars.m2m.testthread.UpdateSubscriptionResourceThread;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Author:yaoalong.
 * Date:2016/5/26.
 * Email:yaoalong@foxmail.com
 */
public class UpdateResourceTest extends PreCreateResource {
    @Test
    public void testUpdateeAE() throws IOException {
        preCreateAE();
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 4; i++) {
            executorService.submit(new UpdateAEResourceThread(this, senderCount, maxCount,resourceList));
        }
        System.in.read();
    }

    @Test
    public void testUpdateContentInstance() throws IOException {
        preCreateContentInstance();
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 4; i++) {
            executorService.submit(new UpdateContentInstanceResourceThread(this, senderCount, maxCount,resourceList));
        }
    }

    @Test
    public void testUpdateContainer() throws IOException {
        preCreateContainer();
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 10; i++) {
            executorService.submit(new UpdateContainerResourceThread(this, senderCount, maxCount,resourceList));
        }
        System.in.read();
    }

    @Test
    public void testUpdateSubscription() throws Exception {
        preCreateSubscription();
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 10; i++) {
            executorService.submit(new UpdateSubscriptionResourceThread(this, senderCount, maxCount, resourceList));
        }
        System.in.read();
    }
}
