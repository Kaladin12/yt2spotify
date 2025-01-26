package kaladin.zwolf.projects.playlist.mover.domain.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BaseRequest {
    private String name;
    private String description;
    @JsonProperty("public")
    private Boolean isPublic;
}