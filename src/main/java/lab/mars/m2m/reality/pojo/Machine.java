package lab.mars.m2m.reality.pojo;

import lab.mars.m2m.protocol.primitive.m2m_primitiveContentType;
import lab.mars.m2m.protocol.resource.m2m_ContentInstance;
import lab.mars.m2m.reflection.ResourceReflection;
import lab.mars.m2m.test.resourcetest.ResourceTestBase;

import javax.xml.bind.JAXBException;
import java.io.StringWriter;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static lab.mars.m2m.protocol.enumeration.m2m_resourceType.contentInstance;
import static lab.mars.m2m.test.resourcetest.ResourceTestBase.ASYNC;

/**
 * Author:yaoalong.
 * Date:2016/4/29.
 * Email:yaoalong@foxmail.com
 */
public abstract class Machine {
    protected ResourceTestBase resourceTestBase;
    protected String cntUri;
    protected boolean isClosed;

    public void request(Machine machine) {
        m2m_primitiveContentType m2m_primitiveContentType = new m2m_primitiveContentType();
        m2m_ContentInstance m2m_contentInstance = new m2m_ContentInstance();
        m2m_contentInstance.con = ResourceReflection.serializeKryo(machine);
        m2m_primitiveContentType.value = m2m_contentInstance;
        StringWriter sw = new StringWriter();
        try {
            ResourceTestBase.marshaller.get().marshal(m2m_primitiveContentType, sw);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        try {

            resourceTestBase.testCreate(cntUri, contentInstance, sw.toString(), OK, ASYNC);//创建一个containerInstance

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public abstract void create(int value);

}
