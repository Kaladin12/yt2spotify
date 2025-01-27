package kaladin.zwolf.projects.playlist.mover.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import kaladin.zwolf.projects.playlist.mover.adapters.out.SpotifyApiAdapter;
import kaladin.zwolf.projects.playlist.mover.domain.spotify.PlaylistCreationRequest;
import kaladin.zwolf.projects.playlist.mover.domain.spotify.SongSearchResponse;
import kaladin.zwolf.projects.playlist.mover.service.util.PlaylistUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.util.*;

@Service
public class SpotifyApiService {
    private Logger logger = LoggerFactory.getLogger(SpotifyApiService.class);

    private SpotifyApiAdapter spotifySongAdapter;

    public SpotifyApiService(SpotifyApiAdapter spotifySongAdapter) {
        this.spotifySongAdapter = spotifySongAdapter;
    }

    public List<String> searchTracks(Map<String, Set<String>> data) {
        List<String> ids = new ArrayList<>();
        data.forEach((artist, songs) -> {
            songs.forEach(song -> {
                SongSearchResponse id = null;
                try {
                    id = performCall(id, artist, song);
                    if (id == null) {
                        logger.error("retrying inverting the values {}:{}", song, artist);
                        id = performCall(id, song, artist);
                        if (id == null) {
                            throw new RuntimeException("NOT FOUND FOR song "+song+" artist "+artist);
                        }
                    }
                    ids.add(id.getTracks().getItems().getFirst().getId());
                } catch (Exception e) {
                    logger.error("ERROR: {}", e.getMessage());
                }
            });
        });
        return ids.stream().map(id -> "spotify:track:"+id).toList();
    }

    private SongSearchResponse performCall(SongSearchResponse id, String artist, String song) throws URISyntaxException {
        if (artist == null || artist.isEmpty()) {
            id = searchSongByName(song);
        } else {
            id = searchSongByArtistAndName(artist, song);
        }

        if (id.getTracks().getItems().isEmpty()) {
            logger.warn("NOT FOUND FOR song: {} and artist {}", song, artist);
            return null;
        }

        return id;
    }

    public SongSearchResponse searchSongByArtistAndName(String artist, String track) throws URISyntaxException {
        return spotifySongAdapter
                .searchSong("artist:\"" + artist + "\" track:\"" + track + "\"", 1)
                .getBody();
    }

    public SongSearchResponse searchSongByName(String track) throws URISyntaxException {
        return spotifySongAdapter
                .searchSong("track:\"" + track + "\"", 1)
                .getBody();
    }

    public String createNewPlaylist(String token, PlaylistCreationRequest body) throws JsonProcessingException {
        return spotifySongAdapter.generateNewPlaylist(token, body.getBaseRequest()).getBody().getId();
    }

    public String addItemsToPlaylist(String token, String playlistId, PlaylistCreationRequest requestBody) throws JsonProcessingException {
        List<List<String>> chunks = PlaylistUtil.splitInChunks(requestBody.getSongIds(), 100);
        chunks.forEach(chunk -> {
            try {
                spotifySongAdapter.addItemsToPlaylist(token, playlistId, chunk);
            } catch (JsonProcessingException e) {
                logger.error("FAILURE: {}", e.getMessage());
            }
        });

        return "OK";
    }

}
