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
 * Customizes the baseURI according the the 'userInfo'.
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
	private static final String USER_INFO = "docusign.userInfo";
	private static final String ACCOUNT = "docusign.account";

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
		JsonNode account = readAccount(session);
		if (userInfo == null || account == null) {
			userInfo = context.getClient()
					.target(uriFactory.getUri("userinfo"))
					.request()
					.headers(context.getHeaders()) // copy bearer token
					.get().readEntity(JsonNode.class);
			session.setAttribute(USER_INFO, userInfo);

			String accountId = (String) context.getConfiguration().getProperty(OAuth2Feature.Property.ACCOUNT_ID);
			account = getAccount(userInfo, accountId);
			session.setAttribute(ACCOUNT, account);
		}

		URI userUri = routeToUserUri(context.getUri(), account);
		context.setUri(userUri);
	}

	public static JsonNode readAccount(ISession s) {
		return (JsonNode) s.getAttribute(ACCOUNT);
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
		JsonNode account = accounts.get(0);

		// TODO find out why account is null here
		// TODO make access to json null safe

		boolean hasAccountId = StringUtils.isNotBlank(accountId);
		for (JsonNode acc : accounts) {
			if(hasAccountId && accountId.equalsIgnoreCase(getAccountId(acc))) {
				account = acc;
				break;
			}
			else if(isDefault(acc)) {
				account = acc;
				break;
			}
		}

		if(account == null) {
			Ivy.log().warn("Could not find any DocuSign account whicl searching for {0}.", hasAccountId ? accountId : "any");
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
		String resource = StringUtils.substringAfter(uri.getPath(), "placeholder/");
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