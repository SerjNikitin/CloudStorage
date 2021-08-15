package domaine.abstarctCommandImpl;

import lombok.Getter;
import domaine.AbstractCommand;


@Getter
public class FileRequest extends AbstractCommand {

    private final String name;

    public FileRequest(String name) {
        this.name = name;
    }

    @Override
    public CommandType getType() {
        return CommandType.FILE_REQUEST;
    }
}
