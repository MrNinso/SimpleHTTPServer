package com.developer.Simple.core;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ServerResponse {
    public HTTPCodes HttpCode;
    public Map<String, String> responsHeader;
    public InputStream responseBody;

    public ServerResponse(HTTPCodes httpCode) {
        this.HttpCode = httpCode;
        this.responseBody = new ByteArrayInputStream(new byte[0]);
        this.responsHeader = new HashMap<>();
    }

    public ServerResponse(HTTPCodes httpCode, Map<String, String> responsHeader, byte[] responseBody) {
        this.HttpCode = httpCode;
        this.responsHeader = responsHeader;
        this.responseBody = new ByteArrayInputStream(responseBody);
    }

    public ServerResponse(HTTPCodes httpCode, byte[] responseBody) {
        this.HttpCode = httpCode;
        this.responseBody = new ByteArrayInputStream(responseBody);
        this.responsHeader = new HashMap<>();
    }

    public ServerResponse(HTTPCodes httpCode, Map<String, String> responsHeader) {
        this.HttpCode = httpCode;
        this.responsHeader = responsHeader;
        this.responseBody = new ByteArrayInputStream(new byte[0]);
    }

    public ServerResponse(HTTPCodes httpCode, InputStream responseBody) {
        this.HttpCode = httpCode;
        this.responseBody = responseBody;
    }

    public ServerResponse(HTTPCodes httpCode, Map<String, String> responsHeader, InputStream responseBody) {
        this.HttpCode = httpCode;
        this.responsHeader = responsHeader;
        this.responseBody = responseBody;
    }
}
