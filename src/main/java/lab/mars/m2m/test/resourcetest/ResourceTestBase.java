package lab.mars.m2m.test.resourcetest;

/**
 * Author:yaoalong.
 * Date:2016/5/25.
 * Email:yaoalong@foxmail.com
 */

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.*;
import lab.mars.m2m.protocol.common.m2m_AnyURIList;
import lab.mars.m2m.protocol.common.m2m_childResourceRef;
import lab.mars.m2m.protocol.common.m2m_eventNotificationCriteria;
import lab.mars.m2m.protocol.enumeration.m2m_resourceStatus;
import lab.mars.m2m.protocol.http.M2MHttpBindings;
import lab.mars.m2m.protocol.http.MissingContentBodyException;
import lab.mars.m2m.protocol.http.MissingParameterException;
import lab.mars.m2m.protocol.primitive.m2m_primitiveContentType;
import lab.mars.m2m.protocol.primitive.m2m_req;
import lab.mars.m2m.protocol.primitive.m2m_rsp;
import lab.mars.m2m.protocol.resource.*;
import lab.mars.m2m.reflection.ResourceReflection;
import lab.mars.m2m.test.TestCSEBase;
import lab.mars.util.async.AsyncStream;
import lab.mars.util.network.HttpClient;
import lab.mars.util.network.HttpServer;
import lab.mars.util.network.NetworkEvent;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static lab.mars.m2m.protocol.enumeration.m2m_resourceType.*;

/**
 * Created by haixiao on 2015/3/23.
 * Email: wumo@outlook.com
 */
public class ResourceTestBase {
    public static final int ASYNC = 1;
    public static final int SYNC = 0;
    protected static final String csebaseuri = "/csebase";
    protected static final Logger log = LoggerFactory.getLogger(TestCSEBase.class);
    public static ThreadLocal<Marshaller> marshaller;
    protected static HttpClient client;
    protected static HttpServer server;
    static JAXBContext jc = null;
    static ThreadLocal<Unmarshaller> unmarshaller;
    private static int port = 10000;
    private static int ip = 0;
    private static String[] ipAddr;
    protected String myIp = "192.168.10.207";
    protected String serverIp = "192.168.10.208";
    protected AtomicLong senderCount;
    protected AtomicLong receiveCount;
    protected Integer maxCount;
    protected AtomicLong recvTime;
    protected boolean isReality = false;
    protected AtomicLong dd = new AtomicLong(0);

