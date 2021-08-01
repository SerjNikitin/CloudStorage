package model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthenticationResponse extends AbstractAuth {

    private final String name;
    private final String login;
    private final String password;

    public AuthenticationResponse(User user) {
        name = user.getName();
        login = user.getLogin();
        password = user.getPassword();
    }

    @Override
    public AuthType getTape() {
        return AuthType.AUTH_RESPONSE;
    }
}