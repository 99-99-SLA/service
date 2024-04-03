package ro.unibuc.hello.data.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@EqualsAndHashCode
public class Movie {
    @Id
    @JsonProperty("id")
    private String id;

    @NotNull
    @JsonProperty("title")
    private String title;

    @NotNull
    @JsonProperty("description")
    private String description;

    @JsonProperty("popularity")
    private Integer popularity;

    @JsonProperty("genres")
    private List<String> genres;

    @JsonProperty("tmdbId")
    private Long tmdbId;

    @JsonProperty("year")
    private Long year;
}
