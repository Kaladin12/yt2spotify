package kaladin.zwolf.projects.playlist.mover;

import kaladin.zwolf.projects.playlist.mover.domain.Constants;
import kaladin.zwolf.projects.playlist.mover.ports.out.ArtistJdbcRepository;
import kaladin.zwolf.projects.playlist.mover.ports.out.SongJdbcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Autowired
	private ArtistJdbcRepository artistJdbcRepository;

	@Autowired
	private SongJdbcRepository songRepository;

	@Override
	public void run(String... args) throws Exception {
		 artistJdbcRepository.findAll().forEach(artist ->
				 Constants.artistsMapping
						 .put(artist.getOriginal_name(), artist.getOverwritten_name())
		);
		songRepository.findAll().forEach(song ->
			Constants.songsMapping
					.put(song.getOriginal_name(), song.getOverwritten_name())
		);
		System.out.println(Constants.artistsMapping);
		System.out.println(Constants.songsMapping);
	}
}
