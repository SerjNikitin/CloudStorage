package domaine.abstarctCommandImpl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import domaine.AbstractCommand;

@Getter
@AllArgsConstructor
public class RegistrationRequest extends AbstractCommand {
    private String name;
    private String login;
    private String password;

    @Override
    public CommandType getType() {
        return CommandType.REGISTRATION_REQUEST;
    }
}
