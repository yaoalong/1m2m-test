package lab.mars.m2m.test;

import lab.mars.m2m.protocol.resource.m2m_ContentInstance;
import lab.mars.m2m.protocol.resource.m2m_resource;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static lab.mars.m2m.protocol.enumeration.m2m_resourceType.*;

/**
 * Created by haixiao on 2015/1/5. test
 */
public class TestContentInstance extends TestBase {
    @Test
    public void testCreateContentInstance() throws Exception {
        String cntUri = testCreate(csebaseuri, container, "createContainer.xml", OK);
        String cinUri = testCreate(cntUri, contentInstance, "createContentInstance.xml", OK);
        testRetrieve(cinUri, "getContentInstance.xml", OK);
        testRetrieve(cntUri, "getContainer.xml", OK);
    }

    @Test
    public void testCreateContentInstanceUnderContainer() throws Exception {
        String cntUri = "/csebase/4";
        String cinUri = testCreate(cntUri, contentInstance, "createContentInstance.xml", OK);
        testRetrieve(cinUri, "getContentInstance.xml", OK);
        testRetrieve(cntUri, "getContainer.xml", OK);
    }

    @Test
    public void testCreateContainer() throws Exception {
        long start = System.nanoTime();
        int num = 2000;
        for (int i = 0; i < num; i++) {
            String cntUri = testCreate(csebaseuri, container, "createContainer.xml", OK);
//            System.out.println("created " + cinUri);
            m2m_resource cin = testRetrieve(cntUri, "getContainer.xml", OK);
            System.out.println("retrieved " + cntUri);
        }
        long duration = System.nanoTime() - start;
        System.out.println(
                duration * 1e-6 + " ms\n" + (duration * 1e-6 / num) + "ms/op\n" + num * 1e9 / duration + "op/s");
    }

    @Test
    public void testCreateContentInstance2() throws Exception {
        String cntUri = testCreate(csebaseuri, container, "createContainer.xml", OK);
        long start = System.nanoTime();
        int num = 20;
        for (int i = 0; i < num; i++) {
            String cinUri = testCreate(cntUri, contentInstance, "createContentInstance.xml", OK);
//            System.out.println("created " + cinUri);
            m2m_resource cin = testRetrieve(cinUri, "getContentInstance.xml", OK);
            System.out.println("retrieved " + cinUri);
            Assert.assertEquals(((m2m_ContentInstance) cin).cnf, "mars");
        }
        long duration = System.nanoTime() - start;
        System.out.println(
                duration * 1e-6 + " ms\n" + (duration * 1e-6 / num) + "ms/op\n" + num * 1e9 / duration + "op/s");
//        m2m_resource resource = testRetrieve(cntUri, "getContainer.xml", OK);
//        System.out.println(((m2m_Container) resource).ch.size());
    }

    private void testUnit() throws Exception {
        String cntUri = testCreate(csebaseuri, container, "createContainer.xml", OK);
        long start = System.nanoTime();
        int num = 20;
        for (int i = 0; i < num; i++) {
            String cinUri = testCreate(cntUri, contentInstance, "createContentInstance.xml", OK);
//            System.out.println("created " + cinUri);
            m2m_resource cin = testRetrieve(cinUri, "getContentInstance.xml", OK);
            System.out.println("retrieved " + cinUri);
            Assert.assertEquals(((m2m_ContentInstance) cin).cnf, "mars");
        }
        long duration = System.nanoTime() - start;
        System.out.println(
                duration * 1e-6 + " ms\n" + (duration * 1e-6 / num) + "ms/op\n" + num * 1e9 / duration + "op/s");
    }

    @Test
    public void concurrentTest() throws Exception {
        int num = 4;
        CountDownLatch latch = new CountDownLatch(num);
        ExecutorService executor = Executors.newFixedThreadPool(num);
        for (int i = 0; i < num; i++) {
            executor.submit(() -> {
                try {
                    testUnit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                latch.countDown();
            });
        }

        latch.await();
    }

    @Test
    public void concurrentTest2() throws Exception {
        int num = 100;
        CountDownLatch latch = new CountDownLatch(num);
        ExecutorService executor = Executors.newFixedThreadPool(num);
        for (int i = 0; i < num; i++) {
            executor.submit(() -> {
                try {
                    testUnit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                latch.countDown();
            });
        }

        latch.await();
    }
}
