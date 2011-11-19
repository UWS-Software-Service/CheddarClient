import com.rusticisoftware.cheddargetter.client.Charge;
import com.rusticisoftware.cheddargetter.client.Customer;
import com.rusticisoftware.cheddargetter.client.Customers;
import com.rusticisoftware.cheddargetter.client.XmlUtils;
import org.apache.commons.io.IOUtils;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

import static com.rusticisoftware.cheddargetter.client.XmlUtils.getFirstChildByTagName;
import static org.testng.Assert.assertEquals;

@Test
public class PaymentServiceTest {


    public static final String CUSTOMER_XML = streamString("/customer.xml");
    public static final String PLANS_XML = streamString("/plans.xml");
    public static final String ERROR_XML = streamString("/error.xml");

    @Test
    public void testCustomerFromXmlDOM() throws IOException, SAXException, ParserConfigurationException {
        Document document = XmlUtils.parseXmlString(CUSTOMER_XML);
        Customer customer = new Customer(
                getFirstChildByTagName(document.getDocumentElement(), "customer")
        );
        assertEquals("test_customer", customer.getCode());
    }

    @Test
    public void testCustomerFromXmlJAXB() throws IOException, SAXException, ParserConfigurationException, JAXBException {
        JAXBContext context = JAXBContext.newInstance(Customers.class, Customer.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        Object test = unmarshaller.unmarshal(stream("/customer.xml"));

        assertEquals(test.getClass(), Customers.class);
        Customers customers = (Customers) test;
        Customer customer = customers.getCustomer().get(0);
        assertEquals(customer.getClass(), Customer.class);
        assertEquals(customer.getCode(), "test_customer");
    }

    private static String streamString(String fileName) {
        try {
            return IOUtils.toString(
                    stream(fileName)
            );
        } catch (IOException e) {
            return null;
        }
    }

    private static InputStream stream(String fileName) {
        return PaymentServiceTest.class.getResourceAsStream(fileName);
    }

}
