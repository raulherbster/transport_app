package org.acopis.transportapp.json;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import org.acopis.transportapp.model.Company;
import org.acopis.transportapp.model.Price;
import org.acopis.transportapp.model.Provider;
import org.acopis.transportapp.model.QueryResponse;
import org.acopis.transportapp.model.Route;
import org.acopis.transportapp.model.Segment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by herbster on 1/12/16.
 */
public class TransportJSONParser {

    public static final String TAG_ROUTES = "routes";
    public static final String TAG_PROVIDER_ATTRIBUTES = "provider_attributes";
    public static final String TAG_DISCLAIMER = "disclaimer";
    public static final String TAG_PRICE = "price";
    public static final String TAG_SEGMENTS = "segments";
    public static final String TAG_PROVIDER = "provider";
    public static final String TAG_TYPE = "type";
    public static final String TAG_CURRENCY = "currency";
    public static final String TAG_AMOUNT = "amount";
    public static final String TAG_ID = "id";
    public static final String TAG_ADDRESS = "address";
    public static final String TAG_MODEL = "model";
    public static final String TAG_DOORS = "doors";
    public static final String TAG_COMPANIES = "companies";
    public static final String TAG_SEATS = "seats";
    public static final String TAG_INTERNAL_CLEANLINESS = "internal_cleanliness";
    public static final String TAG_FUEL_LEVEL = "fuel_level";
    public static final String TAG_ENGINE_TYPE = "engine_type";
    public static final String TAG_LICENSE_PLATE = "license_plate";
    public static final String TAG_AVAILABLE_BIKES = "available_bikes";
    public static final String TAG_PHONE = "phone";
    public static final String TAG_NUM_STOPS = "num_stops";
    public static final String TAG_STOPS = "stops";
    public static final String TAG_POLYLINE = "polyline";
    public static final String TAG_ICON_URL = "icon_url";
    public static final String TAG_COLOR = "color";
    public static final String TAG_DESCRIPTION = "description";
    public static final String TAG_TRAVEL_MODE = "travel_mode";
    public static final String TAG_LAT = "lat";
    public static final String TAG_LNG = "lng";
    public static final String TAG_DATETIME = "datetime";
    public static final String TAG_NAME = "name";
    public static final String TAG_PROPERTIES = "properties";
    public static final String TAG_DISPLAY_NAME = "display_name";
    public static final String TAG_ANDROID_PACKAGE_NAME = "android_package_name";
    public static final String TAG_IOS_APP_URL = "ios_app_url";
    public static final String TAG_IOS_ITUNES_URL = "ios_itunes_url";
    public static final String TAG_PROVIDER_ICON_URL = "provider_icon_url";

    private final String TAG = "JSONParser";

    private static TransportJSONParser singleton = null;

    private List<String> mProviderNameList;

    private TransportJSONParser() {
        mProviderNameList = new ArrayList<String>();
    }

    public synchronized static TransportJSONParser getInstance() {
        if (singleton == null)
            singleton = new TransportJSONParser();
        return singleton;
    }

    public QueryResponse parseQueryResponse(InputStream in) {
        if (in == null)
            return null;

        QueryResponse response = new QueryResponse();
        JsonReader jsonReader = new JsonReader(new InputStreamReader(in));
        try {
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String name = jsonReader.nextName();
                if (name.equals(TAG_ROUTES)) {
                    List<Route> routes = readRoutes(jsonReader);
                    for (Route route : routes)
                        response.addRoute(route);
                } else if (name.equals(TAG_PROVIDER_ATTRIBUTES)) {
                    List<Provider> providers = readProviders(jsonReader);
                    for (Provider provider : providers)
                        response.addProvider(provider);
                }
            }
            jsonReader.endObject();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return response;
    }

    private List<Route> readRoutes(JsonReader reader) throws IOException {
        List<Route> routes = new ArrayList<Route>();

        reader.beginArray();
        while (reader.hasNext()) {
            routes.add(readerSingleRoute(reader));
        }
        reader.endArray();

        return routes;
    }

