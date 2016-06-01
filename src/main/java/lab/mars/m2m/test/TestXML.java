package lab.mars.m2m.test;

import lab.mars.m2m.nami.core.AgentID;
import lab.mars.m2m.nami.util.registry.StringKey;
import lab.mars.m2m.protocol.common.m2m_ID;
import lab.mars.m2m.protocol.common.m2m_aggregatedResponse;
import lab.mars.m2m.protocol.enumeration.m2m_consistencyStrategy;
import lab.mars.m2m.protocol.primitive.m2m_primitiveContentType;
import lab.mars.m2m.protocol.primitive.m2m_req;
import lab.mars.m2m.protocol.primitive.m2m_rsp;
import lab.mars.m2m.protocol.resource.m2m_CSEBase;
import lab.mars.m2m.protocol.resource.m2m_Group;
import lab.mars.m2m.resource.common.ResourceURL;
import org.junit.Before;
import org.junit.Test;

import javax.xml.bind.*;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static lab.mars.m2m.protocol.enumeration.m2m_cseTypeID.MN_CSE;
import static lab.mars.m2m.protocol.enumeration.m2m_resourceType.*;

/**
 * Created by haixiao on 2015/4/1.
 * Email: wumo@outlook.com
 */
public class TestXML {
    JAXBContext jc = null;
    Unmarshaller unmarshaller;
    private Marshaller marshaller;

    @Before
    public void setUp() throws JAXBException {
        jc = JAXBContext.newInstance(m2m_primitiveContentType.class, m2m_req.class, m2m_rsp.class);
//        jc = JAXBContext.newInstance("lab.mars.m2m.protocol.resource:lab.mars.m2m.protocol.primitive");
        unmarshaller = jc.createUnmarshaller();
        marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    }

    @Test
    public void testSerializeCSE() throws Exception {
        m2m_CSEBase resource = createCSE();
        StringWriter sw2 = new StringWriter();
        marshaller.marshal(resource, sw2);
        System.out.println(sw2.toString());
    }

    @Test
    public void testSerializePC() throws Exception {
        m2m_primitiveContentType pc = new m2m_primitiveContentType();
        pc.value = createCSE();
        StringWriter sw2 = new StringWriter();
        marshaller.marshal(pc, sw2);
        System.out.println(sw2.toString());
    }

    private m2m_CSEBase createCSE() {
        m2m_CSEBase resource;
        ResourceURL myURL = new ResourceURL(new AgentID(new StringKey("csebase")));
        resource = new m2m_CSEBase();

        resource.ty = CSEBase;
        resource.ri = myURL.toString();
        resource.ct = new Date();
        resource.lt = new Date();
        resource.rn = "mars";

        resource.cst = MN_CSE;

        resource.srt = new ArrayList<>();
        resource.srt.add(container);
        resource.srt.add(contentInstance);
        resource.srt.add(subscription);

        resource.poa = new ArrayList<>();
        resource.poa.add("http://127.0.0.1:8080");

        try {
            resource.nl = new URI("http://test.com");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        resource.ch = new ArrayList<>();
        return resource;
    }

    @Test
    public void testXML() throws Exception {

        try {
            URL testXml = Thread.currentThread().getContextClassLoader().getResource("DmgCSEBase.xml");
            m2m_primitiveContentType target = (m2m_primitiveContentType) unmarshaller.unmarshal(testXml);
            if (target == null)
                System.out.println("error!");
            //            StringWriter sw2 = new StringWriter();
            //            marshaller.marshal(tg, sw2);
            //            log.info("unmarshal:\n" + sw2);
        } catch (UnmarshalException une) {
            une.printStackTrace();
        }
    }

    @Test
    public void testXML2() throws Exception {

        try {
            StringWriter sw = new StringWriter();
            m2m_rsp rsp = new m2m_rsp();
            rsp.to = "sadf";
            rsp.fr = new m2m_ID("2");
            rsp.rqi = new m2m_ID("sfda");
            rsp.pc = new m2m_primitiveContentType();
            m2m_aggregatedResponse agr = new m2m_aggregatedResponse();
            agr.rsp = new LinkedList<>();

            m2m_rsp rsp1 = new m2m_rsp();
            rsp1.fr = new m2m_ID("2");
            rsp1.rqi = new m2m_ID("sfda");
            agr.rsp.add(rsp1);
            m2m_rsp rsp2 = new m2m_rsp();
            rsp2.fr = new m2m_ID("23");
            rsp2.rqi = new m2m_ID("sfddsfa");
            agr.rsp.add(rsp2);
//            rsp.pc.value = agr;
            marshaller.marshal(rsp, sw);
            System.out.println(sw.toString());
        } catch (UnmarshalException une) {
            une.printStackTrace();
        }
    }

}
