/*
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (c) Alkacon Software GmbH (http://www.alkacon.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * For further information about Alkacon Software GmbH, please see the
 * company website: http://www.alkacon.com
 *
 * For further information about OpenCms, please see the
 * project website: http://www.opencms.org
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.opencms.jsp.util;

import org.opencms.file.CmsFile;
import org.opencms.file.CmsObject;
import org.opencms.test.OpenCmsTestCase;
import org.opencms.test.OpenCmsTestProperties;
import org.opencms.xml.content.CmsXmlContent;
import org.opencms.xml.content.CmsXmlContentFactory;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Unit tests for the <code>{@link CmsJspContentAccessBean}</code>.<p>
 * 
 * @since 7.0.2
 */
public class TestCmsJspContentAccessBean extends OpenCmsTestCase {

    /**
     * Default JUnit constructor.<p>
     * 
     * @param arg0 JUnit parameters
     */
    public TestCmsJspContentAccessBean(String arg0) {

        super(arg0);
    }

    /**
     * Test suite for this test class.<p>
     * 
     * @return the test suite
     */
    public static Test suite() {

        OpenCmsTestProperties.initialize(org.opencms.test.AllTests.TEST_PROPERTIES_PATH);

        TestSuite suite = new TestSuite();
        suite.setName(TestCmsJspContentAccessBean.class.getName());

        suite.addTest(new TestCmsJspContentAccessBean("testContentAccess"));

        TestSetup wrapper = new TestSetup(suite) {

            @Override
            protected void setUp() {

                setupOpenCms("simpletest", "/");
            }

            @Override
            protected void tearDown() {

                removeOpenCms();
            }
        };

        return wrapper;
    }

    /**
     * Tests general content access for XML content.<p>
     * 
     * @throws Exception if the test fails
     */
    public void testContentAccess() throws Exception {

        CmsObject cms = getCmsObject();

        // first read the XML content 
        CmsFile file = cms.readFile("/xmlcontent/article_0002.html");
        // need to set URI for macro resolver to work
        cms.getRequestContext().setUri("/xmlcontent/article_0002.html");
        CmsXmlContent content = CmsXmlContentFactory.unmarshal(cms, file);

        // new create the content access bean
        CmsJspContentAccessBean bean = new CmsJspContentAccessBean(cms, Locale.ENGLISH, content);

        // now for some fun with the bean
        assertSame(file, bean.getFile());

        // some simple has / has not checks
        Map hasValue = bean.getHasValue();
        assertSame(Boolean.TRUE, hasValue.get("Title"));
        assertSame(Boolean.FALSE, hasValue.get("IdontExistHere"));

        // check which kind of locales we have (should be "en" and "de")
        Map hasLocale = bean.getHasLocale();
        assertSame(Boolean.TRUE, hasLocale.get("en"));
        assertSame(Boolean.TRUE, hasLocale.get("de"));
        assertSame(Boolean.FALSE, hasLocale.get("fr"));

        // access the content form the default locale
        Map enValue = bean.getValue();
        assertEquals("This is the article 2 sample", String.valueOf(enValue.get("Title")));

        // now access the content from a selected locale
        Map localeValue = bean.getLocaleValue();
        Map deValue = (Map)localeValue.get("de");
        assertEquals("Das ist Artikel 2", String.valueOf(deValue.get("Title")));
        enValue = (Map)localeValue.get("en");
        assertEquals("This is the article 2 sample", String.valueOf(enValue.get("Title")));
        Map frValue = (Map)localeValue.get("fr");
        assertFalse(((CmsJspContentAccessValueWrapper)frValue.get("Title")).getExists());

        // check list access to default locale
        Map enValues = bean.getValueList();
        assertEquals(2, ((List)enValues.get("Teaser")).size());
        assertEquals("This is teaser 2 in sample article 2.", String.valueOf(((List)enValues.get("Teaser")).get(1)));

        // now check list access to selected locale
        Map localeValues = bean.getLocaleValueList();
        Map deValues = (Map)localeValues.get("de");
        assertEquals(3, ((List)deValues.get("Teaser")).size());

        // check macro resolving of the Title property
        assertEquals(
            "This is the article 2 sample",
            String.valueOf(((CmsJspContentAccessValueWrapper)((List)deValues.get("Teaser")).get(2)).getResolveMacros()));

        enValues = (Map)localeValues.get("en");
        assertEquals(2, ((List)enValues.get("Teaser")).size());
        assertEquals("This is teaser 2 in sample article 2.", String.valueOf(((List)enValues.get("Teaser")).get(1)));
        Map frValues = (Map)localeValues.get("fr");
        assertEquals(0, ((List)frValues.get("Title")).size());

        // check random access to any object
        CmsJspContentAccessValueWrapper val = (CmsJspContentAccessValueWrapper)enValue.get("i/do/no/exists");
        assertFalse(val.getExists());
        assertTrue(val.getIsEmpty());
        assertTrue(val.getIsEmptyOrWhitespaceOnly());
        assertEquals("", val.toString());
        assertEquals("", val.getPath());
        assertEquals(0, val.getValueList().size());
        assertEquals(0, val.getHasValue().size());
        assertEquals(0, val.getValue().size());

        // create the content access bean with a locale that is not available, so a fall back should be used
        bean = new CmsJspContentAccessBean(cms, Locale.FRENCH, content);

        // check list access to default locale English 
        frValues = bean.getValueList();
        assertEquals(2, ((List)frValues.get("Teaser")).size());
        assertEquals("This is teaser 2 in sample article 2.", String.valueOf(((List)enValues.get("Teaser")).get(1)));
    }
}