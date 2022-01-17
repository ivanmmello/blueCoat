package com.blueCoat.service.Impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

import static com.blueCoat.enuns.BlueCoatEnum.*;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import com.blueCoat.service.BlueCoatRestService;

@Service
public class BlueCoatRestServiceImpl implements BlueCoatRestService{
	
	@Value("{bluecoat.url}")
	private String proxyUrl;
	
	@Value("{bluecoat.port}")
	private String proxyPort;
	
	@Value("{bluecoat.user}")
	private String user;
	
	@Value("{bluecoat.pass}")
	private String pass;

	 @Override

     public RestTemplate getRestTemplate(String credentials) {



            if (credentials == null || credentials.equals("") || credentials.equalsIgnoreCase(NO_PROXY.name()))

                  return new RestTemplate();



            if (credentials.equalsIgnoreCase(CREDENTIALS.name()))

                  return this.getRestTemplateWithCredentials(proxyUrl, Integer.parseInt(proxyPort), user, pass);



            if (credentials.equalsIgnoreCase(NO_CREDENTIALS.name()))

                  return this.getRestTemplateWithoutCredenciais(proxyUrl, Integer.parseInt(proxyPort));



            return new RestTemplate();

     }



     @Override

     public WebServiceTemplate getWebServiceTemplate(String credentials, WebServiceTemplate webServiceTemplate) {



            if (credentials == null || credentials.equals("") || credentials.equalsIgnoreCase(NO_PROXY.name())

                         || credentials.equalsIgnoreCase(CREDENTIALS.name()))

                  return webServiceTemplate;



            HttpClientBuilder builder = HttpClientBuilder.create();

            builder.addInterceptorFirst(new HttpComponentsMessageSender.RemoveSoapHeadersInterceptor());

            HttpHost proxy = new HttpHost(proxyUrl, Integer.parseInt(proxyPort));

            builder.setProxy(proxy);



            CloseableHttpClient httpClient = builder.build();



            HttpComponentsMessageSender messageSender = new HttpComponentsMessageSender(httpClient);

           webServiceTemplate.setMessageSender(messageSender);



            return webServiceTemplate;

     }



     private RestTemplate getRestTemplateWithoutCredenciais(String url, Integer port) {

            RestTemplate restTemplate = new RestTemplate();

            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

            InetSocketAddress address = new InetSocketAddress(url, port);

            Proxy proxy = new Proxy(Proxy.Type.HTTP, address);

            factory.setProxy(proxy);

            restTemplate.setRequestFactory(factory);

            return restTemplate;

     }



     private RestTemplate getRestTemplateWithCredentials(String proxy, Integer port, String user, String pass) {

            RestTemplate restTemplate = new RestTemplate();

          CredentialsProvider credsProvider = new BasicCredentialsProvider();

         credsProvider.setCredentials(new AuthScope(proxy, port), new UsernamePasswordCredentials(user, pass));

         HttpClientBuilder clientBuilder = HttpClientBuilder.create();

          clientBuilder.useSystemProperties();

          clientBuilder.setProxy(new HttpHost(proxy, port));

      clientBuilder.setDefaultCredentialsProvider(credsProvider);

          clientBuilder.setProxyAuthenticationStrategy(new ProxyAuthenticationStrategy());

          CloseableHttpClient client = clientBuilder.build();

          HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();

          factory.setHttpClient(client);

          restTemplate.setRequestFactory(factory);

            return restTemplate;

     }



     @Override

     public InputStream getInputStream(String url) throws MalformedURLException, IOException {

            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyUrl, Integer.parseInt(proxyPort)));

            URLConnection connection = new URL(url).openConnection(proxy);//remover o proxy quando for utilizar local

            return connection.getInputStream();

     }



}