package org.bongiorno.ariadne.implementations.xml.jaxb;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.bongiorno.ariadne.AriadneException;
import org.bongiorno.ariadne.KnowledgeBase;
import org.bongiorno.ariadne.interfaces.OperandOwner;
import org.bongiorno.ariadne.interfaces.Operation;
import org.bongiorno.ariadne.operandowners.OperandOwnerFactory;
import org.bongiorno.ariadne.operations.OperationFactory;
import org.bongiorno.ariadne.operators.OperatorFactory;
import org.bongiorno.ariadne.interfaces.Operator;

/**
 * Created by IntelliJ IDEA.
 * User: chbo
 * Date: Apr 14, 2008
 * Time: 5:28:23 PM
 * There are two two files in the regression test packages, SOES.xsd and SCHS.xsd.
 */
public class XmlKnowledgeBase extends KnowledgeBase {
    private OperatorFactory opFact = new XMLOperatorFactory();
    private OperationFactory operFact = new XMLOperationFactory();
    private OperandOwnerFactory ooFact = new XMLOperandOwnerFactory();
    private KnowledegeBaseEntry kbIn = null;
    private KnowledegeBaseEntry kbout = null;
    private static JAXBContext jc;
    private static String SYS_PARAM = "xml.config";
    private InputStream inStream;
    private OutputStream outStream;

    static {
        String s = KnowledegeBaseEntry.class.getPackage().getName();
        try {
            jc = JAXBContext.newInstance(s);
        }
        catch (JAXBException e) {
            throw new RuntimeException("This should never occurr");
        }
    }

    protected OperationFactory getOperationFactory() {
        return operFact;
    }

    protected OperandOwnerFactory getOperandOwnerFactory() {
        return ooFact;
    }

    protected OperatorFactory getOperatorFactory() {
        return opFact;
    }

    @Override
    public void load() throws AriadneException {
        try {
            Unmarshaller u = jc.createUnmarshaller();
            inStream = getXmlInputStream();
            kbIn = (KnowledegeBaseEntry) u.unmarshal(inStream);
        }
        catch (JAXBException e) {
            throw new AriadneException(e);
        }
        catch (IOException e) {
            throw new AriadneException(e);
        }
        super.load();
    }

    @Override
    public void store() throws AriadneException {
        try {
            // create a fresh copy of KB
            kbout = new KnowledegeBaseEntry();
            super.store();
            Marshaller marshaller = jc.createMarshaller();
            outStream = getXmlOutputStream();
            marshaller.marshal(kbout, outStream);
        }
        catch (JAXBException e) {
            throw new AriadneException(e);
        }
        catch (IOException e) {
            throw new AriadneException(e);
        }
    }

    @Override
    public String toString() {
        StringBuffer buff = new StringBuffer();
        String newLine = System.getProperty("line.separator");

        buff.append(System.getProperty(SYS_PARAM)).append(newLine);
        buff.append(super.toString()).append(newLine);
        return buff.toString();
    }

    /**
     * Gets the stream with which to read knowledgebase data from
     * @return a stream from which to read KnowledgeBase data
     * @throws IOException if there was a problem reading from the stream
     */
    public InputStream getXmlInputStream() throws IOException {
        String xmlPath = System.getProperty(SYS_PARAM);
        if(xmlPath == null)
            throw new IllegalArgumentException("you must supply the System property: " + SYS_PARAM + "to use this class");
        return new FileInputStream(xmlPath);
    }

    public OutputStream getXmlOutputStream() throws IOException {
        String xmlPath = System.getProperty(SYS_PARAM);
        if(xmlPath == null)
            throw new IllegalArgumentException("you must supply the System property: " + SYS_PARAM + "to use this class");
        return new FileOutputStream(xmlPath);
    }

    private final class XMLOperatorFactory extends OperatorFactory {
        public void load() throws AriadneException {
            for (OperatorEntry entry : kbIn.getOperatorEntry())
                addOperator(entry.getID(), entry.getFQCN());
        }

        public void store() throws AriadneException {
            for (Operator operator : this.getOperators())
                kbout.addOperatorEntry(new OperatorEntry(operator));

        }
    }

    private class XMLOperationFactory extends OperationFactory {
        private XMLOperationFactory() {
            super(XmlKnowledgeBase.this);
        }

        public void load() throws AriadneException {
            for (OperationEntry operEntry : kbIn.getOperationEntry())
                getOperation(operEntry.getID(), operEntry.getLHO(), operEntry.getOP(), operEntry.getRHO());

        }

        public void store() throws AriadneException {
            for (Operation operation : getOperations())
                kbout.addOperationEntry(new OperationEntry(operation));
        }
    }

    private class XMLOperandOwnerFactory extends OperandOwnerFactory {
        private XMLOperandOwnerFactory() {
            super(XmlKnowledgeBase.this);
        }

        public void load() throws AriadneException {
            for (OperandOwnerEntry ooe : kbIn.getOperandOwnerEntry())
                instantiate(ooe.getID(), ooe.getFQCN(), ooe.getInput());

        }

        public void store() throws AriadneException {
            for (OperandOwner oo : getOperandOwners())
                kbout.addOperandOwnerEntry(new OperandOwnerEntry(oo));
        }
    }

}
