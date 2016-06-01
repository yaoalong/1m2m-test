package lab.mars.m2m.test.resourcetest;

import lab.mars.m2m.testthread.AddAEResoureThread;
import lab.mars.m2m.testthread.AddContainerResourceThread;
import lab.mars.m2m.testthread.AddContentInstanceThread;
import lab.mars.m2m.testthread.AddSubscriptionResourceThread;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Author:yaoalong.
 * Date:2016/5/25.
 * Email:yaoalong@foxmail.com
 */
public class CreateResourceTest extends PreCreateResource {

    @Test
    public void testCreateAE() throws IOException {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        for (int i = 0; i < 4; i++) {
            executorService.submit(new AddAEResoureThread(this, senderCount, maxCount));
        }
        System.in.read();
    }

    @Test
    public void testCreateContentInstance() throws IOException {
        preCreateContainer();
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 4; i++) {
            executorService.submit(new AddContentInstanceThread(this, senderCount, maxCount, resourceList));
        }
    }

    @Test
    public void testCreateContainer() throws IOException {
        preCreateAE();
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 10; i++) {
            executorService.submit(new AddContainerResourceThread(this, senderCount, maxCount, resourceList));
        }
        System.in.read();
    }

    @Test
    public void testCreateSubScription() throws IOException {
        preCreateContainer();
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 10; i++) {
            executorService.submit(new AddSubscriptionResourceThread(this, senderCount, maxCount, resourceList));
        }
        System.in.read();
    }
    public static void main(String args[]) throws Exception {
        CreateResourceTest createResourceTest = new CreateResourceTest();
        createResourceTest.setUp();
        createResourceTest.testCreateAE();
    }
}
