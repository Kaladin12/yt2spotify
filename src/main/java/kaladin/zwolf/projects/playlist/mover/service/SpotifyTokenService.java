package kaladin.zwolf.projects.playlist.mover.service;

import kaladin.zwolf.projects.playlist.mover.domain.spotify.TokenResponse;
import kaladin.zwolf.projects.playlist.mover.ports.out.AuthUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class SpotifyTokenService {
    private final AuthUseCase spotifyAuthAdapter;

    public SpotifyTokenService(AuthUseCase spotifyAuthAdapter) {
        this.spotifyAuthAdapter = spotifyAuthAdapter;
    }

    public TokenResponse retrieveT(String code) {
        return (TokenResponse) spotifyAuthAdapter.getToken(code).getBody();
    }

}
