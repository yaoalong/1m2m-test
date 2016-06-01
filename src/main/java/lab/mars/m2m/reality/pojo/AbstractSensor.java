package lab.mars.m2m.reality.pojo;

import lab.mars.m2m.protocol.primitive.m2m_primitiveContentType;
import lab.mars.m2m.protocol.resource.m2m_ContentInstance;
import lab.mars.m2m.reflection.ResourceReflection;
import lab.mars.m2m.test.resourcetest.ResourceTestBase;
import lab.mars.onem2m.pojo.NotificationUtils;

import javax.xml.bind.JAXBException;
import java.io.Serializable;
import java.io.StringWriter;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static lab.mars.m2m.protocol.enumeration.m2m_resourceType.contentInstance;
import static lab.mars.m2m.test.resourcetest.ResourceTestBase.ASYNC;

/**
 * Author:yaoalong.
 * Date:2016/4/25.
 * Email:yaoalong@foxmail.com
 */
public abstract class AbstractSensor implements SensorObject, Serializable {

    protected ResourceTestBase resourceTestBase;
    protected String cntUri;
    protected String machineUri;
    private long id;

    public void request(AbstractSensor sensor) {
        m2m_primitiveContentType m2m_primitiveContentType = new m2m_primitiveContentType();
        m2m_ContentInstance m2m_contentInstance = new m2m_ContentInstance();
        long zxid = NotificationUtils.zxid.getAndIncrement();
        sensor.setId(zxid);
        m2m_contentInstance.con = ResourceReflection.serializeKryo(sensor);
        m2m_primitiveContentType.value = m2m_contentInstance;
        StringWriter sw = new StringWriter();
        try {
            resourceTestBase.marshaller.get().marshal(m2m_primitiveContentType, sw);
            NotificationUtils.zxidMapStartTime.put(zxid, System.currentTimeMillis());
            resourceTestBase.testCreate(cntUri, contentInstance, sw.toString(), OK, ASYNC);//创建一个containerInstance
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMachineUri() {
        return machineUri;
    }
}
