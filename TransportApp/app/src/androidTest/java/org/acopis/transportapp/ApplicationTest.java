package org.acopis.transportapp;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.test.InstrumentationTestCase;

import org.acopis.transportapp.json.TransportJSONParser;
import org.acopis.transportapp.model.Provider;
import org.acopis.transportapp.model.QueryResponse;
import org.acopis.transportapp.model.Route;

import java.io.IOException;
import java.util.List;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends InstrumentationTestCase {

    private final String SAMPLE_FILE_NAME = "sample.json";

    public ApplicationTest() {

    }

    public void testJSONParser() {
        TransportJSONParser parser = TransportJSONParser.getInstance();

        QueryResponse testQueryResponse = parser.parseQueryResponse(null);
        assertNull(testQueryResponse);

        try {
            testQueryResponse = parser.parseQueryResponse(getInstrumentation().getContext().getAssets().open(SAMPLE_FILE_NAME));
            List<Route> routes = testQueryResponse.getRoutes();
            assertEquals(9,routes.size());
            List<Provider> providers = testQueryResponse.getProviders();
            assertEquals(6,providers.size());
        } catch (IOException e) {
            fail("Error on opening the test sample file.");
        }
    }
}