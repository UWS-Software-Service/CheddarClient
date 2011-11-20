package com.rusticisoftware.cheddargetter.client;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class Error {

    private @XmlAttribute String id;

    private @XmlAttribute String code;

    private @XmlAttribute String auxCode;

    private @XmlValue String message;

    public Error() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAuxCode() {
        return auxCode;
    }

    public void setAuxCode(String auxCode) {
        this.auxCode = auxCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
