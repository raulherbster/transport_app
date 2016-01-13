package org.acopis.transportapp.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by herbster on 1/12/16.
 */
public class QueryResponse {

    public List<Route> mRoutesList;
    public List<Provider> mProvidersList;

    public QueryResponse() {
        mRoutesList = new ArrayList<Route>();
        mProvidersList = new ArrayList<Provider>();
    }

    public synchronized boolean addRoute(Route route) {
        return mRoutesList.add(route);
    }

    public synchronized boolean removeRoute(Route route) {
        return mRoutesList.remove(route);
    }

    public List<Route> getRoutes() {
        return mRoutesList;
    }

    public synchronized boolean addProvider(Provider provider) {
        return mProvidersList.add(provider);
    }

    public synchronized boolean removeProvider(Provider provider) {
        return mProvidersList.remove(provider);
    }

    public List<Provider> getProviders() {
        return mProvidersList;
    }

}