    public void setUp() throws Exception {
        client = new HttpClient();
        server = new HttpServer();
        List<String> servers = new ArrayList<>();
      //    servers.add(myIp);
        for (int i = 1; i < 10; i++) {
            servers.add("192.168.10.17" + i);
        }
        for (int i = 0; i < 10; i++) {
            servers.add("192.168.10.18" + i);
        }
        ipAddr = servers.toArray(new String[servers.size()]);
        server.bindAsync(myIp, 9010)
                .then(future -> {
                    System.out.println("server has started@9010");
                })
                .<NetworkEvent<FullHttpRequest>>loop(m -> {
                    ByteBuf data = m.msg.content();
                    try {
                        System.out.println("开始");
                        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                        m.ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                        System.out.println("结束");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, OK);
                    //  m.ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                    return true;
                });
        marshaller = new ThreadLocal<Marshaller>() {
            @Override
            public Marshaller initialValue() {
                try {
                    Marshaller marshaller = jc.createMarshaller();
                    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                    return marshaller;
                } catch (JAXBException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        unmarshaller = new ThreadLocal<Unmarshaller>() {
            @Override
            public Unmarshaller initialValue() {
                try {
                    return jc.createUnmarshaller();
                } catch (JAXBException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        try {
            jc = JAXBContext.newInstance(m2m_primitiveContentType.class, m2m_req.class, m2m_rsp.class);
        } catch (JAXBException e) {
            e.printStackTrace();
        }

        // ExecutorService executorService= Executors.newFixedThreadPool(1);
        // for(int i=0;i<1;i++){
        //     executorService.submit(new AddResoureThread(this));
        // }
        //  Thread.sleep(1000);
        //executorService.shutdownNow();
        handleBefore();
    }

    public void handleBefore() {

    }

    public void handleNotify(m2m_childResourceRef ref) {

    }

    public void tearDown() throws Exception {
        log.info("shutdown");
        server.close();
    }

    public m2m_rsp testRequest(HttpMethod method, String path, HttpResponseStatus statusCode, String content, int flag) throws Exception {

        String[][] req_headers = new String[][]{
                {"Host", "/cse01"},
                {"Accept", "application/onem2m-resource+xml"},
                {"Content-type", "application/onem2m-resource+xml"},
                {"From", "/AE01"},
                {"X-M2M-RI", "00001"},
        };
        String requestBody = content;
        String[][] rsp_headers = new String[][]{
//				{"X-M2M-RI", "00001"},
        };
        if (flag == SYNC) {
            return syncTestRequest(method, path, req_headers, requestBody, statusCode, rsp_headers);
        }
        testRequest(method, path, req_headers, requestBody, statusCode, rsp_headers);
        return null;
    }

    public String testCreate(String parent_path, int ty, String contentFilePath, HttpResponseStatus statusCode, int flag) throws Exception {
        return testCreate(parent_path, ty, null, contentFilePath, statusCode, flag);
    }


    protected String testCreate(String parent_path, int ty, String name, String content, HttpResponseStatus statusCode, int flag) throws Exception {
        m2m_rsp m_rsp = testRequest(HttpMethod.POST, parent_path + "?ty=" + ty + (name == null ? "" : "&rn=" + name), statusCode, content, flag);
        if (flag == ASYNC) {
            return null;
        }
        if (m_rsp.pc != null && m_rsp.pc.value instanceof m2m_childResourceRef)
            return ((m2m_childResourceRef) m_rsp.pc.value).v;
        return null;
    }

    public m2m_resource testSyncRetrieve(String path, HttpResponseStatus statusCode) throws Exception {
        return testRetrieve(path, null, statusCode, SYNC);
    }

    public m2m_resource testRetrieve(String path, HttpResponseStatus statusCode) throws Exception {
        return testRetrieve(path, null, statusCode, ASYNC);
    }

    public m2m_resource testRetrieve(String path, String contentFilePath, HttpResponseStatus statusCode, int flag) throws Exception {

        if (flag == SYNC) {
            m2m_rsp m_rsp = testRequest(HttpMethod.GET, path, statusCode, contentFilePath, 0);
            if (m_rsp.pc != null && m_rsp.pc.value instanceof m2m_resource)
                return ((m2m_resource) m_rsp.pc.value);
        }

        testRequest(HttpMethod.GET, path, statusCode, contentFilePath, ASYNC);
        return null;
    }

    protected void testUpdate(String path, String contentFilePath, HttpResponseStatus statusCode, int flag) throws Exception {
        m2m_rsp m_rsp = testRequest(HttpMethod.PUT, path, statusCode, contentFilePath, flag);
    }

    public void testDelete(String path, HttpResponseStatus statusCode) throws Exception {
        testRequest(HttpMethod.DELETE, path, statusCode, null, ASYNC);
    }

    protected AsyncStream testRequest(
            HttpMethod method,
            String path,
            String[][] req_headers,
            String requestBody,
            HttpResponseStatus status,
            String[][] rsp_headers) throws InterruptedException, IOException, URISyntaxException {

        URI uri = new URI("http://" + serverIp + ":8081");
        HttpRequest httpRequest = HttpClient.makeRequest(method, path, req_headers, requestBody);
        InetSocketAddress inetSocketAddress = nextAddr();
        return client.requestAsync(uri, httpRequest,inetSocketAddress)
                .<NetworkEvent<FullHttpResponse>>then(resp -> {
                    if (!isReality && senderCount != null) {
                        if(resp==null){
                            System.out.println("inet::" + inetSocketAddress.toString());
                        }
//                        System.out.println("inet::" + inetSocketAddress.toString());
//                        System.out.println("cost time:" + (resp.arriveTime));
//                        System.out.println("时间为" + (recvTime.getAndAdd(resp.arriveTime)));
             //       System.out.println("Haha" + (resp == null) + "count:" + dd.getAndIncrement());
                    }
                }).end();
    }

    /**
     * 异步更新AE
     *
     * @throws Exception
     */
    public void updateAsyncAEResource(String aeURI) throws Exception {
        m2m_primitiveContentType m2m_primitiveContentType = new m2m_primitiveContentType();
        StringWriter sw = new StringWriter();
        m2m_AE rsp = new m2m_AE();
        m2m_primitiveContentType.value = rsp;
        marshaller.get().marshal(m2m_primitiveContentType, sw);
        String value = sw.toString();
        testUpdate(aeURI, value, OK, ASYNC);//创建一个AE
    }

    /**
     * 异步更新Container
     *
     * @throws Exception
     */
    public void updateAsyncContainer(String containerURI) throws Exception {
        StringWriter sw = new StringWriter();
        m2m_primitiveContentType m2m_primitiveContentType = new m2m_primitiveContentType();
        m2m_Container m2m_container = new m2m_Container();
        m2m_primitiveContentType.value = m2m_container;
        marshaller.get().marshal(m2m_primitiveContentType, sw);
        testUpdate(containerURI, sw.toString(), OK, ASYNC);//创建一个container
    }

    /**
     * 异步更新ContentInstance
     *
     * @throws Exception
     */
    public void updateAsyncContentInstance(String contentInstance) throws Exception {
        StringWriter sw = new StringWriter();
        m2m_primitiveContentType m2m_primitiveContentType = new m2m_primitiveContentType();
        m2m_ContentInstance m2m_contentInstance = new m2m_ContentInstance();
        m2m_primitiveContentType.value = m2m_contentInstance;
        marshaller.get().marshal(m2m_primitiveContentType, sw);
        testUpdate(contentInstance, sw.toString(), OK, ASYNC);//创建一个container
    }

    public void updateAsyncSubScriptions(String subscriptionURI) throws Exception {
        StringWriter sw = new StringWriter();
        m2m_primitiveContentType m2m_primitiveContentType = new m2m_primitiveContentType();
        m2m_Subscription m2m_subscription = new m2m_Subscription();
        m2m_eventNotificationCriteria m2m_eventNotificationCriteria = new m2m_eventNotificationCriteria();
        List<Integer> integerList = new ArrayList<>();
        integerList.add(m2m_resourceStatus.childCreated);
        m2m_eventNotificationCriteria.rss = integerList;
        m2m_subscription.enc = m2m_eventNotificationCriteria;
        m2m_AnyURIList m2m_anyURIList = new m2m_AnyURIList();
        List<String> reference = new ArrayList<>();
        reference.add("http://" + myIp + ":9010/");
        m2m_anyURIList.reference = reference;
        m2m_subscription.nu = m2m_anyURIList;
        m2m_primitiveContentType.value = m2m_subscription;
        marshaller.get().marshal(m2m_primitiveContentType, sw);
        testUpdate(subscriptionURI, sw.toString(), OK, ASYNC);
    }

    public String createSyncAEResource() throws Exception {
        m2m_primitiveContentType m2m_primitiveContentType = new m2m_primitiveContentType();
        StringWriter sw = new StringWriter();
        m2m_AE rsp = new m2m_AE();
        m2m_primitiveContentType.value = rsp;
        marshaller.get().marshal(m2m_primitiveContentType, sw);
        String value = sw.toString();
        return testCreate(csebaseuri, AE, value, OK, SYNC);//创建一个AE
    }

    public void createAsyncAEResource() throws Exception {
        m2m_primitiveContentType m2m_primitiveContentType = new m2m_primitiveContentType();
        StringWriter sw = new StringWriter();
        m2m_AE rsp = new m2m_AE();
        m2m_primitiveContentType.value = rsp;
        marshaller.get().marshal(m2m_primitiveContentType, sw);
        String value = sw.toString();
        testCreate(csebaseuri, AE, value, OK, ASYNC);//创建一个AE
    }

    /**
     * 异步创建Container
     *
     * @throws Exception
     */
    public void createAsyncContainer(String aeURI) throws Exception {
        StringWriter sw = new StringWriter();
        m2m_primitiveContentType m2m_primitiveContentType = new m2m_primitiveContentType();
        m2m_Container m2m_container = new m2m_Container();
        m2m_primitiveContentType.value = m2m_container;
        marshaller.get().marshal(m2m_primitiveContentType, sw);
        testCreate(aeURI, container, sw.toString(), OK, ASYNC);//创建一个container
    }

    /**
     * 异步创建Container
     *
     * @throws Exception
     */
    public String createSyncContainer(String aeURI) throws Exception {
        StringWriter sw = new StringWriter();
        m2m_primitiveContentType m2m_primitiveContentType = new m2m_primitiveContentType();
        m2m_Container m2m_container = new m2m_Container();
        m2m_primitiveContentType.value = m2m_container;
        marshaller.get().marshal(m2m_primitiveContentType, sw);
        return testCreate(aeURI, container, sw.toString(), OK, SYNC);//创建一个container
    }

    /**
     * 异步创建ContainerInstance
     *
     * @throws Exception
     */
    public void createAsyncContainerInstance(String cntURI) throws Exception {
        StringWriter sw = new StringWriter();
        m2m_primitiveContentType m2m_primitiveContentType = new m2m_primitiveContentType();
        m2m_ContentInstance m2m_contentInstance = new m2m_ContentInstance();
        m2m_contentInstance.con = ResourceReflection.serializeKryo("1".getBytes());
        m2m_primitiveContentType.value = m2m_contentInstance;
        marshaller.get().marshal(m2m_primitiveContentType, sw);
        testCreate(cntURI, contentInstance, sw.toString(), OK, ASYNC);
    }

    public void createAsyncSubScriptions(String cntURI) throws Exception {
        StringWriter sw = new StringWriter();
        m2m_primitiveContentType m2m_primitiveContentType = new m2m_primitiveContentType();
        m2m_Subscription m2m_subscription = new m2m_Subscription();
        m2m_eventNotificationCriteria m2m_eventNotificationCriteria = new m2m_eventNotificationCriteria();
        List<Integer> integerList = new ArrayList<>();
        integerList.add(m2m_resourceStatus.childCreated);
        m2m_eventNotificationCriteria.rss = integerList;
        m2m_subscription.enc = m2m_eventNotificationCriteria;
        m2m_AnyURIList m2m_anyURIList = new m2m_AnyURIList();
        List<String> reference = new ArrayList<>();
        reference.add("http://" + myIp + ":9010/");
        m2m_anyURIList.reference = reference;
        m2m_subscription.nu = m2m_anyURIList;
        m2m_primitiveContentType.value = m2m_subscription;
        marshaller.get().marshal(m2m_primitiveContentType, sw);
        testCreate(cntURI, subscription, sw.toString(), OK, ASYNC);
    }

    /**
     * 同步创建ContainerInstance
     *
     * @throws Exception
     */
    public String createSyncContentInstance(String cntURI) throws Exception {
        StringWriter sw = new StringWriter();
        m2m_primitiveContentType m2m_primitiveContentType = new m2m_primitiveContentType();
        m2m_ContentInstance m2m_contentInstance = new m2m_ContentInstance();
        m2m_contentInstance.con = ResourceReflection.serializeKryo("1".getBytes());
        m2m_primitiveContentType.value = m2m_contentInstance;
        marshaller.get().marshal(m2m_primitiveContentType, sw);
        return testCreate(cntURI, contentInstance, sw.toString(), OK, SYNC);
    }

    /**
     * 同步创建subscription
     *
     * @param subScrption
     * @throws Exception
     */
    public String createSyncSubScription(String subScrption) throws Exception {
        StringWriter sw = new StringWriter();
        m2m_primitiveContentType m2m_primitiveContentType = new m2m_primitiveContentType();
        m2m_Subscription m2m_subscription = new m2m_Subscription();
        m2m_eventNotificationCriteria m2m_eventNotificationCriteria = new m2m_eventNotificationCriteria();
        List<Integer> integerList = new ArrayList<>();
        integerList.add(m2m_resourceStatus.childCreated);
        m2m_eventNotificationCriteria.rss = integerList;
        m2m_subscription.enc = m2m_eventNotificationCriteria;
        m2m_AnyURIList m2m_anyURIList = new m2m_AnyURIList();
        List<String> reference = new ArrayList<>();
        reference.add("http://" + myIp + ":9010/");
        m2m_anyURIList.reference = reference;
        m2m_subscription.nu = m2m_anyURIList;
        m2m_primitiveContentType.value = m2m_subscription;
        marshaller.get().marshal(m2m_primitiveContentType, sw);
        return testCreate(subScrption, subscription, sw.toString(), OK, SYNC);
    }

    protected m2m_rsp syncTestRequest(
            HttpMethod method,
            String path,
            String[][] req_headers,
            String requestBody,
            HttpResponseStatus status,
            String[][] rsp_headers) throws InterruptedException, IOException, URISyntaxException {

        CountDownLatch latchNami = new CountDownLatch(1);
        URI uri = new URI("http://" + serverIp + ":8081");
        HttpRequest httpRequest = HttpClient.makeRequest(method, path, req_headers, requestBody);
        m2m_rsp m_rsp[] = new m2m_rsp[1];
        InetSocketAddress inetSocketAddress = nextAddr();
        client.requestAsync(uri, httpRequest, inetSocketAddress)
                .<NetworkEvent<FullHttpResponse>>then(resp -> {
                    Assert.assertNotNull(status);
                    System.out.println("inet::" + inetSocketAddress.toString());
                    Assert.assertNotNull(resp.msg);
                    Assert.assertEquals(status, resp.msg.getStatus());
                    if (rsp_headers != null)
                        for (String[] header : rsp_headers)
                            Assert.assertEquals(header[1], resp.msg.headers().get(header[0]));
                    try {
                        if (log.isInfoEnabled())
                            m_rsp[0] = M2MHttpBindings.decodeResponse(resp.msg);

                    } catch (JAXBException | MissingParameterException | MissingContentBodyException e) {
                        e.printStackTrace();
                    }
                    latchNami.countDown();
                });
        latchNami.await();
        return m_rsp[0];
    }

    private synchronized InetSocketAddress nextAddr() {
        if (port > 65530) {
            ip = (ip + 1) % ipAddr.length;
            port = 10000;
        }
        return new InetSocketAddress(ipAddr[ip], port++);
    }
}