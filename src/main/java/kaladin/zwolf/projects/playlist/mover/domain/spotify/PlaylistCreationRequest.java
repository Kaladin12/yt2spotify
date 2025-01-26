package kaladin.zwolf.projects.playlist.mover.domain.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class PlaylistCreationRequest {
    private BaseRequest baseRequest;
    private List<String> songIds;
}
