package Presentation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SlideReader {
    private final Path pathToPresentations;
    private Map<String, List<Path>> presentations = new HashMap<>();

    public SlideReader(Path pathToPresentations) {
        this.pathToPresentations = pathToPresentations;
    }

    public void uploadPresentations() {
        try {
            for(Path folder : Files.list(pathToPresentations).collect(Collectors.toList())) {
                presentations.put(folder.toString(), new ArrayList<>());
                Files.list(folder).forEach(slide -> {
                     presentations.get(folder.toString()).add(slide);
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
            //TODO add logs
        }
    }

    public Map<String, List<Path>> getPresentations() {
        return presentations;
    }

    public void setPresentations(Map<String, List<Path>> presentations) {
        this.presentations = presentations;
    }
}
