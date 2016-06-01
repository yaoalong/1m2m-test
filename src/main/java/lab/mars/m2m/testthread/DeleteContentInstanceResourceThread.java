package lab.mars.m2m.testthread;

import lab.mars.m2m.test.resourcetest.ResourceTestBase;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Author:yaoalong.
 * Date:2016/5/26.
 * Email:yaoalong@foxmail.com
 */
public class DeleteContentInstanceResourceThread extends ParentResourceThread implements Runnable {


    public DeleteContentInstanceResourceThread(ResourceTestBase resourceTestBase, AtomicLong senderCount, long maxCount, List<String> contentInstanceList) {
        super(resourceTestBase, senderCount, maxCount, contentInstanceList);
    }

    public DeleteContentInstanceResourceThread(ResourceTestBase resourceTestBase) {
        this.resourceTestBase = resourceTestBase;
    }

    @Override
    public void run() {
        deleteResource();
    }
}