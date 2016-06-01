package lab.mars.m2m.test.resourcetest;

import lab.mars.m2m.testthread.RetrieveAEResourceThread;
import lab.mars.m2m.testthread.RetrieveContainerResourceThread;
import lab.mars.m2m.testthread.RetrieveContentInstanceResourceThread;
import lab.mars.m2m.testthread.RetrieveSubscriptionResourceThread;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Author:yaoalong.
 * Date:2016/5/26.
 * Email:yaoalong@foxmail.com
 */
public class RetrieveResourceTest extends PreCreateResource {
    @Test
    public void testRetrieveAE() throws IOException {
        preCreateAE();
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 4; i++) {
            executorService.submit(new RetrieveAEResourceThread(this, senderCount, maxCount, resourceList));
        }
        System.in.read();
    }

    @Test
    public void testRetrieveContentInstance() throws IOException {
        preCreateContentInstance();
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 4; i++) {
            executorService.submit(new RetrieveContentInstanceResourceThread(this, senderCount, maxCount, resourceList));
        }
    }

    @Test
    public void testRetrieveContainer() throws IOException {
        preCreateContainer();
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 10; i++) {
            executorService.submit(new RetrieveContainerResourceThread(this, senderCount, maxCount, resourceList));
        }
        System.in.read();
    }

    @Test
    public void testRetrieveSubscription() throws IOException {
        preCreateSubscription();
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 10; i++) {
            executorService.submit(new RetrieveSubscriptionResourceThread(this, senderCount, maxCount, resourceList));
        }
        System.in.read();
    }
}
