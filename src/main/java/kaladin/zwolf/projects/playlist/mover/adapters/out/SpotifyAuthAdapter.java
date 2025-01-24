package kaladin.zwolf.projects.playlist.mover.adapters.out;

import kaladin.zwolf.projects.playlist.mover.domain.spotify.TokenResponse;
import kaladin.zwolf.projects.playlist.mover.ports.out.AuthUseCase;
import kaladin.zwolf.projects.playlist.mover.service.YoutubePlaylistAggregationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Map;

@Component
public class SpotifyAuthAdapter implements AuthUseCase {

    private Logger logger = LoggerFactory.getLogger(SpotifyAuthAdapter.class);

    private RestClient spotifyApiRestClient;

    @Value("${playlist.mover.spotify.client-id}")
    private String clientId;

    @Value("${playlist.mover.spotify.client-secret}")
    private String clientSecret;

    @Value("${playlist.mover.spotify.redirect-url}")
    private String redirectUrl;

    public SpotifyAuthAdapter(RestClient spotifyApiRestClient) {
        this.spotifyApiRestClient = spotifyApiRestClient;
    }

    @Override
    public ResponseEntity<TokenResponse> getToken(String code) {
        MultiValueMap<String, String> body = MultiValueMap.fromSingleValue(
                Map.of("grant_type", "authorization_code",
                        "code", code,
                        "redirect_uri", redirectUrl)
        );

        return spotifyApiRestClient.post()
                .uri("/token")
                .body(body)
                .headers(httpHeaders -> {
                    httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                    httpHeaders.set(HttpHeaders.AUTHORIZATION,
                            "Basic " + Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes()));
                })
                .retrieve()
                .toEntity(TokenResponse.class);
    }
}
