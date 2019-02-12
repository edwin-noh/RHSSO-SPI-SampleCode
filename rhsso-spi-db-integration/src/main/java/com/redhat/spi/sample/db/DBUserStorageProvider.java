package com.redhat.spi.sample.db;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.jboss.logging.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.credential.CredentialModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.cache.CachedUserModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;

@Stateful
@Local(DBUserStorageProvider.class)
public class DBUserStorageProvider implements UserStorageProvider, UserLookupProvider, CredentialInputValidator {

	private static final Logger logger = Logger.getLogger(DBUserStorageProvider.class);
	public static final String PASSWORD_CACHE_KEY = UserAdapter.class.getName() + ".password";

	@PersistenceContext
	protected EntityManager em;

	protected ComponentModel model;
	protected KeycloakSession session;

	public void setModel(ComponentModel model) {
		this.model = model;
	}

	public void setSession(KeycloakSession session) {
		this.session = session;
	}

	@Remove
	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean supportsCredentialType(String credentialType) {
		return CredentialModel.PASSWORD.equals(credentialType);
	}

	@Override
	public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
		return supportsCredentialType(credentialType) && getPassword(user) != null;
	}

	@Override
	public boolean isValid(RealmModel realm, UserModel user, CredentialInput input) {
		if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel))
			return false;
		UserCredentialModel cred = (UserCredentialModel) input;
		String password = getPassword(user);
		return password != null && password.equals(cred.getValue());
	}

	public String getPassword(UserModel user) {
		String password = null;
		if (user instanceof CachedUserModel) {
			password = (String) ((CachedUserModel) user).getCachedWith().get(PASSWORD_CACHE_KEY);
		} else if (user instanceof UserAdapter) {
			password = ((UserAdapter) user).getPassword();
		}
		return password;
	}

	@Override
	public UserModel getUserById(String id, RealmModel realm) {
		//
		logger.info("getUserById: " + id);
		String persistenceId = StorageId.externalId(id);
		UserEntity entity = em.find(UserEntity.class, persistenceId);
		if (entity == null) {
			logger.info("could not find user by id: " + id);
			return null;
		}
		return new UserAdapter(session, realm, model, entity);
	}

	@Override
	public UserModel getUserByUsername(String username, RealmModel realm) {
		//
		logger.info("getUserByUsername: " + username);
		TypedQuery<UserEntity> query = em.createNamedQuery("getUserByUsername", UserEntity.class);
		query.setParameter("name", username);
		List<UserEntity> result = query.getResultList();
		if (result.isEmpty()) {
			logger.info("could not find username: " + username);
			return null;
		}
		return new UserAdapter(session, realm, model, result.get(0));
	}

	@Override
	public UserModel getUserByEmail(String email, RealmModel realm) {
		// Implement this method if email authentication is needed
		return null;
	}

}
