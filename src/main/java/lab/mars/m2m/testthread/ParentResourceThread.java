package lab.mars.m2m.testthread;

import lab.mars.m2m.test.resourcetest.ResourceTestBase;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;

/**
 * Author:yaoalong.
 * Date:2016/5/27.
 * Email:yaoalong@foxmail.com
 */
public class ParentResourceThread {

    protected AtomicLong senderCount;
    protected long maxCount;

    protected ResourceTestBase resourceTestBase;

    protected List<String> resourceList;

    public ParentResourceThread(ResourceTestBase resourceTestBase, AtomicLong senderCount, long maxCount) {
        this.resourceTestBase = resourceTestBase;
        this.senderCount = senderCount;
        this.maxCount = maxCount;
    }

    public ParentResourceThread() {

    }

    public ParentResourceThread(ResourceTestBase resourceTestBase, AtomicLong senderCount, long maxCount, List<String> resourceList) {
        this.resourceTestBase = resourceTestBase;
        this.senderCount = senderCount;
        this.maxCount = maxCount;
        this.resourceList = resourceList;
    }

    protected void deleteResource() {
        while (true) {
            int index = 0;
            if (senderCount != null) {
                if (senderCount.get() > maxCount) {
                    return;
                }
                index = (int) senderCount.getAndIncrement();
            }
            try {
                resourceTestBase.testDelete(resourceList.get(index), OK);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
    protected void createResource(int flag) {
        while (true) {
            int index = 0;
            if (senderCount != null) {
                if (senderCount.get() > maxCount) {
                    return;
                }
                index = (int) senderCount.getAndIncrement();
            }
            try {
                if(flag==1){
                    resourceTestBase.createAsyncContainer(resourceList.get(index));
                }
                else if(flag==2){
                    resourceTestBase.createAsyncContainerInstance(resourceList.get(index));
                }
                else if(flag==3){
                    resourceTestBase.createAsyncSubScriptions(resourceList.get(index));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
    protected  void retrieveResource(){
        while (true) {
            int index = 0;
            if (senderCount != null) {
                if (senderCount.get() > maxCount) {
                    return;
                }
                index = (int) senderCount.getAndIncrement();
            }
            try {
                resourceTestBase.testRetrieve(resourceList.get(index), OK);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
    protected void  updateResource(int flag) {
        while (true) {
            int index = 0;
            if (senderCount != null) {
                if (senderCount.get() > maxCount) {
                    return;
                }
                index = (int) senderCount.getAndIncrement();
            }
            try {
                if(flag==0){
                    resourceTestBase.updateAsyncAEResource(resourceList.get(index));
                }
                if(flag==1){
                    resourceTestBase.updateAsyncContainer(resourceList.get(index));
                }
                else if(flag==2){
                    resourceTestBase.updateAsyncContentInstance(resourceList.get(index));
                }
                else if(flag==3){
                    resourceTestBase.updateAsyncSubScriptions(resourceList.get(index));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}
