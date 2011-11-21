package com.cheddargetter.client.service;

import com.cheddargetter.client.api.Customer;
import com.cheddargetter.client.api.PaymentException;
import com.cheddargetter.client.service.CheddarGetterPaymentService;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

@Test(enabled = false)
public class PaymentServiceTest {

    private CheddarGetterPaymentService service = new CheddarGetterPaymentService();

    @BeforeTest
    public void setupJaxbContext() throws Exception {
        service.setUserName(System.getProperty("username"));
        service.setPassword(System.getProperty("password"));
        service.setProductCode(System.getProperty("productcode"));
        service.afterPropertiesSet();
    }

    @Test
    public void testCustomerFromXmlJAXB() throws PaymentException {
        List<Customer> customers = service.getAllCustomers();
        assertTrue(customers.size() > 0);
    }

}
