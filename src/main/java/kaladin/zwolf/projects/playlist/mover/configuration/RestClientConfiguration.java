package kaladin.zwolf.projects.playlist.mover.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfiguration {

    @Value("${playlist.mover.youtube.api-url}")
    private String youtubeApiUrl;

    @Value("${playlist.mover.spotify.authorize-url}")
    private String spotifyAuthApiUrl;

    @Value("${playlist.mover.spotify.api_url}")
    private String spotifyApiUrl;

    @Bean
    public RestClient youtubeApiRestClient() {
        return RestClient.builder()
                .baseUrl(youtubeApiUrl)
                .build();
    }

    @Bean
    public RestClient spotifyAuthApiRestClient() {
        return RestClient.builder()
                .baseUrl(spotifyAuthApiUrl)
                .build();
    }

    @Bean
    public RestClient spotifyApiRestClient() {
        return RestClient.builder()
                .baseUrl(spotifyApiUrl)
                .build();
    }
}
