package kaladin.zwolf.projects.playlist.mover.ports.out;

import kaladin.zwolf.projects.playlist.mover.domain.h2.Song;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface SongJdbcRepository extends Repository<Song, Long> {
    List<Song> findAll();
}
