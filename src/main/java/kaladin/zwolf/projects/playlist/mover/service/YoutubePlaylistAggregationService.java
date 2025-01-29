package kaladin.zwolf.projects.playlist.mover.service;

import kaladin.zwolf.projects.playlist.mover.domain.Constants;
import kaladin.zwolf.projects.playlist.mover.domain.youtube.YoutubePlaylist;
import kaladin.zwolf.projects.playlist.mover.domain.youtube.YoutubePlaylistItem;
import kaladin.zwolf.projects.playlist.mover.ports.out.PlaylistAggregationUseCase;
import kaladin.zwolf.projects.playlist.mover.ports.out.SourcePlaylistUseCase;
import org.javatuples.Pair;
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

    private void processAndStorePlaylistData(YoutubePlaylist ytPlaylist, Map<String, Set<String>> map) {
        logger.info("Batch size: {}", ytPlaylist.getItems().size());
        removeTrailingData(ytPlaylist.getItems()).forEach((artist, title) -> {
            String artistName = getArtistNameIfInCache(artist);
            map.putIfAbsent(artistName, new HashSet<>());
            title.forEach(t ->
                    map.get(artistName).add(getSongNameIfInCache(t))
            );

        });
    }

    private Map<String, Set<String>> removeTrailingData(List<YoutubePlaylistItem> items) {
        return items.stream()
                .map(youtubePlaylistItem -> youtubePlaylistItem.getSnippet().getTitle())
                .map(String::toLowerCase)
                .map(this::filterTitle)
                .map(title -> title.split("[\\-|\\—|\\–]"))
                .map(title -> {
                    logger.info("PROCESSING {}, SIZE: {}", String.join(" ", title), title.length);
                    if (title.length == 2) {
                        return handleSongsWithArtistData(title);
                    }
                    return handleSongsWithNoArtistData(title);
                })
                .collect(Collectors.groupingBy(
                    p -> p.getValue0().strip(),
                    Collectors.mapping(p -> p.getValue1().strip(), Collectors.toSet())
                ));
    }

    private String filterTitle(String title) {
        Set<String> blackList = Set.of("(official", "(video", "(audio", "[official", "(lyric", "[unofficial", "- official", " official video");

        int index = Math.max(-1, Collections.max(blackList.stream().map(title::indexOf).toList()));
        return title.substring(0, index == -1 ? title.length() : index )
                .replace("'", "")
                .replace("\"", "")
                .replace("video", "")
                .replaceAll("\\(.*?\\)", "");
    }

    private Pair<String, String> handleSongsWithNoArtistData(String[] songData) {
        //return List.of(songData[0], songData[1].split("ft")[0]);
        return new Pair<>("", songData[0]);
    }

    private Pair<String, String> handleSongsWithArtistData(String[] songData) {
        return new Pair<>(songData[0], songData[1].split("ft")[0].split("feat")[0]);
    }

    private String getArtistNameIfInCache(String artistName) {
        return Optional.ofNullable(Constants.artistsMapping.get(artistName.strip()))
                .orElse(artistName.strip());
    }

    private String getSongNameIfInCache(String songName) {
        return Optional.ofNullable(Constants.songsMapping.get(songName.strip()))
                .orElse(songName.strip());
    }
}
