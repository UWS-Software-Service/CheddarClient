package com.rusticisoftware.cheddargetter.client;

import org.apache.commons.io.IOUtils;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

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
