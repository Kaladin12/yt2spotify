package kaladin.zwolf.projects.playlist.mover.adapters.in;

import kaladin.zwolf.projects.playlist.mover.ports.out.PlaylistAggregationUseCase;
import kaladin.zwolf.projects.playlist.mover.service.SpotifyApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin("*")
public class YoutubeController {
    private Logger logger = LoggerFactory.getLogger(YoutubeController.class);

    private PlaylistAggregationUseCase playlistAggregationService;

    private SpotifyApiService spotifyApiService;

    public YoutubeController(@Qualifier("youtube-service") PlaylistAggregationUseCase playlistAggregationService,
                             SpotifyApiService spotifyApiService) {
        this.playlistAggregationService = playlistAggregationService;
        this.spotifyApiService = spotifyApiService;
    }

    @GetMapping("/yt/playlist/{id}")
    @ResponseBody
    public List<String> getPlaylist(@PathVariable String id) {
        try {
            Map<String, Set<String>> size =  playlistAggregationService.getPaginatedPlaylistData(id);
            return spotifyApiService.searchTracks(size);
        } catch (Exception e) {
            logger.error("Exception while fetching Youtube API: {}", e.getMessage());
        }
        return new ArrayList<>();
    }


}
