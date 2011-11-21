package com.cheddargetter.client.service;

import com.cheddargetter.client.api.*;
import com.cheddargetter.client.api.Error;
import org.apache.commons.io.IOUtils;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static javax.xml.bind.JAXBContext.newInstance;
import static org.testng.Assert.assertEquals;

@Test
public class XmlMarshallingTest {

    public static final String CUSTOMER_XML = "/customer.xml";
    public static final String PLANS_XML = "/plans.xml";
    public static final String ERROR_XML = "/error.xml";

    private JAXBContext context;

    @BeforeTest
    public void setupJaxbContext() throws JAXBException {
        context = newInstance(Customers.class, Plans.class, Error.class);
    }

    @Test
    public void testCustomerFromXmlJAXB() throws IOException, SAXException, ParserConfigurationException, JAXBException {
        Unmarshaller unmarshaller = context.createUnmarshaller();
        Object test = unmarshaller.unmarshal(stream(CUSTOMER_XML));

        assertEquals(test.getClass(), Customers.class);
        Customers customers = (Customers) test;
        Customer customer = customers.getCustomers().get(0);
        assertEquals(customer.getClass(), Customer.class);
        assertEquals(customer.getCode(), "test_customer");
        List<Subscription> subscriptions = customer.getSubscriptions();
        assertEquals(subscriptions.size(), 2);
        Subscription subscription = subscriptions.get(0);
        Plan plan = subscription.getPlans().get(0);
        assertEquals(plan.getCode(), "PAID");
    }

    @Test
    public void testPlansFromXmlJAXB() throws IOException, SAXException, ParserConfigurationException, JAXBException {
        Unmarshaller unmarshaller = context.createUnmarshaller();
        Object test = unmarshaller.unmarshal(stream(PLANS_XML));

        assertEquals(test.getClass(), Plans.class);
        Plans plans = (Plans) test;
        Plan plan = plans.getPlans().get(0);
        assertEquals(plan.getClass(), Plan.class);
        assertEquals(plan.getCode(), "FREE");
    }

    @Test
    public void testErrorFromXmlJAXB() throws IOException, SAXException, ParserConfigurationException, JAXBException {
        Unmarshaller unmarshaller = context.createUnmarshaller();
        Object test = unmarshaller.unmarshal(stream(ERROR_XML));

        assertEquals(test.getClass(), Error.class);
        Error error = (com.cheddargetter.client.api.Error) test ;
        assertEquals(error.getId(), "73542");
        assertEquals(error.getCode(), "404");
        assertEquals(error.getMessage(), "Customer not found");
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
        return XmlMarshallingTest.class.getResourceAsStream(fileName);
    }

}
