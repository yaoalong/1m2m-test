package lab.mars.m2m.test;

import io.netty.handler.codec.http.HttpMethod;
import lab.mars.m2m.protocol.primitive.m2m_rsp;
import org.junit.Test;

import static io.netty.handler.codec.http.HttpResponseStatus.*;

/**
 * Created by haixiao on 2015/1/5.
 * test
 */
public class TestAllInOne extends TestBase {
	@Test
	public void testNonExist() throws Exception {
		testRequest(HttpMethod.GET, "/csebase000000000001", OK, "getCSEBase.xml");
		testRequest(HttpMethod.POST, "/csebase000000000001?ty=3&rn=container01", FORBIDDEN, "createContainer.xml");
		testRequest(HttpMethod.POST, "/csebase000000000001/container01?ty=4&rn=contentInstance" + 1, BAD_REQUEST, "createContentInstance.xml");
		testRequest(HttpMethod.GET, "/csebase000000000001/container01", BAD_REQUEST, "getContainerVirtual.xml");
	}

	@Test
	public void testVirtualResource() throws Exception {
		testRequest(HttpMethod.GET, "/csebase001000001", OK, "getCSEBase.xml");
		testRequest(HttpMethod.POST, "/csebase001000001?ty=3&rn=container01", OK, "createContainer.xml");
		testRequest(HttpMethod.POST, "/csebase001000001/container01?ty=4&rn=contentInstance" + 1, OK, "createContentInstance.xml");
		testRequest(HttpMethod.GET, "/csebase001000001/container01", OK, "getContainerVirtual.xml");
	}

	@Test
	public void testSubContainer_Create_ContentInstance() throws Exception {
		testRequest(HttpMethod.GET, "/csebase001000001", OK, "getCSEBase.xml");

		testRequest(HttpMethod.POST, "/csebase001000001?ty=3&rn=container01", OK, "createContainer.xml");
		testRequest(HttpMethod.POST, "/csebase001000001/container01?ty=3&rn=container03", OK, "createContainer.xml");

		testRequest(HttpMethod.GET, "/csebase001000001", OK, "getCSEBase.xml");
		testRequest(HttpMethod.GET, "/csebase001000001/container01", OK, "getContainer.xml");
		testRequest(HttpMethod.GET, "/csebase001000001/container01/container03", OK, "getContainer.xml");

		for (int i = 0; i < 10; i++) {
			testRequest(HttpMethod.POST, "/csebase001000001/container01?ty=4&rn=contentInstance01-" + i, OK, "createContentInstance.xml");
		}
		for (int i = 0; i < 10; i++) {
			testRequest(HttpMethod.POST, "/csebase001000001/container01/container03?ty=4&rn=contentInstance03-" + i, OK,
			            "createContentInstance.xml");
		}

		testRequest(HttpMethod.GET, "/csebase001000001/container01/contentInstance01-1", OK, "getContentInstance.xml");
		testRequest(HttpMethod.GET, "/csebase001000001/container01", OK, "getContainer.xml");
		testRequest(HttpMethod.GET, "/csebase001000001/container01", OK, "getContainerVirtual.xml");

		testRequest(HttpMethod.GET, "/csebase001000001/container01/container03", OK, "getContainer.xml");
		testRequest(HttpMethod.GET, "/csebase001000001/container01/container03", OK, "getContainerVirtual.xml");

		testRequest(HttpMethod.DELETE, "/csebase001000001/container01/contentInstance01-1", OK, null);
		testRequest(HttpMethod.GET, "/csebase001000001/container01", OK, "getContainer.xml");
	}

	@Test
	public void testSubscription_notify_when_update() throws Exception {
		testRequest(HttpMethod.GET, "/csebase001000001", OK, "getCSEBase.xml");

		testRequest(HttpMethod.POST, "/csebase001000001?ty=3&rn=container01", OK, "createContainer.xml");
		testRequest(HttpMethod.POST, "/csebase001000001/container01?ty=3&rn=container02", OK, "createContainer.xml");

		testRequest(HttpMethod.GET, "/csebase001000001", OK, "getCSEBase.xml");
		testRequest(HttpMethod.GET, "/csebase001000001/container01", OK, "getContainer.xml");

		testRequest(HttpMethod.POST, "/csebase001000001/container01?ty=22&rn=subscription1", OK, "createSubscription.xml");
		testRequest(HttpMethod.POST, "/csebase001000001/container01/container02?ty=22&rn=subscription2", OK, "createSubscription.xml");
		testRequest(HttpMethod.GET, "/csebase001000001/container01/subscription1", OK, null);
		testRequest(HttpMethod.GET, "/csebase001000001/container01/container02/subscription2", OK, null);

		for (int i = 0; i < 3; i++) {
			testRequest(HttpMethod.POST, "/csebase001000001/container01?ty=4&rn=contentInstance01-" + i, OK, "createContentInstance.xml");
		}

		for (int i = 0; i < 3; i++) {
			testRequest(HttpMethod.POST, "/csebase001000001/container01/container02?ty=4&rn=contentInstance02-" + i, OK,
			            "createContentInstance.xml");
		}

		testRequest(HttpMethod.GET, "/csebase001000001/container01/contentInstance01-1", OK, "getContentInstance.xml");
		testRequest(HttpMethod.GET, "/csebase001000001/container01", OK, "getContainer.xml");
		testRequest(HttpMethod.GET, "/csebase001000001/container01", OK, "getContainerVirtual.xml");//get latest

		testRequest(HttpMethod.DELETE, "/csebase001000001/container01/contentInstance01-1", OK, null);
		testRequest(HttpMethod.GET, "/csebase001000001/container01", OK, "getContainer.xml");

		testRequest(HttpMethod.DELETE, "/csebase001000001/container01/container02/contentInstance02-1", OK, null);
		testRequest(HttpMethod.DELETE, "/csebase001000001/container01/container02", OK, null);

		testRequest(HttpMethod.DELETE, "/csebase001000001/container01", OK, null);

		//		testRequest(HttpMethod.DELETE, "/csebase001000001/container01/subscription1",OK, null);
	}

