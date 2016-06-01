package lab.mars.m2m.test;

import lab.mars.m2m.protocol.common.m2m_childResourceRef;
import lab.mars.m2m.protocol.resource.m2m_CSEBase;
import lab.mars.m2m.protocol.resource.m2m_resource;
import org.junit.Assert;
import org.junit.Test;

import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static lab.mars.m2m.protocol.enumeration.m2m_resourceType.*;


/**
 * Created by haixiao on 2015/1/5.
 * test
 */
public class TestContainer extends TestBase {

    @Test
    public void testRetrieveContainer() throws Exception {
        m2m_resource resource = testRetrieve(csebaseuri, "getCSEBase.xml", OK);
        m2m_CSEBase csebase = (m2m_CSEBase) resource;
        for (m2m_childResourceRef ref : csebase.ch) {
            testRetrieve(ref.v, "getContainer.xml", OK);
        }
    }

    @Test
    public void testCreateContainer() throws Exception {
        m2m_resource csebase = testRetrieve(csebaseuri, "getCSEBase.xml", OK);
        String cnt1Uri = testCreate(csebaseuri, container, "createContainer.xml", OK);
        testRetrieve(cnt1Uri, "getContainer.xml", OK);
    }

    @Test
    public void testUpdateContainer() throws Exception {
        String cntUri = testCreate(csebaseuri, container, "createContainer.xml", OK);
        testUpdate(cntUri, "updateContainer.xml", OK);
        testRetrieve(cntUri, "getContainer.xml", OK);
    }

    @Test
    public void testDeleteRecoveredContainer() throws Exception {
        String cntUri = "/csebase/2";
        testRetrieve(cntUri, "getContainer.xml", OK);
        testDelete(cntUri, OK);
        testRetrieve(cntUri, "getContainer.xml", NOT_FOUND);
        Thread.sleep(2000);
    }

    @Test
    public void testDeleteContainer() throws Exception {
        String cntUri = testCreate(csebaseuri, container, "createContainer.xml", OK);
        testDelete(cntUri, OK);
        testRetrieve(cntUri, "getContainer.xml", NOT_FOUND);
    }

    @Test
    public void testCreateSubContainerOnRecoveredContainer() throws Exception {
        String cntUri = "/csebase/2";
        String subcntUri = testCreate(cntUri, container, "createContainer.xml", OK);
        testRetrieve(subcntUri, "getContainer.xml", OK);
        testRetrieve(cntUri, "getContainer.xml", OK);
    }

    @Test
    public void testCreateSubContainerAndContentInstance() throws Exception {
        String cntUri = testCreate(csebaseuri, container, "createContainer.xml", OK);
        String subcntUri = testCreate(cntUri, container, "createContainer.xml", OK);
        String cinUri = testCreate(subcntUri, contentInstance, "createContentInstance.xml", OK);
        testRetrieve(cinUri, OK);
        testRetrieve(subcntUri, OK);
        testRetrieve(cntUri, OK);
    }

    @Test
    public void testVirtualResource() throws Exception {
        String cntUri = testCreate(csebaseuri, container, "createContainer.xml", OK);
        String cinUri = testCreate(cntUri, contentInstance, "createContentInstance.xml", OK);
        String cin2Uri = testCreate(cntUri, contentInstance, "createContentInstance.xml", OK);

        Thread.sleep(1000);//let container update latest

        m2m_resource latest = testRetrieve(cntUri + "/latest", OK);
        Assert.assertEquals(latest.ri, cin2Uri);
        m2m_resource oldest = testRetrieve(cntUri + "/oldest", OK);
        Assert.assertEquals(oldest.ri, cinUri);
    }

    @Test
    public void testVirtualResource2() throws Exception {
        m2m_resource latest = testRetrieve(csebaseuri + "/1/latest", OK);
        System.out.println(latest.ri);
        m2m_resource oldest = testRetrieve(csebaseuri + "/1/oldest", OK);
        System.out.println(oldest.ri);
    }

    @Test
    public void testDeleteVirtualResource() throws Exception {
        String cntUri = testCreate(csebaseuri, container, "createContainer.xml", OK);
        String cinUri = testCreate(cntUri, contentInstance, "createContentInstance.xml", OK);
        Thread.sleep(100);
        for (int i = 0; i < 10; i++) {
            testCreate(cntUri, contentInstance, "createContentInstance.xml", OK);
            Thread.sleep(100);
        }
        String cin1Uri = testCreate(cntUri, contentInstance, "createContentInstance.xml", OK);
        Thread.sleep(100);
        String cin2Uri = testCreate(cntUri, contentInstance, "createContentInstance.xml", OK);

        Thread.sleep(1000);//let container update latest

        m2m_resource latest = testRetrieve(cntUri + "/latest", OK);
        Assert.assertEquals(latest.ri, cin2Uri);
        m2m_resource oldest = testRetrieve(cntUri + "/oldest", OK);
        Assert.assertEquals(oldest.ri, cinUri);

        testDelete(cntUri + "/latest", OK);

        Thread.sleep(1000);//let container update latest

        latest = testRetrieve(cntUri + "/latest", OK);
        Assert.assertEquals(latest.ri, cin1Uri);
        oldest = testRetrieve(cntUri + "/oldest", OK);
        Assert.assertEquals(oldest.ri, cinUri);
    }
}
