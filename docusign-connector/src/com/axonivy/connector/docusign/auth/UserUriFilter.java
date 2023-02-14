package com.axonivy.connector.docusign.auth;

import java.io.IOException;
import java.net.URI;
import java.text.MessageFormat;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;

import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.rest.client.oauth2.uri.OAuth2UriProperty;
import ch.ivyteam.ivy.security.ISession;

/**
 * Customizes the baseURI according to the 'userInfo'.
 *
 * <p>
 * See: <b>Step 3. Get your user's base URI</b>
 * https://developers.docusign.com/platform/auth/authcode/authcode-get-token/
 * </p>
 *
 * @since 9.2
 */
@Provider
public class UserUriFilter implements javax.ws.rs.client.ClientRequestFilter {
	/**
	 * Placeholder to replace with account id. Note: the value here
	 * must match the word configured in the Rest client property
	 * PATH.accountId.
	 */
	public static final String ACCOUNT_ID_PLACEHOLDER = "placeholder"; 

	private static final String USER_INFO = "docusign.userInfo";

	private final ISession session;
	private final OAuth2UriProperty uriFactory;

	public UserUriFilter(ISession session, OAuth2UriProperty uriFactory) {
		this.session = session;
		this.uriFactory = uriFactory;
	}

	@Override
	public void filter(ClientRequestContext context) throws IOException {
		if (uriFactory.isAuthRequest(context.getUri())) { // do not intercept token
			// request: avoid
			// stackOverFlow!
			return;
		}

		JsonNode userInfo = readUserInfo(session);
		if (userInfo == null) {
			userInfo = context.getClient()
					.target(uriFactory.getUri("userinfo"))
					.request()
					.headers(context.getHeaders()) // copy bearer token
					.get().readEntity(JsonNode.class);
			session.setAttribute(USER_INFO, userInfo);
		}

		String accountId = (String)context.getConfiguration().getProperty(OAuth2Feature.Property.ACCOUNT_ID);
		JsonNode account = getAccount(userInfo, accountId);
		URI userUri = routeToUserUri(context.getUri(), account);
		context.setUri(userUri);
	}

	public static JsonNode readUserInfo(ISession s) {
		return (JsonNode) s.getAttribute(USER_INFO);
	}

	/**
	 * Extract the account to use.
	 * 
	 * A user might have multiple accounts. If the accountId is set, then
	 * the corresponding account will be searched. If it is not set or
	 * if it is not found, then take the default.
	 * 
	 * @param accounts
	 * @param accountId
	 * @return
	 */
	private static JsonNode getAccount(JsonNode accounts, String accountId) {
		JsonNode account = null;
		boolean hasAccountId = StringUtils.isNotBlank(accountId);

		if(accounts != null) {
			JsonNode accountsList = accounts.get("accounts");
			if(accountsList != null) {
				account = accountsList.get(0);
				for (JsonNode acc : accountsList) {
					if(hasAccountId && accountId.equalsIgnoreCase(getAccountId(acc))) {
						account = acc;
						break;
					}
					else if(isDefault(acc)) {
						account = acc;
						break;
					}
				}
			}
		}

		if(account == null) {
			Ivy.log().warn("Could not find any DocuSign account while searching for {0}.", hasAccountId ? accountId : "any");
		}
		else if(hasAccountId && !accountId.equalsIgnoreCase(getAccountId(account))) {
			Ivy.log().warn("Could not find DocuSign account with id ''{0}'', using {1}.", accountId, accountToString(account));
		}
		else if(!hasAccountId && !isDefault(account)) {
			Ivy.log().warn("Could not find default DocuSign account, using {1}.", accountToString(account));
		}

		return account;
	}

	private static URI routeToUserUri(URI uri, JsonNode accountInfo) {
		Ivy.log().debug("patching URI: " + uri);
		String resource = StringUtils.substringAfter(uri.getPath(), ACCOUNT_ID_PLACEHOLDER + "/");
		String rawQuery = uri.getRawQuery();

		URI baseUri = URI.create(accountInfo.get("base_uri").asText());
		URI userUri = UriBuilder.fromUri(baseUri).path("/restapi/v2.1/accounts/{myId}")
				.resolveTemplate("myId", getAccountId(accountInfo))
				.path(resource)
				.replaceQuery(rawQuery)
				.build();
		Ivy.log().debug("patched user URI: " + userUri);

		return userUri;
	}

	private static boolean isDefault(JsonNode account) {
		return account.get("is_default").asBoolean();
	}

	private static String getAccountName(JsonNode account) {
		return account.get("account_name").asText();
	}

	private static String getAccountId(JsonNode account) {
		return account.get("account_id").asText();
	}

	private static String accountToString(JsonNode account) {
		return MessageFormat.format("Account: Id: ''{0}'' Name: ''{1}'' Default: {2}}",
				getAccountId(account),
				getAccountName(account),
				isDefault(account));
	}
}