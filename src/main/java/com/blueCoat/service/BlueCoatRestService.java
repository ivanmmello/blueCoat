package com.blueCoat.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.ws.client.core.WebServiceTemplate;

@Service
public interface BlueCoatRestService {

	RestTemplate getRestTemplate(String credentials);
	
	WebServiceTemplate getWebServiceTemplate(String credentials, WebServiceTemplate webServiceTemplate);
	
	InputStream getInputStream(String url) throws MalformedURLException, IOException;
	
}
