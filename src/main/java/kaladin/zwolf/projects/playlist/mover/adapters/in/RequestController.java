package kaladin.zwolf.projects.playlist.mover.adapters.in;

import kaladin.zwolf.projects.playlist.mover.ports.out.PlaylistAggregationUseCase;
import kaladin.zwolf.projects.playlist.mover.service.SpotifyTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin("*")
public class RequestController {
    private Logger logger = LoggerFactory.getLogger(RequestController.class);

    private PlaylistAggregationUseCase playlistAggregationService;

    private SpotifyTokenService spotifyTokenService;

    public RequestController(@Qualifier("youtube-service") PlaylistAggregationUseCase playlistAggregationService,
                             SpotifyTokenService spotifyTokenService) {
        this.playlistAggregationService = playlistAggregationService;
        this.spotifyTokenService = spotifyTokenService;
    }

    @GetMapping("/playlist/{id}")
    @ResponseBody
    public ResponseEntity<String> getPlaylist(@PathVariable String id) {
        try {
            var size =  playlistAggregationService.getPaginatedPlaylistData(id);
            logger.info("Size: {}", size);
        } catch (Exception e) {
            logger.error("Exception while fetching Youtube API: {}", e.getMessage());
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/callback")
    public void getToken(@RequestParam("code") String code, @RequestParam("state") String state) {
        logger.info("Code {} state {}", code, state);
        var e = spotifyTokenService.retrieveT(code);
        logger.info("token: {}", e.getAccessToken());
        logger.info("scopes: {}", e.getScope());
    }
}
