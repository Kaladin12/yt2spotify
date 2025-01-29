package kaladin.zwolf.projects.playlist.mover.domain.h2;

import lombok.Data;

@Data
public abstract class BaseEntity {
    private Long id;
    private String original_name;
    private String overwritten_name;
}
