package kaladin.zwolf.projects.playlist.mover.service;

import kaladin.zwolf.projects.playlist.mover.domain.youtube.YoutubePlaylist;
import kaladin.zwolf.projects.playlist.mover.domain.youtube.YoutubePlaylistItem;
import kaladin.zwolf.projects.playlist.mover.ports.out.PlaylistAggregationUseCase;
import kaladin.zwolf.projects.playlist.mover.ports.out.SourcePlaylistUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service("youtube-service")
public class YoutubePlaylistAggregationService implements PlaylistAggregationUseCase {
    private Logger logger = LoggerFactory.getLogger(YoutubePlaylistAggregationService.class);

    private SourcePlaylistUseCase<YoutubePlaylist> sourcePlaylistUseCase;

    public YoutubePlaylistAggregationService(SourcePlaylistUseCase<YoutubePlaylist> sourcePlaylistUseCase) {
        this.sourcePlaylistUseCase = sourcePlaylistUseCase;
    }

    @Override
    public int getPaginatedPlaylistData(String playlistId) {
        ResponseEntity<YoutubePlaylist> playlist = sourcePlaylistUseCase.getSourcePlaylist(playlistId);
        YoutubePlaylist ytPlaylist = playlist.getBody();
        logger.info("Batch size: {}", ytPlaylist.getItems().size());
        removeTrailingData(ytPlaylist.getItems()).forEach(System.out::println);
        // Map to Spotify and create new playlist
//        while (ytPlaylist.getNextPageToken() != null) {
//            ytPlaylist = sourcePlaylistUseCase
//                    .getPaginatedPlaylist(playlistId, ytPlaylist.getNextPageToken()).getBody();
//            logger.info("Batch size: {}", ytPlaylist.getItems().size());
//        }

        return ytPlaylist.getPageInfo().getTotalResults();
    }

    private Set<String> removeTrailingData(List<YoutubePlaylistItem> items) {
        Set<String> blackList = Set.of("(official", "(video", "(audio", "[official", "(lyric", "[unofficial", "- official", " official video");
        return items.stream()
                .map(youtubePlaylistItem -> youtubePlaylistItem.getSnippet().getTitle())
                .map(String::toLowerCase)
                .map(title -> {
                    int index = Math.max(-1, Collections.max(blackList.stream().map(title::indexOf).toList()));
                    return title.substring(0, index == -1 ? title.length() : index );
                })
                .map(title -> title.replaceAll("[\\-|\\—]", ""))
                .collect(Collectors.toSet());
    }
}
