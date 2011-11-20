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

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.xml.sax.SAXException;
import sun.misc.BASE64Encoder;

import javax.xml.parsers.ParserConfigurationException;

public class CheddarGetterPaymentService implements PaymentService {
	private static Logger log = Logger.getLogger(CheddarGetterPaymentService.class.toString());
	
	private static String CG_SERVICE_ROOT = "https://cheddargetter.com/xml";

    private String serviceRoot = CG_SERVICE_ROOT;
	private String userName;
	private String password;
	private String productCode;


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

	/* (non-Javadoc)
	 * @see com.rusticisoftware.cheddargetter.client.PaymentService#getCustomer(java.lang.String)
	 */
	public Customer getCustomer(String custCode) throws PaymentException {
		Document doc = null;
		try {
			doc = makeServiceCall("/customers/get/productCode/" + getProductCode() + "/code/" + custCode, null);
		}
		catch (PaymentServiceException cge){
			//If the exception is just that the customer doesn't exist, return null
			if(cge.getCode() == 404){
				return null;
			}
		}
		Element root = doc.getDocumentElement();
		Element customer = XmlUtils.getFirstChildByTagName(root, "customer");
		return (customer == null) ? null : new Customer(customer);
	}

	/* (non-Javadoc)
	 * @see com.rusticisoftware.cheddargetter.client.PaymentService#customerExists(java.lang.String)
	 */
	public boolean customerExists(String custCode) {
		boolean exists = false;
		try {
			Customer cust = getCustomer(custCode);
			if(cust != null){
				exists = true;
			}
		}
		catch (Exception e) {}
		return exists;
	}

