package kaladin.zwolf.projects.playlist.mover.domain.youtube;

import lombok.Data;

@Data
public class YoutubePlaylistItem {
    private String kind;
    private String etag;
    private String nextPageToken;
    private Snippet snippet;

    @Data
    public static class Snippet {
        private String publishedAt; // requires date conversion
        private String channelId;
        private String title;
        private String description;
        private Object thumbnails;
        private String channelTitle;
        private String playlistId;
        private int position;
        private ResourceId resourceId;
        private String videoOwnerChannelTitle;
        private String videoOwnerChannelIdl;
    }

    @Data
    public static class ResourceId {
        private String kind;
        private String videoId;
    }
}