	@Test
	public void testSubscription_notify_when_update2() throws Exception {
		testRequest(HttpMethod.GET, "/csebase001000001", OK, "getCSEBase.xml");
		testRequest(HttpMethod.POST, "/csebase001000001?ty=22&rn=subscription1", OK, "createSubscription.xml");

		testRequest(HttpMethod.POST, "/csebase001000001?ty=3&rn=container01", OK, "createContainer.xml");
		testRequest(HttpMethod.GET, "/csebase001000001/container01", OK, "getContainer.xml");
		testRequest(HttpMethod.DELETE, "/csebase001000001/container01", OK, null);

		//		testRequest(HttpMethod.DELETE, "/csebase001000001/container01/subscription1",OK, null);
	}

	@Test
	public void testCRUD() throws Exception {
		testRequest(HttpMethod.GET, "/csebase001000001", OK, "getCSEBase.xml");

		testRequest(HttpMethod.POST, "/csebase001000001?ty=3&rn=container01", OK, "createContainer.xml");
		testRequest(HttpMethod.POST, "/csebase001000001?ty=3&rn=container02", OK, "createContainer.xml");
		testRequest(HttpMethod.POST, "/csebase001000001/container01?ty=3&rn=container03", OK, "createContainer.xml");

		testRequest(HttpMethod.GET, "/csebase001000001", OK, "getCSEBase.xml");
		testRequest(HttpMethod.GET, "/csebase001000001/container01", OK, "getContainer.xml");
		testRequest(HttpMethod.GET, "/csebase001000001/container01/container03", OK, "getContainer.xml");

		testRequest(HttpMethod.PUT, "/csebase001000001/container01", OK, "updateContainer.xml");
		testRequest(HttpMethod.GET, "/csebase001000001/container01", OK, "getContainer.xml");

		testRequest(HttpMethod.DELETE, "/csebase001000001/container01", OK, "getContainer.xml");
		testRequest(HttpMethod.GET, "/csebase001000001", OK, "getCSEBase.xml");

		testRequest(HttpMethod.GET, "/csebase001000001/container01/container03", BAD_REQUEST, "getContainer.xml");

		for (int i = 0; i < 100; i++) {
			testRequest(HttpMethod.POST, "/csebase001000001/container02?ty=4&rn=contentInstance" + i, OK, "createContentInstance.xml");
		}
		testRequest(HttpMethod.POST, "/csebase001000001?ty=3&rn=container04", OK, "createContainer.xml");
		for (int i = 0; i < 100; i++) {
			testRequest(HttpMethod.POST, "/csebase001000001/container04?ty=4&rn=contentInstance0" + i, OK, "createContentInstance.xml");
		}
		//		testRequest(HttpMethod.POST, "/container02?ty=4&rn=contentInstance1",OK, "createContentInstance.xml");
		//		testRequest(HttpMethod.POST, "/container02?ty=4&rn=contentInstance2",OK, "createContentInstance.xml");

		testRequest(HttpMethod.GET, "/csebase001000001/container04/contentInstance01", OK, null);
		testRequest(HttpMethod.GET, "/csebase001000001/container02", OK, "getContainer.xml");

		testRequest(HttpMethod.GET, "/csebase001000001/container02", OK, "getContainerVirtual.xml");

		testRequest(HttpMethod.DELETE, "/csebase001000001/container04/contentInstance01", OK, null);
		testRequest(HttpMethod.GET, "/csebase001000001/container02", OK, "getContainer.xml");

		//		Thread.sleep(5000);
	}

	@Test
	public void testRetrieveType() {
		try {
//			m2m_rsp req = testRequest(HttpMethod.GET, "/csebase/2", OK, "getType.xml");
			m2m_rsp req = testRequest(HttpMethod.GET, "/csebase", OK, "getType.xml");
			int x = 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
