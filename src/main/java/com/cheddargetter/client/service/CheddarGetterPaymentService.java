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

package com.cheddargetter.client.service;

import com.cheddargetter.client.api.*;
import com.cheddargetter.client.api.Error;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.springframework.beans.factory.InitializingBean;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.*;
import java.util.logging.Logger;

import static com.cheddargetter.client.util.MapUtils.entry;
import static com.cheddargetter.client.util.MapUtils.hashMap;
import static javax.xml.bind.JAXBContext.newInstance;
import static org.springframework.util.CollectionUtils.isEmpty;

public class CheddarGetterPaymentService implements PaymentService, InitializingBean {
	private static Logger log = Logger.getLogger(CheddarGetterPaymentService.class.toString());

    public static final HttpHost CG_SERVICE_HOST = new HttpHost("cheddargetter.com", 443, "https");

    private HttpHost host = CG_SERVICE_HOST;
	private String userName;
	private String password;
	private String productCode;

    private JAXBContext context;

    private HttpClient httpClient;

    public HttpHost getHost() {
        return host;
    }

    public void setHost(HttpHost host) {
        this.host = host;
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

    public CheddarGetterPaymentService(HttpHost host, String userName, String password, String productCode) {
        setHost(host);
        setUserName(userName);
        setPassword(password);
        setProductCode(productCode);
    }

    public void afterPropertiesSet() throws Exception {
        context = newInstance(Customers.class, Plans.class, Error.class, Success.class);

        ThreadSafeClientConnManager connManager = new ThreadSafeClientConnManager();
        connManager.setMaxTotal(25);
        DefaultHttpClient httpClient = new DefaultHttpClient(connManager);
        httpClient.getCredentialsProvider().setCredentials(
                new AuthScope(getHost().getHostName(), getHost().getPort()),
                new UsernamePasswordCredentials(getUserName(), getPassword())
        );
        this.httpClient = httpClient;
    }

	public Customer getCustomer(String custCode) throws PaymentException {
        return firstCustomer(makeServiceCall(
                Customers.class,
                "/customers/get/productCode/" + getProductCode() + "/code/" + custCode
        ));
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

	public List<Customer> getAllCustomers() throws PaymentException {
		return makeServiceCall(
                Customers.class,
                "/customers/get/productCode/" + getProductCode(),
                hashMap(
                        entry("subscriptionStatus", "activeOnly")
                )
        ).getCustomers();
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

    public Customer createNewCustomer(Map<String, String> params) throws PaymentException {
        return firstCustomer(
                makeServiceCall(
                        Customers.class,
                        "/customers/new/productCode/" + getProductCode(),
                        params
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

    @Override
    public void deleteCustomer(String code) throws PaymentException {
        makeServiceCall(
                Success.class,
                "/customers/delete/productCode/" + getProductCode() + "/code/" + code
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
        return firstCustomer(makeServiceCall(
            Customers.class,
            "/customers/add-item-quantity/productCode/" + getProductCode() + "/code/" + customerCode + "/itemCode/" + itemCode,
            hashMap(
                    entry("quantity", String.valueOf(quantity))
            )
        ));
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

	public Customer addCustomCharge(String customerCode, String chargeCode, Integer quantity, Double eachAmount, String description,
	                              String invoicePeriod, String remoteAddress) throws PaymentException {

		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("chargeCode", chargeCode);
		paramMap.put("quantity", quantity.toString());
		paramMap.put("eachAmount", eachAmount.toString());
		if (description != null) {
			paramMap.put("description", description);
		}
		if (invoicePeriod != null) {
			paramMap.put("invoicePeriod", invoicePeriod);
		}
		if (remoteAddress != null) {
			paramMap.put("remoteAddress", remoteAddress);
		}

		return firstCustomer(makeServiceCall(
			Customers.class,
			"/customers/add-charge/productCode/" + getProductCode() + "/code/" + customerCode,
			paramMap
		));
	}

    public Plans getAllPricingPlans() throws PaymentException {
        return makeServiceCall(
            Plans.class,
            "/plans/get/productCode/" + getProductCode()
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
        return makeServiceCall(clazz, path, new HashMap<String, String>());
    }

	protected <T> T makeServiceCall(Class<T> clazz, String path, Map<String, String> paramMap) throws PaymentException {
        InputStream responseStream = postTo("/xml" + path, paramMap);
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

	protected InputStream postTo(String urlStr, Map<String, String> params) throws PaymentException {
        log.fine("Sending this data to this url: " + urlStr + " data = " + params);

        try {
            HttpPost post = new HttpPost(urlStr);
            post.setEntity(createFormEntity(params));
            return httpClient
                    .execute(host, post, createHttpContext())
                    .getEntity()
                    .getContent();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Provided service URL not correct.", e);
        } catch (ProtocolException e) {
            throw new IllegalStateException("POST method not found on protocol.", e);
        } catch (IllegalStateException e) {
            throw new PaymentException("Communications link in illegal state while processing request", e);
        } catch (IOException e) {
            throw new PaymentException("Communications exception occurred while processing request", e);
        }
    }

    protected UrlEncodedFormEntity createFormEntity(Map<String, String> params) throws UnsupportedEncodingException {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> entry : params.entrySet())
            nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        return new UrlEncodedFormEntity(nvps, HTTP.UTF_8);
    }

    private BasicHttpContext createHttpContext() {
        AuthCache authCache = new BasicAuthCache();
        authCache.put(host, new BasicScheme());
        BasicHttpContext context = new BasicHttpContext();
        context.setAttribute(ClientContext.AUTH_CACHE, authCache);
        return context;
    }

	protected String stripCcNumber(String ccNumber) {
		return (ccNumber == null) ? null : ccNumber.replace(" ", "").replace("-", "");
	}
}
