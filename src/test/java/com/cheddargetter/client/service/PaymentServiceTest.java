package com.cheddargetter.client.service;

import com.cheddargetter.client.api.*;
import org.testng.annotations.*;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

@Test(enabled = false)
public class PaymentServiceTest {

    private CheddarGetterPaymentService service = new CheddarGetterPaymentService();

    @BeforeTest
    public void setupPaymentService() throws Exception {
        service.setUserName(System.getProperty("username"));
        service.setPassword(System.getProperty("password"));
        service.setProductCode(System.getProperty("productcode"));
        service.afterPropertiesSet();
    }

    @Test(enabled = false)
    public void testCustomerFromXmlJAXB() throws PaymentException {
        List<Customer> customers = service.getAllCustomers();
        assertTrue(customers.size() > 0);
    }

	@Test(enabled = false)
	public void testAddCustomCharge() throws Exception {
		Customer customer =
				service.addCustomCharge(System.getProperty("customercode"), "CHARGE_CODE", 1, 25.43, "description", null, null);
		List<Charge> charges = customer.getSubscriptions().get(0).getInvoices().get(0).getCharges();
		Charge lastCharge = charges.get(charges.size() - 1);

		assertTrue("CHARGE_CODE".equals(lastCharge.getCode()));
		assertTrue(lastCharge.getQuantity() == 1);
		assertTrue("description".equals(lastCharge.getDescription()));
	}

	@Test(enabled = false)
	public void testGetCustomer() throws Exception {
		int previousUsage = service.getCurrentItemUsage(System.getProperty("customercode"), System.getProperty("itemcode"));

		service.addItemQuantity(System.getProperty("customercode"), System.getProperty("itemcode"), 200);

		int currentItemUsage = service.getCurrentItemUsage(System.getProperty("customercode"), System.getProperty("itemcode"));
		assertEquals(currentItemUsage, previousUsage + 200);
	}

    @Test(enabled = false)
    public void testGetAllPricingPlans() throws PaymentException {
        Plans plans = service.getAllPricingPlans();
        assertNotNull(plans);
        assertTrue(plans.getPlans().size() > 0);
    }
}
