package lab.mars.m2m.testthread;

import lab.mars.m2m.test.resourcetest.ResourceTestBase;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Author:yaoalong.
 * Date:2016/5/25.
 * Email:yaoalong@foxmail.com
 */
public class AddAEResoureThread extends ParentResourceThread implements Runnable {


    public AddAEResoureThread(ResourceTestBase resourceTestBase, AtomicLong senderCount, long maxCount) {
        super(resourceTestBase, senderCount, maxCount);
    }

    public AddAEResoureThread(ResourceTestBase resourceTestBase) {
        this.resourceTestBase = resourceTestBase;
    }

    @Override
    public void run() {
        while (true) {
            if (senderCount != null) {
                if (senderCount.get() > maxCount) {
                    return;
                }
                senderCount.getAndIncrement();
            }
            try {
                resourceTestBase.createAsyncAEResource();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}
