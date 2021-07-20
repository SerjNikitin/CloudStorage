package server;


import lombok.Getter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ListResponse extends AbstractCommand {

    private final List<String> name;

    public ListResponse(Path path) throws IOException {
        name = Files.list(path)
                .map(p -> p.getFileName().toString())
                .collect(Collectors.toList());
    }

    @Override
    public CommandType getType() {
        return CommandType.LIST_MESSAGE;
    }
}
