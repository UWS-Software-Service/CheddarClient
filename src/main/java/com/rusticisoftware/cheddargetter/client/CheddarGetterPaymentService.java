/* Software License Agreement (BSD License)
 * 
 * Copyright (c) 2010-2011, Rustici Software, LLC
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL Rustici Software, LLC BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.rusticisoftware.cheddargetter.client;

import org.springframework.beans.factory.InitializingBean;
import sun.misc.BASE64Encoder;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.Logger;

import static com.rusticisoftware.cheddargetter.client.MapUtils.entry;
import static com.rusticisoftware.cheddargetter.client.MapUtils.hashMap;
import static javax.xml.bind.JAXBContext.newInstance;
import static org.springframework.util.CollectionUtils.isEmpty;

public class CheddarGetterPaymentService implements PaymentService, InitializingBean {
	private static Logger log = Logger.getLogger(CheddarGetterPaymentService.class.toString());
	
	private static String CG_SERVICE_ROOT = "https://cheddargetter.com/xml";

    private String serviceRoot = CG_SERVICE_ROOT;
	private String userName;
	private String password;
	private String productCode;

    private JAXBContext context;

    public String getServiceRoot() {
        return serviceRoot;
    }

    public void setServiceRoot(String serviceRoot) {
        this.serviceRoot = serviceRoot;
    }

	public String getUserName(){
		return userName;
	}

	public void setUserName(String userName){
		this.userName = userName;
	}

	public String getPassword(){
		return password;
	}

	public void setPassword(String password){
		this.password = password;
	}

	public String getProductCode(){
		return productCode;
	}

	public void setProductCode(String productCode){
		this.productCode = productCode;
	}
	
	public CheddarGetterPaymentService(){
	}

    public CheddarGetterPaymentService(String userName, String password, String productCode){
		setUserName(userName);
		setPassword(password);
		setProductCode(productCode);
	}

    public CheddarGetterPaymentService(String serviceRoot, String userName, String password, String productCode) {
        setServiceRoot(serviceRoot);
        setUserName(userName);
        setPassword(password);
        setProductCode(productCode);
    }

    public void afterPropertiesSet() throws Exception {
        context = newInstance(Customers.class, Plans.class, Error.class);
    }

	public Customer getCustomer(String custCode) throws PaymentException {
        return makeServiceCall(
                Customer.class,
                "/customers/get/productCode/" + getProductCode() + "/code/" + custCode
        );
	}

	public boolean customerExists(String custCode) throws PaymentException {
		try {
			return getCustomer(custCode) != null;
		} catch (PaymentServiceException paymentException) {
            if (paymentException.getCode() == 404) {
                return false;
            } else {
                throw paymentException;
            }
        }
	}

	public Customers getAllCustomers() throws PaymentException {
		return makeServiceCall(
                Customers.class,
                "/customers/get/productCode/" + getProductCode(),
                hashMap(
                        entry("subscriptionStatus", "activeOnly")
                )
        );
	}

	public Customer createNewCustomer(String custCode, String firstName, String lastName,
			String email, String company, String subscriptionPlanCode, String ccFirstName,
			String ccLastName, String ccNumber, String ccExpireMonth, String ccExpireYear,
			String ccCardCode, String ccZip) throws PaymentException {

		HashMap<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("code", custCode);
		paramMap.put("firstName", firstName);
		paramMap.put("lastName", lastName);
		paramMap.put("email", email);
		if(company != null)
			paramMap.put("company", company);

		paramMap.put("subscription[planCode]", subscriptionPlanCode);
		//If plan is free, no cc information needed, so we just check
		//ccNumber field and assume the rest are there or not
		if(ccNumber != null){
			paramMap.put("subscription[ccFirstName]", ccFirstName);
			paramMap.put("subscription[ccLastName]", ccLastName);
			paramMap.put("subscription[ccNumber]", stripCcNumber(ccNumber));
			paramMap.put("subscription[ccExpiration]", ccExpireMonth + "/" + ccExpireYear);
			if(ccCardCode != null)
				paramMap.put("subscription[ccCardCode]", ccCardCode);
			if(ccZip != null)
				paramMap.put("subscription[ccZip]", ccZip);
		}

        return firstCustomer(
                makeServiceCall(
                        Customers.class,
                        "/customers/new/productCode/" + getProductCode(),
                        paramMap
                )
        );
	}

	public Customer updateCustomerAndSubscription(String custCode, String firstName, String lastName,
			String email, String company, String subscriptionPlanCode, String ccFirstName,
			String ccLastName, String ccNumber, String ccExpireMonth, String ccExpireYear,
			String ccCardCode, String ccZip) throws PaymentException {

		HashMap<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("firstName", firstName);
		paramMap.put("lastName", lastName);
		paramMap.put("email", email);
		if(company != null){
			paramMap.put("company", company);
		}

		paramMap.put("subscription[planCode]", subscriptionPlanCode);

		//If plan is free, no cc information needed, so we just check
		//ccNumber field and assume the rest are there or not
		if(ccNumber != null){
			paramMap.put("subscription[ccFirstName]", ccFirstName);
			paramMap.put("subscription[ccLastName]", ccLastName);
			paramMap.put("subscription[ccNumber]", stripCcNumber(ccNumber));
			paramMap.put("subscription[ccExpiration]", ccExpireMonth + "/" + ccExpireYear);
			if(ccCardCode != null){
				paramMap.put("subscription[ccCardCode]", ccCardCode);
			}
			if(ccZip != null){
				paramMap.put("subscription[ccZip]", ccZip);
			}
		}

        return firstCustomer(
                makeServiceCall(
                        Customers.class,
                        "/customers/edit/productCode/" + getProductCode() + "/code/" + custCode,
                        paramMap
                )
        );
	}

    public Customer updateCustomer(String custCode, String firstName, String lastName,
			String email, String company) throws PaymentException {
		HashMap<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("firstName", firstName);
		paramMap.put("lastName", lastName);
		paramMap.put("email", email);
		if(company != null){
			paramMap.put("company", company);
		}
        return firstCustomer(
                makeServiceCall(
                        Customers.class,
                        "/customers/edit-customer/productCode/" + getProductCode() + "/code/" + custCode,
                        paramMap
                )
        );
	}

	public Customer updateSubscription(String customerCode, String planCode, String ccFirstName, String ccLastName,
                                       String ccNumber, String ccExpireMonth, String ccExpireYear, String ccCardCode, String ccZip) throws PaymentException {

		HashMap<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("planCode", planCode);

		//If plan is free, no cc information needed, so we just check
		//ccNumber field and assume the rest are there or not
		if(ccNumber != null){
			paramMap.put("ccFirstName", ccFirstName);
			paramMap.put("ccLastName", ccLastName);
			paramMap.put("ccNumber", stripCcNumber(ccNumber));
			paramMap.put("ccExpiration", ccExpireMonth + "/" + ccExpireYear);
			if(ccCardCode != null){
				paramMap.put("ccCardCode", ccCardCode);
			}
			if(ccZip != null){
				paramMap.put("ccZip", ccZip);
			}
		}

        return makeServiceCall(
                Customer.class,
                "/customers/edit-subscription/productCode/" + getProductCode() + "/code/" + customerCode,
                paramMap
        );
	}

	public Customer cancelSubscription(String customerCode) throws PaymentException {
		return makeServiceCall(
                Customer.class,
                "/customers/cancel/productCode/" + getProductCode() + "/code/" + customerCode
        );
	}

	public Customer addItemQuantity(String customerCode, String itemCode) throws PaymentException {
	    return addItemQuantity(customerCode, itemCode, 1);
	}

	public Customer addItemQuantity(String customerCode, String itemCode, int quantity) throws PaymentException {
        return makeServiceCall(
                Customer.class,
                "/customers/add-item-quantity/productCode/" + getProductCode() + "/code/" + customerCode + "/itemCode/" + itemCode,
                hashMap(
                        entry("quantity", String.valueOf(quantity))
                )
        );

	}

	public CreditCardData getLatestCreditCardData(String customerCode) throws PaymentException {
		Customer customer = getCustomer(customerCode);
        for (Subscription sub : customer.getSubscriptions())
            if(sub.getCcExpirationDate() != null)
                return getCreditCardFromSubscription(sub);
        return null;
	}

    public boolean isLatestSubscriptionCanceled(String customerCode) throws PaymentException {
        Customer customer = getCustomer(customerCode);
        Subscription subscription = getFirstSubscription(customer);
        return subscription != null && subscription.getCanceledDatetime() != null;
    }

    public int getCurrentItemUsage(String customerCode, String itemCode) throws PaymentException{
        Customer customer = getCustomer(customerCode);
        Subscription subscription = getFirstSubscription(customer);
        if (subscription != null)
            for(Item item : subscription.getItems())
                if(item.getCode().equals(itemCode))
                    return item.getQuantity();
        throw new PaymentException("Couldn't find item with code " + itemCode);
    }

    protected CreditCardData getCreditCardFromSubscription(Subscription sub) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.setTime(sub.getCcExpirationDate());
        return new CreditCardData(
                sub.getCcFirstName(), sub.getCcLastName(),
                sub.getCcType(), sub.getCcLastFour(),
                cal.get(Calendar.MONTH), cal.get(Calendar.YEAR)
        );
    }

    protected Customer firstCustomer(Customers customers) {
        if (customers == null || isEmpty(customers.getCustomers()))
            return null;
        return customers.getCustomers().get(0);
    }

    protected Subscription getFirstSubscription(Customer customer) {
        List<Subscription> subs = customer.getSubscriptions();
        return isEmpty(subs) ? null : subs.get(0);
    }

    protected <T> T makeServiceCall(Class<T> clazz, String path) throws PaymentException {
        return makeServiceCall(clazz, path, null);
    }

	protected <T> T makeServiceCall(Class<T> clazz, String path, Map<String, String> paramMap) throws PaymentException {
		String fullPath = CG_SERVICE_ROOT + path;
		String encodedParams = encodeParamMap(paramMap);
		InputStream responseStream = postTo(fullPath, getUserName(), getPassword(), encodedParams);
        try {
            Object response = context.createUnmarshaller().unmarshal(responseStream);
            if (response instanceof Error)
                throw new PaymentServiceException((Error) response);
            if (response.getClass().equals(clazz))
                return (T) response;
            else
                throw new PaymentException("Unexpected return content.");
        } catch (JAXBException e) {
            throw new PaymentException("Unable to create XML response unmarshaller", e);
        }
	}

	protected InputStream postTo(String urlStr, String userName, String password, String data) throws PaymentException {

		log.fine("Sending this data to this url: " + urlStr + " data = " + data);

		//Create a new request to send this data...
        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();

            //Put authentication fields in http header, and make the data the body
            BASE64Encoder enc = new BASE64Encoder();
            //connection.setRequestProperty("Content-Type", "text/xml");
            String auth = userName + ":" + password;
            connection.setRequestProperty("Authorization", "Basic " + enc.encode(auth.getBytes()));


            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            PrintWriter output = new PrintWriter(new OutputStreamWriter(connection.getOutputStream()));
            output.write(data);
            output.flush();
            output.close();

            try {
                return connection.getInputStream();
            } catch (IOException ioe) {
                return connection.getErrorStream();
            }
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Provided service URL not correct.", e);
        } catch (ProtocolException e) {
            throw new IllegalStateException("POST method not found on protocol.", e);
        } catch (IOException e) {
            throw new PaymentException("Communications exception occurred while processing request", e);
        }
    }

	protected String encodeParamMap(Map<String, String> paramMap) throws PaymentException {
		if(paramMap == null || paramMap.keySet().size() == 0){
			return "";
		}
		StringBuilder encoded = new StringBuilder();
		for (String name : paramMap.keySet()){
			encoded.append(getEncodedParam(name, paramMap.get(name)) + "&");
		}
		//Cutoff last ampersand
		encoded.delete(encoded.length() - 1, encoded.length());

		return encoded.toString();
	}

	protected String getEncodedParam(String paramName, String paramVal) {
        try {
            return URLEncoder.encode(paramName, "UTF-8") + "=" + URLEncoder.encode(paramVal, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 encoding not found.", e);
        }
    }

	protected String stripCcNumber(String ccNumber) {
		return (ccNumber == null) ? null : ccNumber.replace(" ", "").replace("-", "");
	}
}
