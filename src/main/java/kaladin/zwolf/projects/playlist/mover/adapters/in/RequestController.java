package kaladin.zwolf.projects.playlist.mover.adapters.in;

import com.fasterxml.jackson.core.JsonProcessingException;
import kaladin.zwolf.projects.playlist.mover.domain.spotify.PlaylistCreationRequest;
import kaladin.zwolf.projects.playlist.mover.ports.out.PlaylistAggregationUseCase;
import kaladin.zwolf.projects.playlist.mover.service.SpotifyApiService;
import kaladin.zwolf.projects.playlist.mover.service.SpotifyTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin("*")
public class RequestController {
    private Logger logger = LoggerFactory.getLogger(RequestController.class);

    private PlaylistAggregationUseCase playlistAggregationService;

    private SpotifyTokenService spotifyTokenService;

    private SpotifyApiService spotifyApiService;

    private String token;

    public RequestController(@Qualifier("youtube-service") PlaylistAggregationUseCase playlistAggregationService,
                             SpotifyTokenService spotifyTokenService,
                             SpotifyApiService spotifyApiService) {
        this.playlistAggregationService = playlistAggregationService;
        this.spotifyTokenService = spotifyTokenService;
        this.spotifyApiService = spotifyApiService;
    }

    @GetMapping("/playlist/{id}")
    @ResponseBody
    public List<String> getPlaylist(@PathVariable String id) {
        try {
            Map<String, Set<String>> size =  playlistAggregationService.getPaginatedPlaylistData(id);
            List<String> ids = spotifyApiService.idk(size);
            return ids;
        } catch (Exception e) {
            logger.error("Exception while fetching Youtube API: {}", e.getMessage());
        }

        return new ArrayList<>();
    }

    @GetMapping("/callback")
    public void getToken(@RequestParam("code") String code, @RequestParam("state") String state) {
        logger.info("Code {} state {}", code, state);
        var e = spotifyTokenService.retrieveToken(code);
        logger.info("token: {}", e.getAccessToken());
        token = e.getAccessToken();
        logger.info("scopes: {}", e.getScope());
    }

    @GetMapping("/test")
    public void getSpotifySong(@RequestParam("artist") String artist, @RequestParam("track") String track) throws URISyntaxException {
        var e = spotifyApiService.searchSongByArtistAndName(artist, track);
        logger.info("RES: {}", e);
    }

    @PostMapping(value = "/playlist", consumes = "application/json")
    public String createPlaylist(@RequestBody PlaylistCreationRequest body) throws JsonProcessingException {
        var id = spotifyApiService.createNewPlaylist(token, body);
        var res = spotifyApiService.addItemsToPlaylist(token, id, body);
        return id;
    }

    @PostMapping(value = "/quack", consumes = "application/json")
    public PlaylistCreationRequest d(@RequestBody PlaylistCreationRequest ii) {
        logger.info("YAYAYA {}", ii);
        return ii;
    }
}
