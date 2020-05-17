package com.developer.Simple.core;

import java.util.HashMap;
import java.util.Map;

public class ServerResponse {
    public int HttpCode;
    public Map<String, String> responsHeader;
    public byte[] responseBody;

    public ServerResponse(int httpCode) {
        this.HttpCode = httpCode;
        this.responseBody = new byte[0];
        this.responsHeader = new HashMap<>();
    }

    public ServerResponse(int httpCode, Map<String, String> responsHeader, byte[] responseBody) {
        this.HttpCode = httpCode;
        this.responsHeader = responsHeader;
        this.responseBody = responseBody;
    }

    public ServerResponse(int httpCode, byte[] responseBody) {
        this.HttpCode = httpCode;
        this.responseBody = responseBody;
        this.responsHeader = new HashMap<>();
    }

    public ServerResponse(int httpCode, Map<String, String> responsHeader) {
        this.HttpCode = httpCode;
        this.responsHeader = responsHeader;
        this.responseBody = new byte[0];
    }

}
