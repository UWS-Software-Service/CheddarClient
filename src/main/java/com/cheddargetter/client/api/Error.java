package com.cheddargetter.client.api;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class Error {

    private @XmlAttribute String id;

    private @XmlAttribute int code;

    private @XmlAttribute int auxCode;

    private @XmlValue String message;

    public Error() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getAuxCode() {
        return auxCode;
    }

    public void setAuxCode(int auxCode) {
        this.auxCode = auxCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
