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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.w3c.dom.Element;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlType
public class Subscription {

	protected @XmlAttribute String id;
	protected @XmlElement String gatewayToken;
	protected @XmlElement String ccFirstName;
	protected @XmlElement String ccLastName;
	protected @XmlElement String ccType;
	protected @XmlElement String ccLastFour;
	protected @XmlJavaTypeAdapter(CGDateAdapter.class) Date ccExpirationDate;
	protected @XmlJavaTypeAdapter(CGDateAdapter.class) Date canceledDatetime;
	protected @XmlJavaTypeAdapter(CGDateAdapter.class) Date createdDatetime;


	protected @XmlElement(name = "plan") @XmlElementWrapper List<Plan> plans = new ArrayList<Plan>();
	protected @XmlElement(name = "item") @XmlElementWrapper List<Item> items = new ArrayList<Item>();
	protected @XmlElement(name = "invoice") @XmlElementWrapper List<Invoice> invoices = new ArrayList<Invoice>();

    public Subscription() {
    }

    public String getId() {
		return id;
	}

	public String getGatewayToken() {
		return gatewayToken;
	}
	
	public String getCcFirstName() {
		return ccFirstName;
	}
	
	public String getCcLastName() {
		return ccLastName;
	}

	public String getCcType() {
		return ccType;
	}

	public String getCcLastFour() {
		return ccLastFour;
	}

	public Date getCcExpirationDate() {
		return ccExpirationDate;
	}

	public Date getCanceledDatetime() {
		return canceledDatetime;
	}

	public Date getCreatedDatetime() {
		return createdDatetime;
	}

	public List<Plan> getPlans() {
		return plans;
	}

	public List<Item> getItems() {
		return items;
	}

	public List<Invoice> getInvoices() {
		return invoices;
	}

}