    private List<Provider> readProviders(JsonReader reader) throws IOException {
        List<Provider> providers = new ArrayList<Provider>();

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (mProviderNameList.contains(name)) {
                providers.add(readSingleProvider(reader));
            }
        }
        reader.endObject();
        return providers;
    }

    private Provider readSingleProvider(JsonReader reader) throws IOException {
        Provider provider = new Provider();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals(TAG_PROVIDER_ICON_URL)) {
                checkAndReadString(reader);
            } else if (name.equals(TAG_DISCLAIMER)) {
                checkAndReadString(reader);
            } else if (name.equals(TAG_IOS_ITUNES_URL)) {
                checkAndReadString(reader);
            } else if (name.equals(TAG_IOS_APP_URL)) {
                checkAndReadString(reader);
            } else if (name.equals(TAG_ANDROID_PACKAGE_NAME)) {
                checkAndReadString(reader);
            } else if (name.equals(TAG_DISPLAY_NAME)) {
                checkAndReadString(reader);
            }
        }
        reader.endObject();
        return provider;
    }

    private Route readerSingleRoute(JsonReader reader) throws IOException {
        Route route = new Route();

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals(TAG_TYPE)) {
                checkAndReadString(reader);
            } else if (name.equals(TAG_PROVIDER)) {
                String providerName = checkAndReadString(reader);
                if (providerName != null)
                    mProviderNameList.add(providerName);
            } else if (name.equals(TAG_SEGMENTS)) {
                readSegments(reader);
            } else if (name.equals(TAG_PROPERTIES)) {
                readProperties(reader);
            } else if (name.equals(TAG_PRICE)) {
                readPrice(reader);
            }
        }
        reader.endObject();
        return route;
    }

    private Price readPrice(JsonReader reader) throws IOException {
        if (checkNextNull(reader)) {
            return null;
        } else {
            reader.beginObject();
            Price price = new Price();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals(TAG_CURRENCY)) {
                    checkAndReadString(reader);
                } else if (name.equals(TAG_AMOUNT)) {
                    reader.nextInt();
                }
            }
            reader.endObject();
            return price;
        }
    }

    private boolean checkNextNull(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return true;
        } else
            return false;

    }

    private Properties readProperties(JsonReader reader) throws IOException {
        if (checkNextNull(reader)) {
            return null;
        } else {
            Properties properties = new Properties();
            reader.beginObject();
                while (reader.hasNext()) {
                    String name = reader.nextName();
                    if (name.equals(TAG_ID)) {
                        checkAndReadString(reader);
                    } else if (name.equals(TAG_AVAILABLE_BIKES)) {
                        reader.nextInt();
                    } else if (name.equals(TAG_ADDRESS)) {
                        checkAndReadString(reader);
                    } else if (name.equals(TAG_MODEL)) {
                        checkAndReadString(reader);
                    } else if (name.equals(TAG_LICENSE_PLATE)) {
                        checkAndReadString(reader);
                    } else if (name.equals(TAG_ENGINE_TYPE)) {
                        checkAndReadString(reader);
                    } else if (name.equals(TAG_FUEL_LEVEL)) {
                        reader.nextInt();
                    } else if (name.equals(TAG_INTERNAL_CLEANLINESS)) {
                        checkAndReadString(reader);
                    } else if (name.equals(TAG_DESCRIPTION)) {
                        checkAndReadString(reader);
                    } else if (name.equals(TAG_SEATS)) {
                        checkAndReadString(reader);
                    } else if (name.equals(TAG_DOORS)) {
                        checkAndReadString(reader);
                    } else if (name.equals(TAG_COMPANIES)) {
                        readCompanies(reader);
                    }
                }
            reader.endObject();
            return properties;
        }
    }

    private List<Segment> readSegments(JsonReader reader) throws IOException {
        reader.beginArray();
        while (reader.hasNext()) {
            readSingleSegment(reader);
        }
        reader.endArray();
        return null;
    }

    private List<Company> readCompanies(JsonReader reader) throws IOException {
        reader.beginArray();
        while (reader.hasNext()) {
            readSingleCompany(reader);
        }
        reader.endArray();
        return null;
    }

    private void readSingleCompany(JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals(TAG_NAME)) {
                checkAndReadString(reader);
            } else if (name.equals(TAG_PHONE)) {
                checkAndReadString(reader);
            }
        }
        reader.endObject();
    }

    private void readSingleSegment(JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals(TAG_NAME)) {
                checkAndReadString(reader);
            } else if (name.equals(TAG_NUM_STOPS)) {
                reader.nextInt();
            } else if (name.equals(TAG_STOPS)) {
                readStops(reader);
            } else if (name.equals(TAG_TRAVEL_MODE)) {
                checkAndReadString(reader);
            } else if (name.equals(TAG_DESCRIPTION)) {
                checkAndReadString(reader);
            } else if (name.equals(TAG_COLOR)) {
                checkAndReadString(reader);
            } else if (name.equals(TAG_ICON_URL)) {
                checkAndReadString(reader);
            } else if (name.equals(TAG_POLYLINE)) {
                checkAndReadString(reader);
            }
        }
        reader.endObject();
    }

    private void readStops(JsonReader reader) throws IOException {
        reader.beginArray();
        while (reader.hasNext()) {
            readSingleStop(reader);
        }
        reader.endArray();
    }

    private void readSingleStop(JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals(TAG_LAT)) {
                reader.nextDouble();
            } else if (name.equals(TAG_LNG)) {
                reader.nextDouble();
            } else if (name.equals(TAG_DATETIME)) {
                checkAndReadString(reader);
            } else if (name.equals(TAG_NAME)) {
                checkAndReadString(reader);
            } else if (name.equals(TAG_PROPERTIES)) {
                checkAndReadString(reader);
            }
        }
        reader.endObject();
    }

    private Provider readerSingleProvider(JsonReader reader) throws IOException {
        reader.beginObject();
        Provider provider = new Provider();
        reader.endObject();
        return provider;
    }

    private String checkAndReadString(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        } else
            return reader.nextString();
    }

}
