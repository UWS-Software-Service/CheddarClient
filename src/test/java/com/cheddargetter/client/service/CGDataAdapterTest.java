package com.cheddargetter.client.service;

import com.cheddargetter.client.service.CGDateAdapter;
import org.testng.annotations.Test;

import java.util.Date;

import static javax.xml.bind.JAXBContext.newInstance;
import static org.testng.Assert.assertEquals;

@Test
public class CGDataAdapterTest {

    public static final String DATE_STRING = "2009-10-01T019:24:10+00:00";
    public static final Date DATE = new Date(109, 9, 1, 21, 24, 10);
    public static final String ERROR_XML = "/error.xml";

    @Test
    public void testGetDateFromString() throws Exception {
        CGDateAdapter adapter = new CGDateAdapter();
        Date result = adapter.unmarshal(DATE_STRING);
        assertEquals(result, DATE);
    }

    @Test
    public void testGetStringFromDate() throws Exception {
        CGDateAdapter adapter = new CGDateAdapter();
        String result = adapter.marshal(DATE);
        assertEquals(result, DATE_STRING);
    }

}
