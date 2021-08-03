package com.fullmetalgalaxy.server.api;

import java.net.URI;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

public class UriInfoMyImpl implements UriInfo {
	HttpServletRequest request = null;

	public UriInfoMyImpl(HttpServletRequest request) {
		this.request = request;
	}

	public String getQueryParameterString(String key, String defaultValue) {
		String value = request.getParameter(key);
		if (value == null) {
			value = defaultValue;
		}
		return value;
	}

	public int getQueryParameterInt(String key, String defaultValue) {
		return Integer.parseInt(getQueryParameterString(key, defaultValue));
	}

	public boolean getQueryParameterBoolean(String key, String defaultValue) {
		return Boolean.parseBoolean(getQueryParameterString(key, defaultValue));
	}

	// ============= UriInfo ================================
	@Override
	public URI getAbsolutePath() {
		throw new RuntimeException("unimplemented");
	}

	@Override
	public UriBuilder getAbsolutePathBuilder() {
		throw new RuntimeException("unimplemented");
	}

	@Override
	public URI getBaseUri() {
		throw new RuntimeException("unimplemented");
	}

	@Override
	public UriBuilder getBaseUriBuilder() {
		throw new RuntimeException("unimplemented");
	}

	@Override
	public List<Object> getMatchedResources() {
		throw new RuntimeException("unimplemented");
	}

	@Override
	public List<String> getMatchedURIs() {
		throw new RuntimeException("unimplemented");
	}

	@Override
	public List<String> getMatchedURIs(boolean arg0) {
		throw new RuntimeException("unimplemented");
	}

	@Override
	public String getPath() {
		throw new RuntimeException("unimplemented");
	}

	@Override
	public String getPath(boolean arg0) {
		throw new RuntimeException("unimplemented");
	}

	@Override
	public MultivaluedMap<String, String> getPathParameters() {
		throw new RuntimeException("unimplemented");
	}

	@Override
	public MultivaluedMap<String, String> getPathParameters(boolean arg0) {
		return getPathParameters();
	}

	@Override
	public List<PathSegment> getPathSegments() {
		throw new RuntimeException("unimplemented");
	}

	@Override
	public List<PathSegment> getPathSegments(boolean arg0) {
		throw new RuntimeException("unimplemented");
	}

	@Override
	public MultivaluedMap<String, String> getQueryParameters() {
		MultivaluedHashMap<String, String> parameters = new MultivaluedHashMap<String, String>();
		for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
			for (String value : entry.getValue()) {
				parameters.add(entry.getKey(), value);
			}
		}
		return parameters;
	}

	@Override
	public MultivaluedMap<String, String> getQueryParameters(boolean arg0) {
		return getQueryParameters();
	}

	@Override
	public URI getRequestUri() {
		throw new RuntimeException("unimplemented");
	}

	@Override
	public UriBuilder getRequestUriBuilder() {
		throw new RuntimeException("unimplemented");
	}

	@Override
	public URI relativize(URI arg0) {
		throw new RuntimeException("unimplemented");
	}

	@Override
	public URI resolve(URI arg0) {
		throw new RuntimeException("unimplemented");
	}

}
