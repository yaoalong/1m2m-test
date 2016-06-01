package lab.mars.m2m.test.resourcetest;

import lab.mars.m2m.testthread.DeleteAEResourceThread;
import lab.mars.m2m.testthread.DeleteContainerResourceThread;
import lab.mars.m2m.testthread.DeleteContentInstanceResourceThread;
import lab.mars.m2m.testthread.DeleteSubscriptionResourceThread;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Author:yaoalong.
 * Date:2016/5/26.
 * Email:yaoalong@foxmail.com
 */
public class DeleteResourceTest extends PreCreateResource {


    @Test
    public void testDeleteAE() throws IOException {
        preCreateAE();
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 4; i++) {
            executorService.submit(new DeleteAEResourceThread(this, senderCount, maxCount, resourceList));
        }
        System.in.read();
    }


    @Test
    public void testDeleteContentInstance() throws IOException {
        preCreateContentInstance();
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 4; i++) {
            executorService.submit(new DeleteContentInstanceResourceThread(this, senderCount, maxCount, resourceList));
        }
    }

    @Test
    public void testDeleteContainer() throws IOException {
        preCreateContainer();
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 10; i++) {
            executorService.submit(new DeleteContainerResourceThread(this, senderCount, maxCount, resourceList));
        }
        System.in.read();
    }

    @Test
    public void testDeleteSubscription() throws IOException {
        preCreateSubscription();
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 10; i++) {
            executorService.submit(new DeleteSubscriptionResourceThread(this, senderCount, maxCount, resourceList));
        }
        System.in.read();
    }
}
