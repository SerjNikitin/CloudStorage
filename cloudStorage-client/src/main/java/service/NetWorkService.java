package service;

import domaine.AbstractCommand;

public interface NetWorkService {

    void sendCommand(AbstractCommand abstractCommand);

    AbstractCommand readCommandResult();

    void closeConnection();
}
