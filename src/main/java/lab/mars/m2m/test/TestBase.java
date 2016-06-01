package lab.mars.m2m.test;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.*;
import lab.mars.m2m.launcher.Starter;
import lab.mars.m2m.protocol.common.m2m_childResourceRef;
import lab.mars.m2m.protocol.http.M2MHttpBindings;
import lab.mars.m2m.protocol.http.MissingContentBodyException;
import lab.mars.m2m.protocol.http.MissingParameterException;
import lab.mars.m2m.protocol.primitive.m2m_rsp;
import lab.mars.m2m.protocol.resource.m2m_resource;
import lab.mars.util.network.HttpClient;
import lab.mars.util.network.HttpServer;
import lab.mars.util.network.NetworkEvent;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.concurrent.CountDownLatch;

/**
 * Created by haixiao on 2015/3/23.
 * Email: wumo@outlook.com
 */
public class TestBase {
    public static final String csebaseuri = "/csebase";
    protected static final Logger log = LoggerFactory.getLogger(TestCSEBase.class);
    protected HttpClient client;
    protected HttpServer server;
    private Starter starter;

    @Before
    public void setUp() throws Exception {
        client = new HttpClient();
        server = new HttpServer();
        server.bindAsync("127.0.0.1", 9010)
              .then(future -> { System.out.println("server has started@9010");})
              .<NetworkEvent<FullHttpRequest>>loop(m -> {
                  ByteBuf data = m.msg.content();
                  if (log.isInfoEnabled())
                      log.info("【recv】 notify:\n" + data.toString(Charset.forName("utf-8")) + " eof");
                  HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                  m.ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                  return true;
              });
//        starter = new Starter();
//        starter.start();
    }

    @After
    public void tearDown() throws Exception {
        log.info("shutdown");
//        starter.shutdown();
        server.close();
    }

    public m2m_rsp testRequest(HttpMethod method, String path, HttpResponseStatus statusCode, String contentPath) throws Exception {

        String[][] req_headers = new String[][]{
                {"Host", "/cse01"},
                {"Accept", "application/onem2m-resource+xml"},
                {"Content-type", "application/onem2m-resource+xml"},
                {"From", "/AE01"},
                {"X-M2M-RI", "00001"},
        };
        String requestBody = contentPath != null ? IOUtils.toString(Thread.currentThread().getContextClassLoader().getResource(contentPath)) : null;
        String[][] rsp_headers = new String[][]{
//				{"X-M2M-RI", "00001"},
        };
        if (log.isInfoEnabled())
            log.info("testRequest----");
        return testRequest(method, path, req_headers, requestBody, statusCode, rsp_headers);
    }

    protected String testCreate(String parent_path, int ty, String contentFilePath, HttpResponseStatus statusCode) throws Exception {
        return testCreate(parent_path, ty, null, contentFilePath, statusCode);
    }

    protected String testCreate(String parent_path, int ty, String name, String contentFilePath, HttpResponseStatus statusCode) throws Exception {
        m2m_rsp m_rsp = testRequest(HttpMethod.POST, parent_path + "?ty=" + ty + (name == null ? "" : "&rn=" + name), statusCode, contentFilePath);
        if (m_rsp.pc != null && m_rsp.pc.value instanceof m2m_childResourceRef)
            return ((m2m_childResourceRef) m_rsp.pc.value).v;
        return null;
    }

    protected m2m_resource testRetrieve(String path, HttpResponseStatus statusCode) throws Exception {
        return testRetrieve(path, null, statusCode);
    }

    protected m2m_resource testRetrieve(String path, String contentFilePath, HttpResponseStatus statusCode) throws Exception {
        m2m_rsp m_rsp = testRequest(HttpMethod.GET, path, statusCode, contentFilePath);
        if (m_rsp.pc != null && m_rsp.pc.value instanceof m2m_resource)
            return ((m2m_resource) m_rsp.pc.value);
        return null;
    }

    protected void testUpdate(String path, String contentFilePath, HttpResponseStatus statusCode) throws Exception {
        m2m_rsp m_rsp = testRequest(HttpMethod.PUT, path, statusCode, contentFilePath);
    }

    protected void testDelete(String path, HttpResponseStatus statusCode) throws Exception {
        m2m_rsp m_rsp = testRequest(HttpMethod.DELETE, path, statusCode, null);
    }

    protected m2m_rsp testRequest(
            HttpMethod method,
            String path,
            String[][] req_headers,
            String requestBody,
            HttpResponseStatus status,
            String[][] rsp_headers) throws InterruptedException, IOException, URISyntaxException {

        CountDownLatch latchNami = new CountDownLatch(1);
        URI uri = new URI("http://localhost:8081");
        HttpRequest httpRequest = HttpClient.makeRequest(method, path, req_headers, requestBody);
        m2m_rsp m_rsp[] = new m2m_rsp[1];
        client.requestAsync(uri, httpRequest)
              .<NetworkEvent<FullHttpResponse>>then(resp -> {
                  Assert.assertEquals(status, resp.msg.getStatus());
                  if (rsp_headers != null)
                      for (String[] header : rsp_headers)
                          Assert.assertEquals(header[1], resp.msg.headers().get(header[0]));
                  try {
                      if (log.isInfoEnabled())
                          System.out.println(resp.msg.content().toString(Charset.forName("utf-8")));
                      m_rsp[0] = M2MHttpBindings.decodeResponse(resp.msg);
                  } catch (JAXBException | MissingParameterException | MissingContentBodyException e) {
                      e.printStackTrace();
                  }
                  latchNami.countDown();
              });

        latchNami.await();
        return m_rsp[0];
    }
}
