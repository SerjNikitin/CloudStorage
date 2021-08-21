package domaine.abstarctCommandImpl;

import domaine.AbstractCommand;

public class PathUpRequest extends AbstractCommand {
    @Override
    public CommandType getType() {
        return CommandType.PATH_UP_REQUEST;
    }
}
