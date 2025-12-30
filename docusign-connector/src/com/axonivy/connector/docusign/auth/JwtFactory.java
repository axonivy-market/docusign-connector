package com.axonivy.connector.docusign.auth;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import ch.ivyteam.ivy.config.IFileAccess;
import ch.ivyteam.ivy.rest.client.FeatureConfig;
import ch.ivyteam.ivy.rest.client.oauth2.uri.OAuth2UriProperty;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;

/**
 * Builds a docuSign compliant JSON Web Token (JWT)
 *
 * https://developers.docusign.com/platform/auth/jwt/jwt-get-token
 * @since 9.2.0
 */
public class JwtFactory {
  private final FeatureConfig conf;
  private final OAuth2UriProperty uriFactory;

  public JwtFactory(FeatureConfig conf, OAuth2UriProperty uriFactory) {
    this.conf = conf;
    this.uriFactory = uriFactory;
  }

  public String createToken() {
    return build()
            .signWith(getPrivateKey(), Jwts.SIG.RS256)
            .compact();
  }

  private JwtBuilder build() {
    return Jwts.builder()
            .issuer(conf.readMandatory(OAuth2Feature.Property.INTEGRATION_KEY))
            .subject(conf.readMandatory(OAuth2Feature.Property.JWT_USER_ID))
            .audience().add(uriFactory.getTokenUri().getHost()).and()
            .claim("scope", OAuth2Feature.getScope(conf))
            .issuedAt(Date.from(Instant.now()))
            .expiration(Date.from(Instant.now().plus(1l, ChronoUnit.HOURS)))
            .header().add("typ", "JWT").and();
  }

  public static byte[] getKey(Path privateKeyPem) {
    try (InputStream is = Files.newInputStream(privateKeyPem, StandardOpenOption.READ);
            ByteArrayOutputStream os = new ByteArrayOutputStream()) {
      IOUtils.copy(is, os);
      return os.toByteArray();
    } catch (IOException ex) {
      throw new RuntimeException("can not read key", ex);
    }
  }

  public static PrivateKey readPrivateKeyFromByteArray(byte[] rawPkcsKeyContent, String algorithm) {
    try {
      byte[] enyKey = readPemBody(rawPkcsKeyContent);
      KeyFactory kf = KeyFactory.getInstance(algorithm, new BouncyCastleProvider());
      EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(enyKey);
      return kf.generatePrivate(keySpec);
    } catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException ex) {
      throw new RuntimeException("Failed to read private key", ex);
    }
  }

  private static byte[] readPemBody(byte[] privateKeyBytes) throws IOException {
    try (BufferedReader buffer = new BufferedReader(new StringReader(new String(privateKeyBytes)))) {
      StringBuffer buf = new StringBuffer();
      String line = null;
      while ((line = buffer.readLine()) != null) {
        if (line.startsWith("-----")) {
          continue;
        }
        buf.append(line.trim());
      }
      return Base64.getDecoder().decode(buf.toString());
    }
  }

  @SuppressWarnings("all")
  private PrivateKey getPrivateKey() {
    Path keyFile = Path.of(conf.readMandatory(OAuth2Feature.Property.JWT_KEY_FILE));
    if (!keyFile.isAbsolute()) {
        String configDir = System.getProperty("configuration");
        Path basePath = configDir != null ? Path.of(configDir)
            : Path.of("").toAbsolutePath().resolve("configuration");
        keyFile = basePath.resolve(keyFile.toString());
    }
    return readPrivateKeyFromByteArray(getKey(keyFile), "RSA");
  }
}
