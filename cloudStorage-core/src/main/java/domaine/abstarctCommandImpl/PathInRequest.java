package domaine.abstarctCommandImpl;

import lombok.Getter;
import domaine.AbstractCommand;

@Getter
public class PathInRequest extends AbstractCommand {
    private final String path;

    public PathInRequest(String path) {
        this.path = path;
    }

    @Override
    public CommandType getType() {
        return CommandType.PATH_IN_REQUEST;
    }
}
