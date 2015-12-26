package com.locator_app.locator.service;

import com.locator_app.locator.model.Login;
import com.locator_app.locator.util.Server;


import org.androidannotations.annotations.rest.Get;
import org.androidannotations.annotations.rest.Post;
import org.androidannotations.annotations.rest.Rest;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.List;

@Rest(rootUrl = Server.apiUrl, converters = MappingJackson2HttpMessageConverter.class)
public interface LoginService {
    @Post("/login")
    Login login();
}
