package ws.furrify.sources.keycloak.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * Identity provider token details from current user.
 *
 * @author Skyte
 */
@Data
public class KeycloakIdpTokenQueryDTO {
    /**
     * Identity provider access token.
     */
    @SerializedName("access_token")
    private String accessToken;

    /**
     * Token type ex. Bearer.
     */
    @SerializedName("token_type")
    private String tokenType;

    /**
     * Expire time in seconds.
     */
    @SerializedName("expires_in")
    private Integer expiresIn;

    /**
     * Refresh token.
     */
    @SerializedName("refresh_token")
    private String refreshToken;

    /**
     * Scopes in the token.
     */
    @SerializedName("scope")
    private String scope;

    /**
     * Has it succeeded? ex. success
     */
    @SerializedName("status")
    private String status;
}
