package com.cheddargetter.client.service;

import com.cheddargetter.client.api.Charge;
import com.cheddargetter.client.api.Customer;
import com.cheddargetter.client.api.PaymentException;
import com.cheddargetter.client.api.Plans;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;

import static org.testng.Assert.*;

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

    @Test(enabled = false, expectedExceptions = PaymentException.class)
    public void deleteCustomerThatNotExistShouldThrowPaymentException() throws PaymentException {
        service.deleteCustomer("this_sutomer_for_sure_not_exists_on_CG");
    }

    @Test(enabled = false)
    public void createValidCustomer() throws PaymentException {
        String code = "CG_TEST_CUSTOMER_CODE";
        HashMap<String, String> args = new HashMap<String, String>();
        args.put("code", code);
        args.put("firstName", "Test First Name");
        args.put("lastName", "Test Last Name");
        args.put("email", "proper@email.com");
        args.put("subscription[planCode]", "TEST_TOP_RIGHT_10K");
        args.put("subscription[ccFirstName]", "CC First Name");
        args.put("subscription[ccLastName]", "CC Last Name");
        args.put("subscription[ccNumber]", "468474");
        args.put("subscription[ccExpireMonth]", "14");
        args.put("subscription[ccExpireYear]", "3472");
        args.put("subscription[ccCardCode]", "CASZ");
        args.put("subscription[ccZip]", "892314");
        args.put("subscription[returnUrl]", "http://topright.com");
        args.put("subscription[cancelUrl]", "http://topright.com");
        assertNotNull(service.createNewCustomer(args));
        assertNotNull(service.getCustomer(code));
        // cleanup
        service.deleteCustomer(code);
    }
}
