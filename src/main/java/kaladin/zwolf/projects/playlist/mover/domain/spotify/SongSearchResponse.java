package kaladin.zwolf.projects.playlist.mover.domain.spotify;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SongSearchResponse {
    private Track tracks;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Track {
        List<Item> items;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {
        private String id;
    }
}
