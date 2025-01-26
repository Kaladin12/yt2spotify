package kaladin.zwolf.projects.playlist.mover.domain.spotify;

import lombok.Data;

import java.util.List;

@Data
public class AddItemsToPlaylistRequest {
    private List<String> uris;
    private int position;
}
