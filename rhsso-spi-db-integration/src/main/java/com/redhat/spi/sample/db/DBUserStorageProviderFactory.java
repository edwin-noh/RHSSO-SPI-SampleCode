package com.redhat.spi.sample.db;

import javax.naming.InitialContext;

import org.jboss.logging.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.storage.UserStorageProviderFactory;

public class DBUserStorageProviderFactory implements UserStorageProviderFactory<DBUserStorageProvider> {
	
	private static final Logger logger = Logger.getLogger(DBUserStorageProviderFactory.class);
	
	@Override
	public DBUserStorageProvider create(KeycloakSession session, ComponentModel model) {
		try {
            InitialContext ctx = new InitialContext();
            DBUserStorageProvider provider = (DBUserStorageProvider)ctx.lookup("java:global/db-user-storage-sample/" + DBUserStorageProvider.class.getSimpleName());
            provider.setModel(model);
            provider.setSession(session);
            return provider;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
	}

	@Override
    public String getId() {
        return "db-user-storage-sample";
    }

    @Override
    public String getHelpText() {
        return "DB User Storage Provider with JPA Sample";
    }

    @Override
    public void close() {
        logger.info("<<<<<< Closing factory");
    }
}
