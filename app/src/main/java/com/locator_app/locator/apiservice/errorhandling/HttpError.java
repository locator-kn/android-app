package com.locator_app.locator.apiservice.errorhandling;


import java.util.HashMap;
import java.util.Map;

public class HttpError extends Throwable {

    public enum HttpErrorCode {
        badRequest(400),
        unauthorized(401),
        forbidden(403),
        notFound(404),
        requestTimeout(408),
        conflict(409),
        preconditionFailed(412),
        unsupportedMediaType(415),
        unknown(-1);

        private final int code;
        HttpErrorCode(int code) {
            this.code = code;
        }

        private static Map<Integer, HttpErrorCode> map = new HashMap<>();

        static {
            for (HttpErrorCode httpErrorCode: HttpErrorCode.values()) {
                map.put(httpErrorCode.code, httpErrorCode);
            }
        }

        public static HttpErrorCode get(int code) {
            if (map.containsKey(code)) {
                return map.get(code);
            }
            return unknown;
        }
    }

    private HttpErrorCode httpErrorCode;

    public HttpError(int httpCode) {
        httpErrorCode = HttpErrorCode.get(httpCode);
    }

    public HttpErrorCode getErrorCode() {
        return httpErrorCode;
    }
}
