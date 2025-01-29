package kaladin.zwolf.projects.playlist.mover.adapters.in;

import com.fasterxml.jackson.core.JsonProcessingException;
import kaladin.zwolf.projects.playlist.mover.domain.spotify.PlaylistCreationRequest;
import kaladin.zwolf.projects.playlist.mover.service.SpotifyApiService;
import kaladin.zwolf.projects.playlist.mover.service.SpotifyTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin("*")
public class SpotifyController {
    private Logger logger = LoggerFactory.getLogger(SpotifyController.class);

    private SpotifyTokenService spotifyTokenService;

    private SpotifyApiService spotifyApiService;

    private String token;

    public SpotifyController(SpotifyTokenService spotifyTokenService,
                             SpotifyApiService spotifyApiService) {
        this.spotifyTokenService = spotifyTokenService;
        this.spotifyApiService = spotifyApiService;
    }

    @GetMapping("/callback")
    public void getToken(@RequestParam("code") String code, @RequestParam("state") String state) {
        logger.info("Code {} state {}", code, state);
        var e = spotifyTokenService.retrieveToken(code);
        logger.info("token: {}", e.getAccessToken());
        token = e.getAccessToken();
        logger.info("scopes: {}", e.getScope());
    }

    @PostMapping(value = "/sfy/playlist", consumes = "application/json")
    public String createPlaylist(@RequestBody PlaylistCreationRequest body) throws JsonProcessingException {
        var id = spotifyApiService.createNewPlaylist(token, body);
        var res = spotifyApiService.addItemsToPlaylist(token, id, body);
        return id;
    }
}
