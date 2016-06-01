package lab.mars.m2m.test;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import lab.mars.m2m.protocol.common.m2m_AnyURIList;
import lab.mars.m2m.protocol.primitive.m2m_rsp;
import lab.mars.m2m.protocol.resource.m2m_resource;
import org.junit.Assert;
import org.junit.Test;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static lab.mars.m2m.protocol.enumeration.m2m_resourceType.AE;
import static lab.mars.m2m.protocol.enumeration.m2m_resourceType.container;
import static lab.mars.m2m.protocol.enumeration.m2m_resourceType.contentInstance;

/**
 * User: Gxkl
 * Time: 2015.12.29
 * Copyright © Gxkl. All Rights Reserved.
 */
public class TestResourceDiscovery extends TestBase {

    @Test
    public void test_discoveryAll() throws Exception {
        String aeUri = testCreate(csebaseuri, AE, "createAE.xml", OK);
        String cntUri = testCreate(csebaseuri, container, "createContainer.xml", OK);
        String cinUri = testCreate(cntUri, contentInstance, "createContentInstance.xml", OK);
        String url = csebaseuri + "?fu=discovery";
        m2m_AnyURIList cin = testDiscovery(url, null, OK);
//        Assert.assertArrayEquals(new String[]{"/csebase/2", "/csebase/1"}, cin.reference.toArray());
    }

    @Test
    public void test_discoveryByLabel() throws Exception {
        String aeUri = testCreate(csebaseuri, AE, "createAE.xml", OK);
        String cntUri = testCreate(csebaseuri, container, "createContainer.xml", OK);
        String cinUri = testCreate(cntUri, contentInstance, "createContentInstance.xml", OK);
        String url = csebaseuri + "?lbl=label2&fu=discovery";
        m2m_AnyURIList cin = testDiscovery(url, null, OK);
//        Assert.assertArrayEquals(new String[]{"/csebase/2", "/csebase/1"}, cin.reference.toArray());
    }

    @Test
    public void test_discoveryByLim() throws Exception {
        String aeUri = testCreate(csebaseuri, AE, "createAE.xml", OK);
        aeUri = testCreate(csebaseuri, AE, "createAE.xml", OK);
        String cntUri = testCreate(csebaseuri, container, "createContainer.xml", OK);
        String cinUri = testCreate(cntUri, contentInstance, "createContentInstance.xml", OK);
        String url = csebaseuri + "?lim=2&fu=discovery";
        m2m_AnyURIList cin = testDiscovery(url, null, OK);
        Assert.assertEquals(2, cin.reference.size());
    }

    @Test
    public void test_discoveryByMultipleFC() throws Exception {
        String aeUri = testCreate(csebaseuri, AE, "createAE.xml", OK);
        aeUri = testCreate(csebaseuri, AE, "createAE.xml", OK);
        String cntUri = testCreate(csebaseuri, container, "createContainer.xml", OK);
        String cinUri = testCreate(cntUri, contentInstance, "createContentInstance.xml", OK);
        String url = csebaseuri + "?lbl=label2&lbl=label3&lim=11&fu=discovery";
        m2m_AnyURIList cin = testDiscovery(url, null, OK);
        Assert.assertEquals(1, cin.reference.size());
    }

    protected m2m_AnyURIList testDiscovery(String path, String contentFilePath, HttpResponseStatus statusCode) throws Exception {
        m2m_rsp m_rsp = testRequest(HttpMethod.GET, path, statusCode, contentFilePath);
        if (m_rsp.pc != null && m_rsp.pc.value instanceof m2m_AnyURIList)
            return ((m2m_AnyURIList) m_rsp.pc.value);
        return null;
    }
}
