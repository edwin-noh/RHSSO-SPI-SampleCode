package com.redhat.spi.sample.restapi;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.credential.CredentialModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.adapter.AbstractUserAdapter;
import org.keycloak.storage.user.UserLookupProvider;

public class CoreUserIntegrationProvider implements UserStorageProvider, UserLookupProvider, CredentialInputValidator {
	
	protected Map<String, UserModel> loadedUsers = new HashMap<>();
	protected KeycloakSession session;
    protected ComponentModel model;
    
	public CoreUserIntegrationProvider(KeycloakSession session, ComponentModel model) {
		this.session = session;
        this.model = model;
	}
	
	public void close() {
		// TODO Auto-generated method stub
	}

	public boolean supportsCredentialType(String credentialType) {
		return credentialType.equals(CredentialModel.PASSWORD);
	}

	public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
		User coreUser = getUserFromCore(user.getUsername());
		String password = coreUser != null ? coreUser.getPwd() : null;
        return credentialType.equals(CredentialModel.PASSWORD) && password != null;
	}

	public boolean isValid(RealmModel realm, UserModel user, CredentialInput input) {
		if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel)) return false;
        UserCredentialModel cred = (UserCredentialModel)input;
        User coreUser = getUserFromCore(user.getUsername());
        String password = coreUser != null ? coreUser.getPwd() : null;
        if (password == null) return false;
        return password.equals(cred.getValue());
	}
	
	@Override
	public UserModel getUserById(String id, RealmModel realm) {
		StorageId storageId = new StorageId(id);
        String username = storageId.getExternalId();
        return getUserByUsername(username, realm);
	}
	
	protected UserModel createAdapter(RealmModel realm, String username) {
        return new AbstractUserAdapter(session, realm, model) {
            @Override
            public String getUsername() {
                return username;
            }
        };
    }

	@Override
	public UserModel getUserByUsername(String username, RealmModel realm) {
		//
		UserModel adapter = loadedUsers.get(username);
        if (adapter == null) {
        	User user = getUserFromCore(username);
            if (user != null) {
                adapter = createAdapter(realm, username);
                loadedUsers.put(username, adapter);
            }
        }
        return adapter;
	}

	@Override
	public UserModel getUserByEmail(String email, RealmModel realm) {
		return null;
	}
	
	private User getUserFromCore(String userId) {
		Response response = null;
		User result = null;
		try {
			ResteasyClient client = new ResteasyClientBuilder().build();
			URI uri = URI.create("http://localhost:8090/camel/user/1");
			MediaType mt = MediaType.APPLICATION_JSON_TYPE;
			ResteasyWebTarget target = client.target(uri);
			target.request(mt);
			response = target.request().get();
			result = response.readEntity(User.class);
			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed request with HTTP status: " + response.getStatus());
			}
		} catch (Exception ex) {
			
		} finally {
			if (response != null) {
				response.close();
			}		
		}
		return result;
	}
}
