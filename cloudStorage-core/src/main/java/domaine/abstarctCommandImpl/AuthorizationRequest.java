package domaine.abstarctCommandImpl;


import lombok.AllArgsConstructor;
import lombok.Getter;
import domaine.AbstractCommand;

@Getter
@AllArgsConstructor
public class AuthorizationRequest extends AbstractCommand {
    private String login;
    private String password;

    @Override
    public CommandType getType() {
        return CommandType.AUTH_REQUEST;
    }
}
