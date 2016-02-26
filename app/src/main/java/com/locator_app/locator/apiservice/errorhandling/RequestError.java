package com.locator_app.locator.apiservice.errorhandling;

public class RequestError extends Throwable {

    public enum RequestErrorType {
        ServerUnreachable,
        InvalidJson,
        Other;
    }

    private RequestErrorType type;
    public RequestError(RequestErrorType type) {
        this.type = type;
    }

    public RequestErrorType getType() {
        return type;
    }
}
