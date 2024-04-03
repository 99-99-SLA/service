package ro.unibuc.hello.dto.tmdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class ReviewDto {
    private String username;
    private String movieId;
    private int rating;
    private String comment;
    // No need for createdAt field since it will be set when saving the review.
}

