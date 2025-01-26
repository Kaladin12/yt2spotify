package kaladin.zwolf.projects.playlist.mover.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import kaladin.zwolf.projects.playlist.mover.adapters.out.SpotifyApiAdapter;
import kaladin.zwolf.projects.playlist.mover.domain.spotify.PlaylistCreationRequest;
import kaladin.zwolf.projects.playlist.mover.domain.spotify.SongSearchResponse;
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

    public List<String> idk(Map<String, Set<String>> data) {
        List<String> ids = new ArrayList<>();
        data.forEach((artist, songs) -> {
            songs.forEach(song -> {
                try {
                    var id = searchSongByArtistAndName(artist, song);
                    if (id.getTracks().getItems().isEmpty()) {
                        throw new Exception("NOT FOUND FOR song "+song+" artist "+artist);
                    }
                    ids.add(id.getTracks().getItems().getFirst().getId());
                } catch (Exception e) {
                    logger.error("ERROR: {}", e.getMessage());
                }

            });
        });
        return ids.stream().map(id -> "spotify:track:"+id).toList();
    }

    public SongSearchResponse searchSongByArtistAndName(String artist, String track) throws URISyntaxException {
        return spotifySongAdapter
                .searchSong("artist:\"" + artist + "\" track:\"" + track + "\"", 1)
                .getBody();
    }

    public String createNewPlaylist(String token, PlaylistCreationRequest body) throws JsonProcessingException {
        return spotifySongAdapter.generateNewPlaylist(token, body.getBaseRequest()).getBody().getId();
    }

    public String addItemsToPlaylist(String token, String playlistId, PlaylistCreationRequest requestBody) throws JsonProcessingException {
        List<List<String>> chunks = new ArrayList<>();
        int listSize = requestBody.getSongIds().size();

        for (int i = 0; i < listSize; i += 100) {
            chunks.add(new ArrayList<>(requestBody.getSongIds().subList(i, Math.min(i + 100, listSize))));
        }

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
