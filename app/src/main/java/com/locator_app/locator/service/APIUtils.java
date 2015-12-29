package com.locator_app.locator.service;

import com.google.gson.Gson;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import retrofit.Response;

public class APIUtils {

    public static Object parseResponse(Response response, Class responseClass) {
        String body = (String) response.body();
        Gson gson = new Gson();
        Object parsedResponse = gson.fromJson(body, responseClass);
        return parsedResponse;
    }

    private static String fromStream(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder out = new StringBuilder();
        final String newLine = System.getProperty("line.separator");

        String line;
        while ((line = reader.readLine()) != null) {
            out.append(line);
            out.append(newLine);
        }
        return out.toString();
    }
}
