package kaladin.zwolf.projects.playlist.mover.adapters.out;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import kaladin.zwolf.projects.playlist.mover.domain.spotify.*;
import kaladin.zwolf.projects.playlist.mover.ports.out.DestinationSongUserCase;
import kaladin.zwolf.projects.playlist.mover.service.SpotifyTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class SpotifyApiAdapter implements DestinationSongUserCase {
    private Logger logger = LoggerFactory.getLogger(SpotifyApiAdapter.class);

    private RestClient spotifyApiRestClient;

    private SpotifyTokenService spotifyTokenService;

    @Value("${playlist.mover.spotify.username}")
    String spotifyUsername;

    public SpotifyApiAdapter(RestClient spotifyApiRestClient, SpotifyTokenService spotifyTokenService) {
        this.spotifyApiRestClient = spotifyApiRestClient;
        this.spotifyTokenService = spotifyTokenService;
    }

    @Override
    public ResponseEntity<SongSearchResponse> searchSong(String uri, int limit) {
        ClientCredentialTokenResponse res = spotifyTokenService.retrieveToken();
        return spotifyApiRestClient.get()
                .uri(uriBuilder ->{
                    var e = uriBuilder.path("/search")
                            .queryParam("q", uri)
                            .queryParam("type", "track")
                            .queryParam("market", "MX")
                            .queryParam("limit", 1)
                            .build();
                    logger.info("URI: {}", e);
                    return e;
                })
                .headers(httpHeaders ->
                        httpHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer " + res.getAccessToken()))
                .retrieve()
                .toEntity(SongSearchResponse.class);

    }//artist:burial track:homeless

    public ResponseEntity<PlaylistCreationResponse> generateNewPlaylist(String token, BaseRequest requestBody) throws JsonProcessingException {
        return spotifyApiRestClient.post()
                .uri(uriBuilder -> {
                    var p = uriBuilder.path("/users/").path(spotifyUsername).path("/playlists").build();
                    logger.info("PLAYLIST PATH: {}", p);
                    return p;
                })
                .body(new JsonMapper().writeValueAsString(requestBody))
                .headers(httpHeaders ->
                        httpHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .retrieve()
                .toEntity(PlaylistCreationResponse.class);
    }

    public void addItemsToPlaylist(String token, String playlistId,  List<String> uris) throws JsonProcessingException {
        AddItemsToPlaylistRequest req = new AddItemsToPlaylistRequest();
        req.setUris(uris);
        req.setPosition(0);

        spotifyApiRestClient.post()
                .uri(uriBuilder -> {
                    var p = uriBuilder.path("/playlists/").path(playlistId).path("/tracks").build();
                    logger.info("PLAYLIST PATH: {}", p);
                    return p;
                })
                .body(new JsonMapper().writeValueAsString(req))
                .headers(httpHeaders ->
                        httpHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .retrieve()
                .toEntity(String.class);
    }

}
