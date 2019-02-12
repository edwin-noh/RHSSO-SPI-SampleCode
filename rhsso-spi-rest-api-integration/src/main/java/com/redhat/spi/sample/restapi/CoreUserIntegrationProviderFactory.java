package com.redhat.spi.sample.restapi;

import org.keycloak.Config;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.storage.UserStorageProviderFactory;

public class CoreUserIntegrationProviderFactory implements UserStorageProviderFactory<CoreUserIntegrationProvider> {
	
	public static final String PROVIDER_NAME = "core-user-if";
	
	@Override
    public void init(Config.Scope config) {
		//Check connectivity and create connection pool
    }
	
	@Override
	public CoreUserIntegrationProvider create(KeycloakSession session, ComponentModel model) {
		//
		return new CoreUserIntegrationProvider(session, model);
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return PROVIDER_NAME;
	}
}
