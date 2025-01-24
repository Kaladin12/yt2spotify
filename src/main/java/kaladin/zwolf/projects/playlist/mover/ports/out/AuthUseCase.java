package kaladin.zwolf.projects.playlist.mover.ports.out;

import org.springframework.http.ResponseEntity;

public interface AuthUseCase {
    ResponseEntity<?> getToken(String code);
}
