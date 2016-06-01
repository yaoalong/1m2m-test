package lab.mars.m2m.testthread;

import lab.mars.m2m.test.resourcetest.ResourceTestBase;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Author:yaoalong.
 * Date:2016/5/26.
 * Email:yaoalong@foxmail.com
 */
public class AddContentInstanceThread extends ParentResourceThread implements Runnable {


    public AddContentInstanceThread(ResourceTestBase resourceTestBase, AtomicLong senderCount, long maxCount, List<String> resourceList) {
        super(resourceTestBase, senderCount, maxCount, resourceList);
    }

    public AddContentInstanceThread(ResourceTestBase resourceTestBase) {
        this.resourceTestBase = resourceTestBase;
    }

    @Override
    public void run() {
       createResource(2);
    }

}
