package kaladin.zwolf.projects.playlist.mover.ports.out;

import org.springframework.http.ResponseEntity;

public interface SourcePlaylistUseCase<T> {
    ResponseEntity<T> getSourcePlaylist(String playlistId);

    ResponseEntity<T> getPaginatedPlaylist(String playlistId, String nextPageToken);
}
