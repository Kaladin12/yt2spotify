package kaladin.zwolf.projects.playlist.mover.domain.spotify;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlaylistCreationResponse {
    private String id;
}
