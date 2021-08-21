package domaine;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    private String name;
    private String login;
    private String password;
}
