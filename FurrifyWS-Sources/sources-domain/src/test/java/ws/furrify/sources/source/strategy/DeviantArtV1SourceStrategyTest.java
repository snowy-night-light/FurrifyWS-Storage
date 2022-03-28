package ws.furrify.sources.source.strategy;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ws.furrify.sources.keycloak.KeycloakServiceClient;
import ws.furrify.sources.keycloak.PropertyHolder;
import ws.furrify.sources.keycloak.dto.KeycloakIdpTokenQueryDTO;
import ws.furrify.sources.providers.deviantart.DeviantArtServiceClient;
import ws.furrify.sources.providers.deviantart.dto.DeviantArtDeviationQueryDTO;
import ws.furrify.sources.providers.deviantart.dto.DeviantArtUserQueryDTO;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// TODO update tests for new strategy
class DeviantArtV1SourceStrategyTest {

    private static KeycloakServiceClient keycloakServiceClient;
    private static DeviantArtServiceClient deviantArtServiceClient;
    private static ServletRequestAttributes servletRequestAttributes;

    private UUID id;
    private String username;
    private HashMap<String, String> data;
    private DeviantArtV1SourceStrategy deviantArtV1SourceStrategy;

    @BeforeAll
    static void beforeAll() {
        keycloakServiceClient = mock(KeycloakServiceClient.class);
        deviantArtServiceClient = mock(DeviantArtServiceClient.class);

        PropertyHolder.AUTH_SERVER = "test";
        PropertyHolder.REALM = "test";

        servletRequestAttributes = new ServletRequestAttributes(new HttpServletRequest() {
            @Override
            public Object getAttribute(final String s) {
                return null;
            }

            @Override
            public Enumeration<String> getAttributeNames() {
                return null;
            }

            @Override
            public String getCharacterEncoding() {
                return null;
            }

            @Override
            public void setCharacterEncoding(final String s) throws UnsupportedEncodingException {

            }

            @Override
            public int getContentLength() {
                return 0;
            }

            @Override
            public long getContentLengthLong() {
                return 0;
            }

            @Override
            public String getContentType() {
                return null;
            }

            @Override
            public ServletInputStream getInputStream() throws IOException {
                return null;
            }

            @Override
            public String getParameter(final String s) {
                return null;
            }

            @Override
            public Enumeration<String> getParameterNames() {
                return null;
            }

            @Override
            public String[] getParameterValues(final String s) {
                return new String[0];
            }

            @Override
            public Map<String, String[]> getParameterMap() {
                return null;
            }

            @Override
            public String getProtocol() {
                return null;
            }

            @Override
            public String getScheme() {
                return null;
            }

            @Override
            public String getServerName() {
                return null;
            }

            @Override
            public int getServerPort() {
                return 0;
            }

            @Override
            public BufferedReader getReader() throws IOException {
                return null;
            }

            @Override
            public String getRemoteAddr() {
                return null;
            }

            @Override
            public String getRemoteHost() {
                return null;
            }

            @Override
            public void setAttribute(final String s, final Object o) {

            }

            @Override
            public void removeAttribute(final String s) {

            }

            @Override
            public Locale getLocale() {
                return null;
            }

            @Override
            public Enumeration<Locale> getLocales() {
                return null;
            }

            @Override
            public boolean isSecure() {
                return false;
            }

            @Override
            public RequestDispatcher getRequestDispatcher(final String s) {
                return null;
            }

            @Override
            public String getRealPath(final String s) {
                return null;
            }

            @Override
            public int getRemotePort() {
                return 0;
            }

            @Override
            public String getLocalName() {
                return null;
            }

            @Override
            public String getLocalAddr() {
                return null;
            }

            @Override
            public int getLocalPort() {
                return 0;
            }

            @Override
            public ServletContext getServletContext() {
                return null;
            }

            @Override
            public AsyncContext startAsync() throws IllegalStateException {
                return null;
            }

            @Override
            public AsyncContext startAsync(final ServletRequest servletRequest, final ServletResponse servletResponse) throws IllegalStateException {
                return null;
            }

            @Override
            public boolean isAsyncStarted() {
                return false;
            }

            @Override
            public boolean isAsyncSupported() {
                return false;
            }

            @Override
            public AsyncContext getAsyncContext() {
                return null;
            }

            @Override
            public DispatcherType getDispatcherType() {
                return null;
            }

            @Override
            public String getAuthType() {
                return null;
            }

            @Override
            public Cookie[] getCookies() {
                return new Cookie[0];
            }

            @Override
            public long getDateHeader(final String s) {
                return 0;
            }

            @Override
            public String getHeader(final String s) {
                return "dsa";
            }

            @Override
            public Enumeration<String> getHeaders(final String s) {
                return null;
            }

            @Override
            public Enumeration<String> getHeaderNames() {
                return null;
            }

            @Override
            public int getIntHeader(final String s) {
                return 0;
            }

            @Override
            public String getMethod() {
                return null;
            }

            @Override
            public String getPathInfo() {
                return null;
            }

            @Override
            public String getPathTranslated() {
                return null;
            }

            @Override
            public String getContextPath() {
                return null;
            }

            @Override
            public String getQueryString() {
                return null;
            }

            @Override
            public String getRemoteUser() {
                return null;
            }

            @Override
            public boolean isUserInRole(final String s) {
                return false;
            }

            @Override
            public Principal getUserPrincipal() {
                return null;
            }

            @Override
            public String getRequestedSessionId() {
                return null;
            }

            @Override
            public String getRequestURI() {
                return null;
            }

            @Override
            public StringBuffer getRequestURL() {
                return null;
            }

            @Override
            public String getServletPath() {
                return null;
            }

            @Override
            public HttpSession getSession(final boolean b) {
                return null;
            }

            @Override
            public HttpSession getSession() {
                return null;
            }

            @Override
            public String changeSessionId() {
                return null;
            }

            @Override
            public boolean isRequestedSessionIdValid() {
                return false;
            }

            @Override
            public boolean isRequestedSessionIdFromCookie() {
                return false;
            }

            @Override
            public boolean isRequestedSessionIdFromURL() {
                return false;
            }

            @Override
            public boolean isRequestedSessionIdFromUrl() {
                return false;
            }

            @Override
            public boolean authenticate(final HttpServletResponse httpServletResponse) throws IOException, ServletException {
                return false;
            }

            @Override
            public void login(final String s, final String s1) throws ServletException {

            }

            @Override
            public void logout() throws ServletException {

            }

            @Override
            public Collection<Part> getParts() throws IOException, ServletException {
                return null;
            }

            @Override
            public Part getPart(final String s) throws IOException, ServletException {
                return null;
            }

            @Override
            public <T extends HttpUpgradeHandler> T upgrade(final Class<T> aClass) throws IOException, ServletException {
                return null;
            }
        });
    }

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        username = "Test";
        data = new HashMap<>();

