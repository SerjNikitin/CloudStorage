package domaine.abstarctCommandImpl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import domaine.AbstractCommand;

@ToString
@Getter
@NoArgsConstructor
public class ListRequest extends AbstractCommand {

    @Override
    public CommandType getType() {
        return CommandType.LIST_REQUEST;
    }
}