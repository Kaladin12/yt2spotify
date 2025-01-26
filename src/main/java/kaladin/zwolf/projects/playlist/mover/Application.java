package kaladin.zwolf.projects.playlist.mover;

import kaladin.zwolf.projects.playlist.mover.domain.Constants;
import kaladin.zwolf.projects.playlist.mover.ports.out.ArtistJdbcRepository;
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
	private ArtistJdbcRepository repository;

	@Override
	public void run(String... args) throws Exception {
		var e = repository.findAll();
		e.forEach(artist -> Constants.artistsMapping
				.put(artist.getOriginal_name(), artist.getOverwritten_name())
		);
		System.out.println(Constants.artistsMapping);
	}
}
