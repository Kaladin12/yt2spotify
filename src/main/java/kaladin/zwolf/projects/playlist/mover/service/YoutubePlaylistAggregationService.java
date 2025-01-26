package kaladin.zwolf.projects.playlist.mover.service;

import kaladin.zwolf.projects.playlist.mover.domain.Constants;
import kaladin.zwolf.projects.playlist.mover.domain.youtube.YoutubePlaylist;
import kaladin.zwolf.projects.playlist.mover.domain.youtube.YoutubePlaylistItem;
import kaladin.zwolf.projects.playlist.mover.ports.out.PlaylistAggregationUseCase;
import kaladin.zwolf.projects.playlist.mover.ports.out.SourcePlaylistUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("youtube-service")
public class YoutubePlaylistAggregationService implements PlaylistAggregationUseCase {
    private Logger logger = LoggerFactory.getLogger(YoutubePlaylistAggregationService.class);

    private SourcePlaylistUseCase<YoutubePlaylist> sourcePlaylistUseCase;

    private SpotifyApiService spotifyApiService;

    public YoutubePlaylistAggregationService(SourcePlaylistUseCase<YoutubePlaylist> sourcePlaylistUseCase, SpotifyApiService spotifyApiService) {
        this.sourcePlaylistUseCase = sourcePlaylistUseCase;
        this.spotifyApiService = spotifyApiService;
    }

    @Override
    public Map<String, Set<String>> getPaginatedPlaylistData(String playlistId) {
        Map<String, Set<String>> map = new HashMap<>();
        ResponseEntity<YoutubePlaylist> playlist = sourcePlaylistUseCase.getSourcePlaylist(playlistId);
        YoutubePlaylist ytPlaylist = playlist.getBody();
        processAndStorePlaylistData(ytPlaylist, map);

        while (ytPlaylist.getNextPageToken() != null) {
            ytPlaylist = sourcePlaylistUseCase
                    .getPaginatedPlaylist(playlistId, ytPlaylist.getNextPageToken()).getBody();
            processAndStorePlaylistData(ytPlaylist, map);
        }
        map.forEach((artistName, title) -> {
            logger.info("ARTIST: {}, SONGS: {}", artistName, title);
        });
        return map;
    }

    private Map<String, Set<String>> removeTrailingData(List<YoutubePlaylistItem> items) {
        Set<String> blackList = Set.of("(official", "(video", "(audio", "[official", "(lyric", "[unofficial", "- official", " official video");
        var titles = items.stream()
                .map(youtubePlaylistItem -> youtubePlaylistItem.getSnippet().getTitle())
                .map(String::toLowerCase)
                .map(title -> {
                    int index = Math.max(-1, Collections.max(blackList.stream().map(title::indexOf).toList()));
                    return title.substring(0, index == -1 ? title.length() : index );
                })
                .map(title -> title.split("[\\-|\\—]"));
        return titles.filter(title -> title.length == 2)
                .map(songData -> {
                    return List.of(songData[0], songData[1].split("ft")[0]);
                })
                .collect(Collectors.groupingBy(
                p -> p.getFirst().strip(),
                Collectors.mapping(p -> p.get(1).strip(), Collectors.toSet())
        ));
//        return titles.filter(title -> title.length < 2).map(title -> )
    }

    private void processAndStorePlaylistData(YoutubePlaylist ytPlaylist, Map<String, Set<String>> map) {
        logger.info("Batch size: {}", ytPlaylist.getItems().size());
        removeTrailingData(ytPlaylist.getItems()).forEach((artist, title) -> {
            String artistName = getArtistNameIfInCache(artist);
            map.putIfAbsent(artistName, new HashSet<>());
            title.forEach(t ->
                    map.get(artistName).add(t)
            );

        });
    }

    private String getArtistNameIfInCache(String artistName) {
        return Optional.ofNullable(Constants.artistsMapping.get(artistName))
                .orElse(artistName);
    }


}
