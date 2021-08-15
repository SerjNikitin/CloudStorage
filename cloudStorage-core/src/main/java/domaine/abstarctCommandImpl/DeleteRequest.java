package domaine.abstarctCommandImpl;


import lombok.AllArgsConstructor;
import lombok.Getter;
import domaine.AbstractCommand;

@AllArgsConstructor
@Getter
public class DeleteRequest extends AbstractCommand {
    private String name;

    @Override
    public CommandType getType() {
        return CommandType.DELETE_REQUEST;
    }
}
