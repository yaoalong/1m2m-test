package lab.mars.m2m.testthread;

import lab.mars.m2m.test.resourcetest.ResourceTestBase;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Author:yaoalong.
 * Date:2016/5/26.
 * Email:yaoalong@foxmail.com
 */
public class UpdateContentInstanceResourceThread extends ParentResourceThread implements Runnable {


    public UpdateContentInstanceResourceThread(ResourceTestBase resourceTestBase, AtomicLong senderCount, long maxCount, List<String> resourceList) {
        super(resourceTestBase, senderCount, maxCount, resourceList);
    }

    public UpdateContentInstanceResourceThread(ResourceTestBase resourceTestBase) {
        this.resourceTestBase = resourceTestBase;
    }

    @Override
    public void run() {
        updateResource(2);
    }
}
