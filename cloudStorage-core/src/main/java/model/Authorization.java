package model;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Authorization extends  AbstractAuth{
    private String login;
    private String password;


    @Override
    public AuthType getTape() {
        return AuthType.AUTHORIZATION;
    }
}
