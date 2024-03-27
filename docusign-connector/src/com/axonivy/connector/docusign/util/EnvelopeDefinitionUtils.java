package com.axonivy.connector.docusign.util;

import static com.axonivy.connector.docusign.constant.DocuSignConstants.DEFAULT_ENVELOPE_STATUS;
import static com.axonivy.connector.docusign.constant.DocuSignConstants.DEFAULT_PAGE_NUMBER;
import static com.axonivy.connector.docusign.constant.DocuSignConstants.DEFAULT_ROLE_NAME;
import static com.axonivy.connector.docusign.constant.DocuSignConstants.DEFAULT_X_OFFSET;
import static com.axonivy.connector.docusign.constant.DocuSignConstants.DEFAULT_Y_OFFSET;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.docusign.esign.model.EnvelopeDefinition;
import com.docusign.esign.model.Recipients;
import com.docusign.esign.model.SignHere;
import com.docusign.esign.model.Signer;
import com.docusign.esign.model.Tabs;

import ch.ivyteam.ivy.scripting.objects.File;

public class EnvelopeDefinitionUtils {

	public static EnvelopeDefinition createSimpleEnvelopeDefinition(String emailSubject, File file, Signer signer,
			SignHere signHere) throws IOException {
		var envelopeDefinition = new EnvelopeDefinition();
		envelopeDefinition.setStatus(DEFAULT_ENVELOPE_STATUS);
		envelopeDefinition.setEmailSubject(emailSubject);
		envelopeDefinition.setDocuments(List.of(DocUtils.ofIvyFile(file)));
		Recipients recipients = new Recipients();
		recipients.setSigners(List.of(unifySignerData(signer, signHere)));
		envelopeDefinition.setRecipients(recipients);
		return envelopeDefinition;
	}

	private static Signer unifySignerData(Signer signer, SignHere signHere) throws IOException {
		var finalSigner = new Signer();
		finalSigner.setRecipientId(signer.getRecipientId());
		finalSigner.setName(signer.getName());
		finalSigner.setEmail(signer.getEmail());
		finalSigner.setClientUserId(signer.getClientUserId());
		finalSigner.routingOrder(signer.getRoutingOrder());
		finalSigner.setRoleName(DEFAULT_ROLE_NAME);
		var anchorString = signHere.getAnchorString();
		var finalSignHere = new SignHere();
		finalSignHere.setOptional(BooleanUtils.FALSE);
		finalSignHere.setPageNumber(StringUtils.defaultIfEmpty(signHere.getPageNumber(), DEFAULT_PAGE_NUMBER));
		if (StringUtils.isNoneBlank(anchorString)) {
			finalSignHere.setAnchorString(anchorString);
			finalSignHere.setAnchorXOffset(StringUtils.defaultIfEmpty(signHere.getAnchorXOffset(), DEFAULT_X_OFFSET));
			finalSignHere.setAnchorYOffset(StringUtils.defaultIfEmpty(signHere.getAnchorYOffset(), DEFAULT_Y_OFFSET));
			finalSignHere.setAnchorIgnoreIfNotPresent(BooleanUtils.TRUE);
		} else {
			finalSignHere.setXPosition(DEFAULT_X_OFFSET);
			finalSignHere.setYPosition(DEFAULT_Y_OFFSET);
		}
		finalSignHere.setDocumentId(DocUtils.DEFAULT_DOC_ID);
		finalSigner.setTabs(new Tabs());
		finalSigner.getTabs().setSignHereTabs(List.of(finalSignHere));
		return finalSigner;
	}
}
