package Presentation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class PresentationReader {
    private final Path pathToPresentations;
    private Map<PresentationInfo, List<Path>> presentations = new HashMap<>();

    public PresentationReader(Path pathToPresentations) {
        this.pathToPresentations = pathToPresentations;
    }

    public void uploadPresentations() {
        try {
            if (Files.isDirectory(Files.list(pathToPresentations).collect(Collectors.toList()).get(0))) {
                for (Path folder : Files.list(pathToPresentations).collect(Collectors.toList())) {
                    PresentationInfo presentationInfo = new PresentationInfo(folder, folder.getFileName().toString());
                    List<Path> paths = new ArrayList<>();
                    paths.addAll(Files.list(folder).collect(Collectors.toList()));
                    presentations.put(presentationInfo, paths);

//                    Files.list(folder).forEach(slide -> {
//                        presentations.get(presentationInfo).add(slide);
//                    });
                }
            } else {
                PresentationInfo presentationInfo = new PresentationInfo(pathToPresentations, pathToPresentations.getFileName().toString());
                List<Path> paths = new ArrayList<>();
                paths.addAll(Files.list(pathToPresentations).collect(Collectors.toList()));
                presentations.put(presentationInfo, paths);
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
