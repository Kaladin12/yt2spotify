package kaladin.zwolf.projects.playlist.mover.domain.youtube;

import lombok.Data;

import java.util.List;

@Data
public class YoutubePlaylist {
    private String kind;
    private String etag;
    private String nextPageToken;
    private List<YoutubePlaylistItem> items;
    private PageInfo pageInfo;
}
