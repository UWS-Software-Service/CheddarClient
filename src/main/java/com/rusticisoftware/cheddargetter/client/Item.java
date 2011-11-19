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

import java.util.Date;

import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Item {
	protected @XmlAttribute String id;
	protected @XmlAttribute String code;
	protected @XmlElement String name;
	protected @XmlElement int quantity;
	protected @XmlElement int quantityIncluded;
	protected @XmlElement boolean isPeriodic;
	protected @XmlElement float overageAmount;
	protected Date createdDatetime;
	protected Date modifiedDatetime;

    public Item() {
    }

    public String getId() {
		return id;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public int getQuantity() {
		return quantity;
	}

	public int getQuantityIncluded() {
		return quantityIncluded;
	}

	public boolean isPeriodic() {
		return isPeriodic;
	}

	public float getOverageAmount() {
		return overageAmount;
	}

	public Date getCreatedDatetime() {
		return createdDatetime;
	}

	public Date getModifiedDatetime() {
		return modifiedDatetime;
	}

	public Item(Element elem){
		this.id = elem.getAttribute("id");
		this.code = elem.getAttribute("code");
		this.name = XmlUtils.getNamedElemValue(elem, "name");
		this.quantity = (Integer)XmlUtils.getNamedElemValue(elem, "quantity", Integer.class, 0);
		this.quantityIncluded = (Integer)XmlUtils.getNamedElemValue(elem, "quantityIncluded", Integer.class, 0);
		this.isPeriodic = (Boolean)XmlUtils.getNamedElemValue(elem, "isPeriodic", Boolean.class, false);
		this.overageAmount = (Float)XmlUtils.getNamedElemValue(elem, "overageAmount", Float.class, 0.0f);
		this.createdDatetime = CheddarGetterPaymentService.parseCgDate(XmlUtils.getNamedElemValue(elem, "createdDatetime"));
		this.modifiedDatetime = CheddarGetterPaymentService.parseCgDate(XmlUtils.getNamedElemValue(elem, "modifiedDatetime"));
	}
}
