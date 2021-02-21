package wbh.bookworm.hoerbuchkatalog.webservice.admin;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import wbh.bookworm.hoerbuchkatalog.app.hoerer.HoererService;
import wbh.bookworm.shared.domain.Hoerernummer;

@Controller
@RequestMapping(AdminConstants.BASE_URL + "/kontingent")
@RolesAllowed("admin")
class AdminKontingentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminKontingentController.class);

    private final HoererService hoererService;

    @Autowired
    AdminKontingentController(final HoererService hoererService) {
        this.hoererService = hoererService;
    }

    @GetMapping
    public ModelAndView index() {
        final Map<String, Serializable> map = Map.of(
                "alle", alleHoererTemplateDTO(),
                "hoerer", new TemplateModel(),
                "result", ""
        );
        return new ModelAndView(AdminConstants.INDEX_TEMPLATE, map);
    }

    @PostMapping(params = "abfragen")
    public ModelAndView abfragen(@Valid @ModelAttribute final Kontingent kontingent,
                                 final BindingResult errors, final Model model) {
        final Map<String, Serializable> map = Map.of(
                "alle", alleHoererTemplateDTO(),
                "hoerer", frageHoererAb(kontingent.getHoerernummer()),
                "result", ""
        );
        return new ModelAndView(AdminConstants.INDEX_TEMPLATE, map);
    }

    @PostMapping(params = "setzen")
    public ModelAndView setzen(@Valid @ModelAttribute final Kontingent kontingent,
                               final BindingResult errors, final Model model) {
        final ArrayList<String> result = new ArrayList<>();
        final Hoerernummer hoerernummer = kontingent.getHoerernummer();
        final int anzahlBestellungenProAusleihzeitraum = kontingent.getAnzahlBestellungenProAusleihzeitraum();
        final int anzahlBestellungenProTag = kontingent.getAnzahlBestellungenProTag();
        final int anzahlDownloadsProHoerbuch = kontingent.getAnzahlDownloadsProHoerbuch();
        if (anzahlBestellungenProAusleihzeitraum > 0) {
            hoererService.neueAnzahlBestellungenProAusleihzeitraum(hoerernummer, anzahlBestellungenProAusleihzeitraum);
            result.add("Kontingent " + anzahlBestellungenProAusleihzeitraum + " Bestellungen pro Ausleihzeitraum für Hörer " + hoerernummer + " gesetzt");
        }
        if (anzahlBestellungenProTag > 0) {
            hoererService.neueAnzahlBestellungenProTag(hoerernummer, anzahlBestellungenProTag);
            result.add("Kontingent " + anzahlBestellungenProTag + " Bestellungen pro Tag für Hörer " + hoerernummer + " gesetzt");
        }
        if (anzahlDownloadsProHoerbuch > 0) {
            hoererService.neueAnzahlDownloadsProHoerbuch(hoerernummer, anzahlDownloadsProHoerbuch);
            result.add("Kontingent " + anzahlDownloadsProHoerbuch + " Downloads pro Hörbuch für Hörer " + hoerernummer + " gesetzt");
        }
        final Map<String, Serializable> map = Map.of(
                "alle", alleHoererTemplateDTO(),
                "hoerer", hoerernummer.isBekannt() ? frageHoererAb(hoerernummer) : new TemplateModel(),
                "result", result
        );
        return new ModelAndView(AdminConstants.INDEX_TEMPLATE, map);
    }

    @PostMapping(params = "freiputzen")
    public ModelAndView freiputzen(@Valid @ModelAttribute final Kontingent kontingent,
                                   final BindingResult errors, final Model model) {
        final Hoerernummer hoerernummer = kontingent.getHoerernummer();
        final boolean geputzt = hoererService.freiputzen(hoerernummer);
        final ArrayList<String> result = new ArrayList<>();
        result.add("Hörer %s %s".formatted(hoerernummer, geputzt ? "freigeputzt" : "nicht freigeputzt"));
        final Map<String, Serializable> map = Map.of(
                "alle", alleHoererTemplateDTO(),
                "hoerer", frageHoererAb(hoerernummer),
                "result", result
        );
        return new ModelAndView(AdminConstants.INDEX_TEMPLATE, map);
    }

    private TemplateModel frageHoererAb(final Hoerernummer hoerernummer) {
        final TemplateModel templateModel = new TemplateModel();
        templateModel.setHoerernummer(hoerernummer);
        templateModel.setAnzahlBestellungenProAusleihzeitraum(hoererService.anzahlBestellungenProAusleihzeitraum(hoerernummer));
        templateModel.setAnzahlBestellungenProTag(hoererService.anzahlBestellungenProTag(hoerernummer));
        templateModel.setAnzahlDownloadsProHoerbuch(hoererService.anzahlDownloadsProHoerbuch(hoerernummer));
        return templateModel;
    }

    private TemplateModel alleHoererTemplateDTO() {
        final TemplateModel alleTemplateModel = new TemplateModel();
        final Hoerernummer hoerernummer = Hoerernummer.UNBEKANNT;
        alleTemplateModel.setAnzahlBestellungenProAusleihzeitraum(hoererService.anzahlBestellungenProAusleihzeitraum(hoerernummer));
        alleTemplateModel.setAnzahlBestellungenProTag(hoererService.anzahlBestellungenProTag(hoerernummer));
        alleTemplateModel.setAnzahlDownloadsProHoerbuch(hoererService.anzahlDownloadsProHoerbuch(hoerernummer));
        return alleTemplateModel;
    }

}
