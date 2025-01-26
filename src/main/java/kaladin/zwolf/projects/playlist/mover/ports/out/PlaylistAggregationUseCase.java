package kaladin.zwolf.projects.playlist.mover.ports.out;

import java.util.Map;
import java.util.Set;

public interface PlaylistAggregationUseCase {
    Map<String, Set<String>> getPaginatedPlaylistData(String playlistId);
}