	/* (non-Javadoc)
	 * @see com.rusticisoftware.cheddargetter.client.PaymentService#getAllCustomers()
	 */
	public Document getAllCustomers() throws PaymentException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("subscriptionStatus", "activeOnly");
		return makeServiceCall("/customers/get/productCode/" + getProductCode(), params);
	}

	/* (non-Javadoc)
	 * @see com.rusticisoftware.cheddargetter.client.PaymentService#createNewCustomer(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public Customer createNewCustomer(String custCode, String firstName, String lastName,
			String email, String company, String subscriptionPlanCode, String ccFirstName,
			String ccLastName, String ccNumber, String ccExpireMonth, String ccExpireYear,
			String ccCardCode, String ccZip) throws PaymentException {

		HashMap<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("code", custCode);
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

		Document doc = makeServiceCall("/customers/new/productCode/" + getProductCode(), paramMap);
		Element root = doc.getDocumentElement();
		Element customer = XmlUtils.getFirstChildByTagName(root, "customer");
		return new Customer(customer);
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

		Document doc = makeServiceCall("/customers/edit/productCode/" + getProductCode() + "/code/" + custCode, paramMap);
		Element root = doc.getDocumentElement();
		Element customer = XmlUtils.getFirstChildByTagName(root, "customer");
		return new Customer(customer);
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
		Document doc = makeServiceCall("/customers/edit-customer/productCode/" + getProductCode() + "/code/" + custCode, paramMap);
		Element root = doc.getDocumentElement();
		Element customer = XmlUtils.getFirstChildByTagName(root, "customer");
		return new Customer(customer);
	}

	/* (non-Javadoc)
	 * @see com.rusticisoftware.cheddargetter.client.PaymentService#updateSubscription(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public Document updateSubscription(String customerCode, String planCode, String ccFirstName, String ccLastName,
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

		String relativeUrl = "/customers/edit-subscription/productCode/" + getProductCode() + "/code/" + customerCode;
		return makeServiceCall(relativeUrl, paramMap);
	}

	/* (non-Javadoc)
	 * @see com.rusticisoftware.cheddargetter.client.PaymentService#cancelSubscription(java.lang.String)
	 */
	public Document cancelSubscription(String customerCode) throws PaymentException {
		return makeServiceCall("/customers/cancel/productCode/" + getProductCode() + "/code/" + customerCode, null);
	}

	/* (non-Javadoc)
	 * @see com.rusticisoftware.cheddargetter.client.PaymentService#addItemQuantity(java.lang.String, java.lang.String)
	 */
	public Document addItemQuantity(String customerCode, String itemCode) throws PaymentException {
	    return addItemQuantity(customerCode, itemCode, 1);
	}

	/* (non-Javadoc)
	 * @see com.rusticisoftware.cheddargetter.client.PaymentService#addItemQuantity(java.lang.String, java.lang.String, int)
	 */
	public Document addItemQuantity(String customerCode, String itemCode, int quantity) throws PaymentException {
	    HashMap<String, String> paramMap = new HashMap<String, String>();
	    paramMap.put("quantity", String.valueOf(quantity));

	    String relativeUrl = "/customers/add-item-quantity/productCode/" + getProductCode() +
	                         "/code/" + customerCode + "/itemCode/" + itemCode;
	    return makeServiceCall(relativeUrl, paramMap);

	}

	/* (non-Javadoc)
	 * @see com.rusticisoftware.cheddargetter.client.PaymentService#getLatestCreditCardData(java.lang.String)
	 */
	public CreditCardData getLatestCreditCardData(String customerCode) throws PaymentException {
		Customer customer;
		try { customer = getCustomer(customerCode); }
		catch (Exception e) { return null; }

		List<Subscription> subs = customer.getSubscriptions();
		if(subs == null || subs.size() == 0){
			return null;
		}

		Subscription sub = subs.get(0);
		if(sub.getCcExpirationDate() == null){
			return null;
		}

		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		cal.setTime(sub.getCcExpirationDate());
		return new CreditCardData(sub.getCcFirstName(), sub.getCcLastName(),
				sub.getCcType(), sub.getCcLastFour(),
				cal.get(Calendar.MONTH), cal.get(Calendar.YEAR));
	}

	/* (non-Javadoc)
	 * @see com.rusticisoftware.cheddargetter.client.PaymentService#isLatestSubscriptionCanceled(java.lang.String)
	 */
	public boolean isLatestSubscriptionCanceled(String customerCode) throws PaymentException {
		Customer customer;
		try { customer = getCustomer(customerCode); }
		catch (Exception e) { return false; }

		List<Subscription> subs = customer.getSubscriptions();
		if(subs == null || subs.size() == 0){
			return false;
		}

		Subscription sub = subs.get(0);
		if(sub.getCanceledDatetime() == null){
			return false;
		}

		return true;
	}

	/* (non-Javadoc)
	 * @see com.rusticisoftware.cheddargetter.client.PaymentService#getCurrentItemUsage(java.lang.String, java.lang.String)
	 */
	public int getCurrentItemUsage(String customerCode, String itemCode) throws PaymentException{
	    Customer cust = getCustomer(customerCode);
	    List<Item> currentItems = cust.getSubscriptions().get(0).getItems();
	    for(Item item : currentItems){
	        if(item.getCode().equals(itemCode)){
	            return item.getQuantity();
	        }
	    }
	    throw new PaymentException("Couldn't find item with code " + itemCode);
	}

	public Document makeServiceCall(String path, Map<String,String> paramMap) throws PaymentException {
		String fullPath = CG_SERVICE_ROOT + path;
		String encodedParams = encodeParamMap(paramMap);
		String response = postTo(fullPath, getUserName(), getPassword(), encodedParams);
        Document document = null;
        try {
            document = XmlUtils.parseXmlString(response);
            log.log(Level.FINE, "Response from CG: " + XmlUtils.getXmlString(document));
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException("Unable to create XML parser", e);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read from response", e);
        } catch (SAXException e) {
            throw new PaymentException("Unable to parse XML response.", e);
        }
		try {
			checkResponseForError(document);
		} catch (PaymentServiceException cge) {
			//Let's not log 404s when looking for a customer, since we may
			//often be looking for a customer just to see if they exist, and this
			//ends up polluting the logs a lot...
			boolean missingCustomer = path.startsWith("/customers") && cge.getCode() == 404;
			if(!missingCustomer){
				log.log(Level.WARNING, "Error calling service at " + path, cge);
				throw cge;
			}
		}
		return document;
	}

	protected String postTo(String urlStr, String userName, String password, String data) throws PaymentException {

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

            //Get response
            BufferedReader rd;
            try {
                rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } catch (IOException ioe) {
                rd = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }

            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while((responseLine = rd.readLine()) != null){
                response.append(responseLine);
            }

            log.fine("Got this back from CG: " + response.toString());

            return response.toString();
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
	
	protected boolean checkResponseForError(Document doc) throws PaymentException {
		Element root = doc.getDocumentElement();
		if(root.getNodeName().equals("error")){
			throw getExceptionFromElement(root);
		}
		else if (root.getNodeName().equals("customers")){
			Element errorsElem = XmlUtils.getFirstChildByTagName(root, "errors");
			if(errorsElem != null){
				Element errorElem = XmlUtils.getFirstChildByTagName(errorsElem, "error");
				if(errorElem != null){
					throw getExceptionFromElement(errorElem);
				}
			}
		}
		return true;
	}
	
	protected PaymentServiceException getExceptionFromElement(Element errorElem){
		String code = errorElem.getAttribute("code");
		String auxCode = errorElem.getAttribute("auxCode");
		if(auxCode == null || auxCode.length() == 0){
			auxCode = "0";
		}
		String message = errorElem.getTextContent();
		return new PaymentServiceException(Integer.parseInt(code), Integer.parseInt(auxCode), message);
	}
	
	public static Date parseCgDate(String cgDate) {
		if(cgDate == null || cgDate.length() == 0){
			return null;
		}
		
		try{
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			return sdf.parse(fixDateFormat(cgDate));
		}
		catch (Exception e){
			log.log(Level.WARNING, "Exception parsing date " + cgDate, e);
			return null;
		}
    }
	
	public static String fixDateFormat(String cgDate){
    	//CG's dates have annoying ':' symbol in middle of timezone part
    	//So here we take it out
    	int tzIndex = cgDate.lastIndexOf("+");
    	String tz = cgDate.substring(tzIndex, cgDate.length());
    	String modifiedTz = tz.replace(":", "");
    	return cgDate.substring(0, tzIndex) + modifiedTz;
	}
	
	private static String stripCcNumber(String ccNumber) {
		return (ccNumber == null) ? null : ccNumber.replace(" ", "").replace("-", "");
	}
}
