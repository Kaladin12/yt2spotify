package kaladin.zwolf.projects.playlist.mover.ports.out;


import kaladin.zwolf.projects.playlist.mover.domain.h2.Artist;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface ArtistJdbcRepository extends Repository<Artist, Long> {
    List<Artist> findAll();
}
