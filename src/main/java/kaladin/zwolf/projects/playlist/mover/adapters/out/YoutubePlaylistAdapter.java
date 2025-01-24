package kaladin.zwolf.projects.playlist.mover.adapters.out;

import kaladin.zwolf.projects.playlist.mover.domain.youtube.YoutubePlaylist;
import kaladin.zwolf.projects.playlist.mover.ports.out.SourcePlaylistUseCase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;

@Component
public class YoutubePlaylistAdapter implements SourcePlaylistUseCase<YoutubePlaylist> {

    private RestClient youtubeApiRestClient;

    @Value("${playlist.mover.youtube.api-key}")
    private String youtubeApiKey;

    @Value("${playlist.mover.youtube.part}")
    private String youtubePart;

    public YoutubePlaylistAdapter(RestClient youtubeApiRestClient) {
        this.youtubeApiRestClient = youtubeApiRestClient;
    }

    @Override
    public ResponseEntity<YoutubePlaylist> getSourcePlaylist(String playlistId) {
        return youtubeApiRestClient.get()
                .uri(uriBuilder ->
                        playlistUriBuilder(uriBuilder, playlistId)
                                .build())
                .retrieve()
                .toEntity(YoutubePlaylist.class);
    }

    @Override
    public ResponseEntity<YoutubePlaylist> getPaginatedPlaylist(String playlistId, String nextPageToken) {
        return youtubeApiRestClient.get()
                .uri(uriBuilder ->
                        playlistUriBuilder(uriBuilder, playlistId)
                                .queryParam("pageToken", nextPageToken)
                                .build())
                .retrieve()
                .toEntity(YoutubePlaylist.class);
    }


    private UriBuilder playlistUriBuilder(UriBuilder uriBuilder, String playlistId) {
        return uriBuilder.path("/playlistItems")
                .queryParam("key", youtubeApiKey)
                .queryParam("part", youtubePart)
                .queryParam("maxResults", 50)
                .queryParam("playlistId", playlistId);
    }
}
