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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
public class Customer implements Serializable {
    
    protected @XmlAttribute String id;
	protected @XmlAttribute String code;
	protected @XmlElement String firstName;
	protected @XmlElement String lastName;
	protected @XmlElement String company;
	protected @XmlElement String email;
	protected @XmlElement String gatewayToken;
	protected @XmlJavaTypeAdapter(CGDateAdapter.class) Date createdDatetime;
	protected @XmlJavaTypeAdapter(CGDateAdapter.class) Date modifiedDatetime;

    @XmlElementWrapper(name = "subscriptions")
    @XmlElement(name = "subscription")
	protected List<Subscription> subscriptions = new ArrayList<Subscription>();

	public String getId() {
		return id;
	}

	public String getCode() {
		return code;
	}

    
	public String getFirstName() {
		return firstName;
	}

    
	public String getLastName() {
		return lastName;
	}

    
	public String getCompany() {
		return company;
	}

    
	public String getEmail() {
		return email;
	}

    
	public String getGatewayToken() {
		return gatewayToken;
	}

	public Date getCreatedDatetime() {
		return createdDatetime;
	}

	public Date getModifiedDatetime() {
		return modifiedDatetime;
	}

	public List<Subscription> getSubscriptions() {
		return subscriptions;
	}

    public Customer() {
    }

}
