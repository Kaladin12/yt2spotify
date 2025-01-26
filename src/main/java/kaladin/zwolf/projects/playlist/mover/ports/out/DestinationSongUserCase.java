package kaladin.zwolf.projects.playlist.mover.ports.out;

import org.springframework.http.ResponseEntity;

import java.net.URI;

public interface DestinationSongUserCase {
    ResponseEntity<?> searchSong(String uri, int limit);
}
