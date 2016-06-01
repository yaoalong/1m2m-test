package lab.mars.m2m.test;

import org.junit.Test;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static lab.mars.m2m.protocol.enumeration.m2m_resourceType.*;

/**
 * Created by haixiao on 2015/1/5.
 * test
 */
public class TestSubscription extends TestBase {
    @Test
    public void testCreateSubscription() throws Exception {
        String resource = testCreate(csebaseuri, subscription, "createSubscription.xml", OK);
        testRetrieve(resource, OK);
        testCreate(csebaseuri, container, "createContainer.xml", OK);
        Thread.sleep(2000);
    }
}
