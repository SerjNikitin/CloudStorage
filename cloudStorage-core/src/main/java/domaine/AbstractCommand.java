package domaine;

import domaine.abstarctCommandImpl.CommandType;

import java.io.Serializable;

public abstract class AbstractCommand implements Serializable {
    public abstract CommandType getType();
}
