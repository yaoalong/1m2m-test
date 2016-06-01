package lab.mars.m2m.testthread;

import lab.mars.m2m.test.resourcetest.ResourceTestBase;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;

/**
 * Author:yaoalong.
 * Date:2016/5/26.
 * Email:yaoalong@foxmail.com
 */
public class DeleteContainerResourceThread extends ParentResourceThread implements Runnable {


    public DeleteContainerResourceThread(ResourceTestBase resourceTestBase, AtomicLong senderCount, long maxCount, List<String> containerList) {
        super(resourceTestBase, senderCount, maxCount, containerList);
    }

    public DeleteContainerResourceThread(ResourceTestBase resourceTestBase) {
        this.resourceTestBase = resourceTestBase;
    }

    @Override
    public void run() {
        deleteResource();
    }
}
