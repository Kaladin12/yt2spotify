package kaladin.zwolf.projects.playlist.mover.service.util;

import java.util.ArrayList;
import java.util.List;

public class PlaylistUtil {
    private PlaylistUtil() {}

    public static List<List<String>> splitInChunks(List<String> originalList, int chunkSize) {
        List<List<String>> chunks = new ArrayList<>();
        int listSize = originalList.size();

        for (int i = 0; i < listSize; i += chunkSize) {
            chunks.add(new ArrayList<>(originalList.subList(i, Math.min(i + chunkSize, listSize))));
        }
        return chunks;
    }
}
