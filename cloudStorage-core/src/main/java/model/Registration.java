package model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Registration extends AbstractAuth{
    private String name;
    private String login;
    private String password;

    @Override
    public AuthType getTape() {
        return AuthType.REGISTRATION;
    }
}
