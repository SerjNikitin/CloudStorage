package domaine.abstarctCommandImpl;


import lombok.AllArgsConstructor;
import lombok.Getter;
import domaine.AbstractCommand;
import domaine.User;

@Getter
@AllArgsConstructor
public class AuthenticationResponse extends AbstractCommand {

    private final String name;
    private final String login;
    private final String password;

    public AuthenticationResponse(User user) {
        name = user.getName();
        login = user.getLogin();
        password = user.getPassword();
    }

    @Override
    public CommandType getType() {
        return CommandType.AUTH_RESPONSE;
    }
}