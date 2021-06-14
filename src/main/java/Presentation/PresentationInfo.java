package Presentation;

import java.nio.file.Path;

import static Utils.RecordUtils.toUTF8;

public class PresentationInfo {
    private final Path pathToPresentation;
    private final String name;

    public PresentationInfo(Path pathToPresentation, String name) {
        this.pathToPresentation = pathToPresentation;
        this.name = name;
    }

    public Path getPathToPresentation() {
        return pathToPresentation;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return toUTF8(name);
    }
}
