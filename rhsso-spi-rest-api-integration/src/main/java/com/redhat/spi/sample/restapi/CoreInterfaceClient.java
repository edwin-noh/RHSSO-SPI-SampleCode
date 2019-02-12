package com.redhat.spi.sample.restapi;

import java.net.URI;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

public class CoreInterfaceClient {

	public void requestAuth() {
		ResteasyClient client = new ResteasyClientBuilder().build();
		URI uri = URI.create("http://localhost:8080/camel/user/1");
		MediaType mt = MediaType.APPLICATION_JSON_TYPE;
		ResteasyWebTarget target = client.target(uri);
		target.request(mt);
		Response response = target.request().get();
		User result = response.readEntity(User.class);
		response.close();
		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed request with HTTP status: " + response.getStatus());
		}
	}
	
	public static void main(String[] args) {
		CoreInterfaceClient cl = new CoreInterfaceClient();
		cl.requestAuth();
	}
}
