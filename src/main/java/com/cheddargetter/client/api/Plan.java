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

package com.cheddargetter.client.api;

import com.cheddargetter.client.service.CGDateAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
public class Plan {

	protected @XmlAttribute String code;
	protected @XmlAttribute String id;
	protected @XmlElement String name;
	protected @XmlElement String description;
	protected @XmlElement boolean isActive;
	protected @XmlElement int trialDays;
	protected @XmlElement String billingFrequency;
	protected @XmlElement String billingFrequencyPer;
	protected @XmlElement String billingFrequencyUnit;
	protected @XmlElement int billingFrequencyQuantity;
	protected @XmlElement String setupChargeCode;
	protected @XmlElement float setupChargeAmount;
	protected @XmlElement String recurringChargeCode;
	protected @XmlElement float recurringChargeAmount;
	protected @XmlJavaTypeAdapter(CGDateAdapter.class) Date createdDatetime;
	protected @XmlElement(name = "item") @XmlElementWrapper List<Item> items = new ArrayList<Item>();

    public Plan() {
    }

    public String getCode() {
		return code;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public boolean isActive() {
		return isActive;
	}

	public int getTrialDays() {
		return trialDays;
	}

	public String getBillingFrequency() {
		return billingFrequency;
	}

	public String getBillingFrequencyPer() {
		return billingFrequencyPer;
	}

	public String getBillingFrequencyUnit() {
		return billingFrequencyUnit;
	}

	public int getBillingFrequencyQuantity() {
		return billingFrequencyQuantity;
	}

	public String getSetupChargeCode() {
		return setupChargeCode;
	}

	public float getSetupChargeAmount() {
		return setupChargeAmount;
	}

	public String getRecurringChargeCode() {
		return recurringChargeCode;
	}

	public float getRecurringChargeAmount() {
		return recurringChargeAmount;
	}

	public Date getCreatedDatetime() {
		return createdDatetime;
	}

	public List<Item> getItems() {
		return items;
	}

}
