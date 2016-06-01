package lab.mars.m2m.test.resourcetest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Author:yaoalong.
 * Date:2016/5/27.
 * Email:yaoalong@foxmail.com
 */
public class PreCreateResource extends ResourceTestBase {
    protected List<String> resourceList = new ArrayList<>();

    @Override
    public void handleBefore() {
        senderCount = new AtomicLong(0);
        maxCount = 1000000;
        recvTime = new AtomicLong(0);
        receiveCount = new AtomicLong(0);
    }

    public void preCreateAE() throws IOException {
        for (int i = 0; i < maxCount; i++) {
            try {
                resourceList.add(createSyncAEResource());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void preCreateContentInstance() throws IOException {
        for (int i = 0; i < maxCount; i++) {
            try {
                String ae = createSyncAEResource();
                String container = createSyncContainer(ae);
                String content = createSyncContentInstance(container);
                resourceList.add(content);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void preCreateContainer() throws IOException {
        for (int i = 0; i < maxCount; i++) {
            try {
                String ae = createSyncAEResource();
                String container = createSyncContainer(ae);
                resourceList.add(container);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void preCreateSubscription() throws IOException {
        for (int i = 0; i < maxCount; i++) {
            try {
                String ae = createSyncAEResource();
                String container = createSyncContainer(ae);
                String subscription = createSyncSubScription(container);
                resourceList.add(subscription);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