        deviantArtV1SourceStrategy = new DeviantArtV1SourceStrategy(
                keycloakServiceClient,
                deviantArtServiceClient
        );
    }

    @Test
    @DisplayName("Validate media")
    void validateMedia() {
        // Given
        data.put("id", id.toString());
        // When
        var deviantArtResponse = new DeviantArtDeviationQueryDTO();
        deviantArtResponse.setDeviationId(id.toString());

        when(keycloakServiceClient.getKeycloakIdentityProviderToken(any(), any(), any())).thenReturn(new KeycloakIdpTokenQueryDTO());
        when(deviantArtServiceClient.getDeviation(any(), any())).thenReturn(deviantArtResponse);

        try (MockedStatic<RequestContextHolder> mock = Mockito.mockStatic(RequestContextHolder.class)) {
            mock.when(RequestContextHolder::getRequestAttributes).thenReturn(servletRequestAttributes);

            // Then
            assertTrue(deviantArtV1SourceStrategy.validateMedia(data).isValid(), "Validation failed with correct parameters.");
        }
    }

    @Test
    @DisplayName("Validate media with empty id property")
    void validateMedia2() {
        // Given
        // When
        // Then
        assertFalse(deviantArtV1SourceStrategy.validateMedia(data).isValid(), "Validation accepted empty id.");
    }

    @Test
    @DisplayName("Validate media with non existing id")
    void validateMedia3() {
        // Given
        // When
        when(keycloakServiceClient.getKeycloakIdentityProviderToken(any(), any(), any())).thenReturn(new KeycloakIdpTokenQueryDTO());
        when(deviantArtServiceClient.getDeviation(any(), any())).thenReturn(null);
        // Then
        assertFalse(deviantArtV1SourceStrategy.validateMedia(data).isValid(), "Validation accepted empty id.");
    }

    @Test
    @DisplayName("Validate attachment")
    void validateAttachment() {
        // Given
        data.put("id", id.toString());
        // When
        var deviantArtResponse = new DeviantArtDeviationQueryDTO();
        deviantArtResponse.setDeviationId(id.toString());

        when(keycloakServiceClient.getKeycloakIdentityProviderToken(any(), any(), any())).thenReturn(new KeycloakIdpTokenQueryDTO());
        when(deviantArtServiceClient.getDeviation(any(), any())).thenReturn(deviantArtResponse);
        // Then
        assertTrue(deviantArtV1SourceStrategy.validateAttachment(data).isValid(), "Validation failed with correct parameters.");
    }

    @Test
    @DisplayName("Validate attachment with empty id property")
    void validateAttachment2() {
        // Given
        // When
        // Then
        assertFalse(deviantArtV1SourceStrategy.validateAttachment(data).isValid(), "Validation accepted empty id.");
    }

    @Test
    @DisplayName("Validate attachment with non existing id")
    void validateAttachment3() {
        // Given
        // When
        when(keycloakServiceClient.getKeycloakIdentityProviderToken(any(), any(), any())).thenReturn(new KeycloakIdpTokenQueryDTO());
        when(deviantArtServiceClient.getDeviation(any(), any())).thenReturn(null);
        // Then
        assertFalse(deviantArtV1SourceStrategy.validateAttachment(data).isValid(), "Validation accepted empty id.");
    }

    @Test
    @DisplayName("Validate user")
    void validateUser() {
        // Given
        data.put("username", username);
        // When
        var deviantArtResponse = new DeviantArtUserQueryDTO();
        deviantArtResponse.setUser(
                new DeviantArtUserQueryDTO.User(username)
        );

        when(keycloakServiceClient.getKeycloakIdentityProviderToken(any(), any(), any())).thenReturn(new KeycloakIdpTokenQueryDTO());
        when(deviantArtServiceClient.getUser(any(), any())).thenReturn(deviantArtResponse);
        // Then
        assertTrue(deviantArtV1SourceStrategy.validateUser(data).isValid(), "Validation failed with correct parameters.");
    }

    @Test
    @DisplayName("Validate attachment with empty username property")
    void validateUser2() {
        // Given
        // When
        // Then
        assertFalse(deviantArtV1SourceStrategy.validateUser(data).isValid(), "Validation accepted empty username.");
    }

    @Test
    @DisplayName("Validate attachment with non existing username")
    void validateUser3() {
        // Given
        // When
        when(keycloakServiceClient.getKeycloakIdentityProviderToken(any(), any(), any())).thenReturn(new KeycloakIdpTokenQueryDTO());
        when(deviantArtServiceClient.getDeviation(any(), any())).thenReturn(null);
        // Then
        assertFalse(deviantArtV1SourceStrategy.validateUser(data).isValid(), "Validation accepted empty username.");
    }
}