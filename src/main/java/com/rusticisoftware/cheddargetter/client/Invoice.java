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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Invoice implements Serializable {
	protected @XmlAttribute String id;
	protected @XmlElement String number;
	protected @XmlElement String type;
	protected Date billingDatetime;
	protected Date createdDatetime;
	protected @XmlElement(name = "transaction") @XmlElementWrapper List<Transaction> transactions = new ArrayList<Transaction>();
	protected @XmlElement(name = "charge") @XmlElementWrapper List<Charge> charges = new ArrayList<Charge>();

    public Invoice() {
    }

    public String getId() {
		return id;
	}

	public String getNumber() {
		return number;
	}

	public String getType() {
		return type;
	}

	public Date getBillingDatetime() {
		return billingDatetime;
	}

	public Date getCreatedDatetime() {
		return createdDatetime;
	}
	
	public List<Transaction> getTransactions(){
		return transactions;
	}
	
	public List<Charge> getCharges() {
		return charges;
	}
	
	public double getTotalAmount(){
		double sum = 0.0d;
		for(Charge charge : charges){
			sum += charge.getEachAmount() * charge.getQuantity();
		}
		return sum;
	}

	public Invoice(Element elem){
		this.id = elem.getAttribute("id");
		this.number = XmlUtils.getNamedElemValue(elem, "number");
		this.type = XmlUtils.getNamedElemValue(elem, "type");
		this.billingDatetime = CheddarGetterPaymentService.parseCgDate(XmlUtils.getNamedElemValue(elem, "billingDatetime"));
		this.createdDatetime = CheddarGetterPaymentService.parseCgDate(XmlUtils.getNamedElemValue(elem, "createdDatetime"));
		
		Element transactionsParent = XmlUtils.getFirstChildByTagName(elem, "transactions");
		if(transactionsParent != null){
			List<Element> transactionsList = XmlUtils.getChildrenByTagName(transactionsParent, "transaction");
			for(Element transaction : transactionsList){
				this.transactions.add(new Transaction(transaction));
			}
		}
		
		Element chargesParent = XmlUtils.getFirstChildByTagName(elem, "charges");
		if(chargesParent != null){
			List<Element> chargesList = XmlUtils.getChildrenByTagName(chargesParent, "charge");
			for(Element charge : chargesList){
				this.charges.add(new Charge(charge));
			}
		}
	}
}
