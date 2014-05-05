package org.bongiorno.ariadne;

import org.bongiorno.ariadne.implementations.xml.jaxb.KnowledegeBaseEntry;
import org.bongiorno.ariadne.implementations.xml.jaxb.OperatorEntry;
import org.bongiorno.ariadne.implementations.xml.jaxb.XmlKnowledgeBase;
import org.bongiorno.ariadne.operandowners.BooleanOperandOwner;
import org.bongiorno.ariadne.operators.arithmetic.*;
import org.bongiorno.ariadne.operators.logical.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.*;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Set;

/**
 * @author chribong
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:xml-test-ontext.xml"})
public class XmlIntegrationTest extends AbstractIntegrationTest {
    static {
        System.getProperties().put("engines.xml", "org.bongiorno.ariadne.XmlIntegrationTest$InMemoryStreamKnowledgeBase");
    }

    @Autowired
    private Resource xmlFile;

    private static ByteArrayOutputStream baos = new ByteArrayOutputStream();


    private final TestStep RSET_STREAM = new TestStep() {
        public void perform() throws Exception {
            baos.reset();
        }
    };
    private final TestStep INIT_XML = new TestStep() {
        public void perform() throws Exception {
            baos.reset();
            setUp();
        }
    };

    @Test
    public void testLargeInset() throws Exception {
        doLargeInsert("xml", INIT_XML, RSET_STREAM, NO_OP);


    }

    @Test
    public void testHugeStore() throws Exception {
        doHugeStore("xml", INIT_XML, RSET_STREAM, NO_OP);

    }

    @Test
    public void testStorable() throws Exception {
        doStore("xml", INIT_XML, RSET_STREAM, NO_OP);

    }

    @Test
    public void testNullValueOperandOwner() throws Exception {
        nullOerandOwnerInputStore("xml", INIT_XML, RSET_STREAM, NO_OP);

    }

    @Before
    public void setUp() throws Exception {
        baos.reset();

//        OutputStream out = new FileOutputStream(System.getProperty("xml.config"));


        String s = KnowledegeBaseEntry.class.getPackage().getName();

        JAXBContext jc = JAXBContext.newInstance(s);
        KnowledegeBaseEntry kbout = new KnowledegeBaseEntry();


        kbout.addOperatorEntry(Equal.class.getName(), "==");
        kbout.addOperatorEntry(LessThan.class.getName(), "<");
        kbout.addOperatorEntry(LessThanEqual.class.getName(), "<=");
        kbout.addOperatorEntry(NotEqual.class.getName(), "!=");
        kbout.addOperatorEntry(GreaterThan.class.getName(), ">");
        kbout.addOperatorEntry(GreaterThanEqual.class.getName(), ">=");
        kbout.addOperatorEntry(Or.class.getName(), "||");
        kbout.addOperatorEntry(And.class.getName(), "&&");
        kbout.addOperatorEntry(Add.class.getName(), "+");
        kbout.addOperatorEntry(Subtract.class.getName(), "-");
        kbout.addOperatorEntry(Multiply.class.getName(), "*");
        kbout.addOperatorEntry(Divide.class.getName(), "/");
        kbout.addOperatorEntry(Min.class.getName(), "min");
        kbout.addOperatorEntry(Max.class.getName(), "max");
        kbout.addOperatorEntry(Power.class.getName(), "pow");



        Marshaller marshaller = jc.createMarshaller();
        marshaller.marshal(kbout, baos);

    }

    @Test
    public void testName() throws Exception {


    }
    public static class InMemoryStreamKnowledgeBase extends XmlKnowledgeBase {


        @Override
        public InputStream getXmlInputStream() throws IOException {
            return new ByteArrayInputStream(baos.toByteArray());
        }

        @Override
        public OutputStream getXmlOutputStream() throws IOException {
            return baos;
        }
    }
}
