package ro.unibuc.hello.data.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
@Document(collection = "user")
public class UserEntity {
    @Id
    private String id;

    private String username;

    private String password;

    private List <String> roles = new ArrayList<>(Arrays.asList("USER"));
}
