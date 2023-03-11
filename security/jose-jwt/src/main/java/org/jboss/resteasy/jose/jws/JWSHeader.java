package org.jboss.resteasy.jose.jws;

import java.io.IOException;
import java.io.Serializable;

import jakarta.ws.rs.core.MediaType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JWSHeader implements Serializable {
    @JsonProperty("alg")
    private Algorithm algorithm;

    @JsonProperty("typ")
    private String type;

    @JsonProperty("cty")
    private String contentType;

    @JsonProperty("kid")
    private String keyId;

    public JWSHeader() {
    }

    public JWSHeader(final Algorithm algorithm, final String type, final String contentType) {
        this.algorithm = algorithm;
        this.type = type;
        this.contentType = contentType;
    }

    public JWSHeader(final Algorithm algorithm, final String type, final String contentType, final String keyId) {
        this.algorithm = algorithm;
        this.type = type;
        this.keyId = keyId;
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public String getType() {
        return type;
    }

    public String getContentType() {
        return contentType;
    }

    public String getKeyId() {
        return keyId;
    }

    @JsonIgnore
    public MediaType getMediaType() {
        if (contentType == null)
            return null;
        return MediaType.valueOf(contentType);
    }

    private static final ObjectMapper mapper = new ObjectMapper();
    static {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public String toString() {
        try {
            return mapper.writeValueAsString(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
