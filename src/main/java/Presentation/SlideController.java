package Presentation;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public class SlideController {
    final SlideReader reader;
    String currentPresentation;
    Integer currentSlide;

    public SlideController(Path pathToFile) {
        reader = new SlideReader(pathToFile);
        reader.uploadPresentations();
        if (!reader.getPresentations().isEmpty()) {
            currentPresentation = reader.getPresentations().keySet().stream().findFirst().get();
            currentSlide = 0;
        }
    }

    public Collection<String> getPresentationsNames() {
        return reader.getPresentations().keySet();
    }

    public void setCurrentPresentation(String currentPresentation) {
        this.currentPresentation = currentPresentation;
        currentSlide = 0;
    }

    public Path nextSlide() {
        List<Path> pres = reader.getPresentations().get(currentPresentation);
        if (pres.size() - 1 > currentSlide)
            return pres.get(++currentSlide);
        else
            return pres.get(currentSlide);
    }

    public Path prevSlide() {
        List<Path> pres = reader.getPresentations().get(currentPresentation);
        if (currentSlide - 1 >= 0)
            return pres.get(--currentSlide);
        else
            return pres.get(currentSlide);
    }

    public Path currentSlide() {
        return reader.getPresentations().get(currentPresentation).get(currentSlide);
    }
}
