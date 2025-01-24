package kaladin.zwolf.projects.playlist.mover.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestClient;

import static org.springframework.http.HttpMethod.OPTIONS;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class RestClientConfiguration {

    @Value("${playlist.mover.youtube.api-url}")
    private String youtubeApiUrl;

    @Value("${playlist.mover.spotify.authorize-url}")
    private String spotifyApiUrl;

    @Bean
    public RestClient youtubeApiRestClient() {
        return RestClient.builder()
                .baseUrl(youtubeApiUrl)
                .build();
    }

    @Bean
    public RestClient spotifyApiRestClient() {
        return RestClient.builder()
                .baseUrl(spotifyApiUrl)
                .build();
    }

}
