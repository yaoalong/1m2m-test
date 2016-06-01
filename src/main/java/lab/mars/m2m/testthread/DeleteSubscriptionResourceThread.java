package lab.mars.m2m.testthread;

import lab.mars.m2m.test.resourcetest.ResourceTestBase;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Author:yaoalong.
 * Date:2016/5/26.
 * Email:yaoalong@foxmail.com
 */
public class DeleteSubscriptionResourceThread extends ParentResourceThread implements Runnable {


    public DeleteSubscriptionResourceThread(ResourceTestBase resourceTestBase, AtomicLong senderCount, long maxCount, List<String> subscriptionList) {
        super(resourceTestBase, senderCount, maxCount, subscriptionList);
    }

    public DeleteSubscriptionResourceThread(ResourceTestBase resourceTestBase) {
        this.resourceTestBase = resourceTestBase;
    }

    @Override
    public void run() {
        deleteResource();
    }
}