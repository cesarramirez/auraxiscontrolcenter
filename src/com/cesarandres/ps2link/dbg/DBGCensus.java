package com.cesarandres.ps2link.dbg;

import java.net.MalformedURLException;
import java.net.URL;

import android.util.Log;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.cesarandres.ps2link.ApplicationPS2Link;
import com.cesarandres.ps2link.dbg.util.Collections.PS2Collection;
import com.cesarandres.ps2link.dbg.util.Logger;
import com.cesarandres.ps2link.dbg.util.QueryString;
import com.cesarandres.ps2link.dbg.volley.GsonRequest;

/**
 * 
 * 
 * This class will be in charge of formatting requests for DBG Census API and
 * retrieving the information. You can use the response directly from JSON or
 * they can be also automatically converted to objects to ease their
 * manipulation.
 * 
 * API Calls follow the following format:
 * /verb/game/collection/[identifier]?[queryString]
 * 
 * This class is been designed by following the design specified on
 * http://census.daybreakgames.com/.
 */

public class DBGCensus {

	public static final String SERVICE_ID = "s:PS2Link";
    public static final String ENDPOINT_URL = "http://census.daybreakgames.com";
    public static final String IMG = "img";
    public static final String ITEM = "item";

    public static Namespace currentNamespace = Namespace.PS2PC;
    
    public static enum Verb {
	GET("get"), COUNT("count");

	private final String verb;

	private Verb(final String verb) {
	    this.verb = verb;
	}

	@Override
	public String toString() {
	    return this.verb;
	}
    }
    
    public static enum Namespace {
	PS2PC("ps2:v2"), 
	PS2PS4US("ps2ps4us:v2"),
	PS2PS4EU("ps2ps4eu:v2");

	private final String namespace;

	private Namespace(final String namespace) {
	    this.namespace = namespace;
	}

	@Override
	public String toString() {
	    return this.namespace;
	}
    }

    public static enum ImageType {
	PAPERDOLL("paperdoll"), HEADSHOT("headshot");

	private final String imagetype;

	private ImageType(final String imagetype) {
	    this.imagetype = imagetype;
	}

	@Override
	public String toString() {
	    return this.imagetype;
	}
    }

    /**
     * @param verb
     *            action to realize, count or get
     * @param collection
     *            resource collection to retrieve
     * @param identifier
     *            id of the resource
     * @param query
     *            query with parameters for the search
     * @return the url to retrieve the requested resource
     */
    public static URL generateGameDataRequest(Verb verb, PS2Collection collection, String identifier, QueryString query) {
	if (identifier == null) {
	    identifier = "";
	}
	if (query == null) {
	    query = new QueryString();
	}
	URL requestDataURL = null;
	try {
	    requestDataURL = new URL(ENDPOINT_URL + "/" + SERVICE_ID + "/" + verb.toString() + "/" + DBGCensus.currentNamespace + "/" + collection.toString() + "/"
		    + identifier + "?" + query.toString());
	} catch (MalformedURLException e) {
	    Logger.log(Log.ERROR, "DBGCensus", "There was a problem creating the URL");
	}
	return requestDataURL;
    }

    /**
     * @param urlParams that will be attached to the end of the default request body.
     * 
     * @return url to retrieve the requested resource
     */
    public static URL generateGameDataRequest(String urlParams) {
	URL requestDataURL = null;
	try {
	    requestDataURL = new URL(ENDPOINT_URL + "/" + SERVICE_ID + "/" + Verb.GET + "/" + DBGCensus.currentNamespace + "/" + urlParams);
	} catch (MalformedURLException e) {
	    Logger.log(Log.ERROR, "DBGCensus", "There was a problem creating the URL");
	}
	return requestDataURL;
    }
    
    /**
     * @param url
     *            the url to request
     * @param responseClass
     *            the class to which retrieve data will be serialized into
     * @param success
     *            run this on success
     * @param error
     *            run this when the request fails
     * @param caller
     *            this is used to tag the call. Usually a fragment or activity
     *            is a good tag
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void sendGsonRequest(String url, Class responseClass, Listener success, ErrorListener error, Object caller) {
	GsonRequest gsonOject = new GsonRequest(url, responseClass, null, success, error);
	gsonOject.setTag(caller);
	ApplicationPS2Link.volley.add(gsonOject);
    }
}
