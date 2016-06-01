package lab.mars.m2m.test;

import lab.mars.m2m.nami.core.Nami;
import lab.mars.m2m.nami.core.cluster.Phone;
import lab.mars.m2m.persistence.impl.DBCassandraAgent;
import lab.mars.m2m.protocol.http.HttpInBoundAgent;
import lab.mars.m2m.protocol.http.HttpOutBoundAgent;
import lab.mars.m2m.recover.BootstrapAgent;
import lab.mars.m2m.util.Setting;
import lab.mars.server.WebServerProxy;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import static lab.mars.m2m.nami.core.Nami.addLocal;
import static lab.mars.m2m.nami.core.Nami.staticAddLocal;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static lab.mars.m2m.nami.core.Nami.addLocal;
import static lab.mars.m2m.nami.core.Nami.staticAddLocal;

/**
 * Created by haixiao on 2015/3/23.
 * Email: wumo@outlook.com
 */
public class TestProxy {
    private CountDownLatch latch = new CountDownLatch(1);
    private static Logger log = LoggerFactory.getLogger(TestProxy.class);

    public TestProxy() {
        init(false);
    }

    public void init(boolean clean) {
        Nami.init();
        Setting.init(Phone.getLocalId(), Phone.getLocalId(), Phone.getLocalId(), Phone.getLocalId(), Phone.getLocalId(), Phone.getLocalId());
        staticAddLocal(Setting.database_addr.toString(), new DBCassandraAgent(clean));
        staticAddLocal(Setting.bootstrap_addr.toString(), new BootstrapAgent(() -> {
            addLocal(Setting.httpInbound_addr.toString(), new HttpInBoundAgent());
            addLocal(Setting.outbound_addr.toString(), new HttpOutBoundAgent());
            addLocal(Setting.webServer_addr.toString(), new WebServerProxy());
            latch.countDown();
        }));
    }

    @Test
    public void test() {
        new TestProxy().start(); //true 新建表
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void logTest() {
        log.trace("======trace");
        log.debug("======debug");
        log.info("======info");
        log.warn("======warn");
        log.error("======error");
    }

    public void start() {
        Nami.start();
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        Nami.shutdown();
    }
}


