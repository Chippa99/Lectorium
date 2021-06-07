package Presentation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PresentationReader {
    private final Path pathToPresentations;
    private Map<PresentationInfo, List<Path>> presentations = new HashMap<>();

    public PresentationReader(Path pathToPresentations) {
        this.pathToPresentations = pathToPresentations;
    }

    public void uploadPresentations() {
        try {
            for(Path folder : Files.list(pathToPresentations).collect(Collectors.toList())) {
                PresentationInfo presentationInfo = new PresentationInfo(folder, folder.getFileName().toString());
                presentations.put(presentationInfo, new ArrayList<>());
                Files.list(folder).forEach(slide -> {
                     presentations.get(presentationInfo).add(slide);
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
            //TODO add logs
        }
    }

    public Map<PresentationInfo, List<Path>> getPresentations() {
        return presentations;
    }

    public void setPresentations(Map<PresentationInfo, List<Path>> presentations) {
        this.presentations = presentations;
    }
}
