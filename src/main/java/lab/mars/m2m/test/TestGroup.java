package lab.mars.m2m.test;

import org.junit.Test;

import static lab.mars.m2m.protocol.enumeration.m2m_resourceType.contentInstance;
import static lab.mars.m2m.protocol.enumeration.m2m_resourceType.group;
import static lab.mars.m2m.test.TestBase.csebaseuri;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;

/**
 * User: Gxkl
 * Time: 2016.3.31
 * Copyright © Gxkl. All Rights Reserved.
 */
public class TestGroup extends TestBase {

    @Test
    public void testCreateGroup() throws Exception {
        String cinUri = testCreate(csebaseuri, group, "createGroup.xml", OK);
        testRetrieve(cinUri, "getContentInstance.xml", OK);
    }

    @Test
    public void testUpdateGroup() throws Exception {
        testUpdate("/csebase/4", "updateGroup,xml", OK);
    }

    @Test
    public void testFanOut() throws Exception {
        testCreate("/csebase/4/fanOutPoint", contentInstance, "createFanOut.xml", OK);
    }

}
