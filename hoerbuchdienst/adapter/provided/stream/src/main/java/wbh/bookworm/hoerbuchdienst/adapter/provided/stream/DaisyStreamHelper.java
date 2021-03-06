package wbh.bookworm.hoerbuchdienst.adapter.provided.stream;

import javax.inject.Inject;
import javax.inject.Singleton;

import wbh.bookworm.hoerbuchdienst.domain.ports.KatalogService;

@Singleton
final class DaisyStreamHelper {

    private final KatalogService katalogService;

    @Inject
    public DaisyStreamHelper(final KatalogService katalogService) {
        this.katalogService = katalogService;
    }

    String titel(final String titelnummer) {
        try {
            final String titel = katalogService.audiobookInfo(titelnummer)
                    .getTitel()
                    .replace(' ', '_')
                    .replaceAll("ä", "ae")
                    .replaceAll("Ä", "Ae")
                    .replaceAll("ö", "oe")
                    .replaceAll("Ö", "Oe")
                    .replaceAll("ü", "ue")
                    .replaceAll("ü", "Ue")
                    .replaceAll("ß", "ss")
                    .replaceAll("[^\\p{ASCII}]", "")
                    .replaceAll("[ ,\\.]", "_")
                    .replaceAll("__", "_");
            return "%s-%s".formatted(titelnummer, titel);
        } catch (RuntimeException e) {
            return "%s".formatted(titelnummer);
        }
    }

}
