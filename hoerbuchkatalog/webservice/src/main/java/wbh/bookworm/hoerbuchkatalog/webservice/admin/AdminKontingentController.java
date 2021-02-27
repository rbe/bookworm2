package wbh.bookworm.hoerbuchkatalog.webservice.admin;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.w3c.dom.stylesheets.LinkStyle;

import wbh.bookworm.hoerbuchkatalog.app.hoerer.HoererService;
import wbh.bookworm.shared.domain.Hoerernummer;

@Controller
@RequestMapping(AdminConstants.BASE_URL + "/kontingent")
@RolesAllowed("admin")
class AdminKontingentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminKontingentController.class);

    private final String oauth2BaseUrl;

    private final HoererService hoererService;

    @Autowired
    AdminKontingentController(@Value("${OAUTH2_BASEURL}") final String oauth2BaseUrl,
                              final HoererService hoererService) {
        this.oauth2BaseUrl = oauth2BaseUrl;
        this.hoererService = hoererService;
    }

    @GetMapping
    public ModelAndView index() {
        final Map<String, Serializable> map = model(alleHoererTemplateDTO(), new TemplateModel(), new ArrayList<>());
        return new ModelAndView(AdminConstants.TEMPLATE_WBH_ADMIN, map);
    }

    @PostMapping(params = "abfragen")
    public ModelAndView abfragen(@Valid @ModelAttribute final Kontingent kontingent,
                                 final BindingResult errors, final Model model) {
        return new ModelAndView(AdminConstants.TEMPLATE_WBH_ADMIN,
                model(alleHoererTemplateDTO(),
                        frageHoererAb(kontingent.getHoerernummer()),
                        new ArrayList<>()));
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
        return new ModelAndView(AdminConstants.TEMPLATE_WBH_ADMIN,
                model(alleHoererTemplateDTO(),
                        hoerernummer.isBekannt() ? frageHoererAb(hoerernummer) : new TemplateModel(),
                        result));
    }

    @PostMapping(params = "freiputzen")
    public ModelAndView freiputzen(@Valid @ModelAttribute final Kontingent kontingent,
                                   final BindingResult errors, final Model model) {
        final Hoerernummer hoerernummer = kontingent.getHoerernummer();
        final boolean geputzt = hoererService.freiputzen(hoerernummer);
        final ArrayList<String> result = new ArrayList<>();
        result.add("Hörer %s %s".formatted(hoerernummer, geputzt ? "freigeputzt" : "nicht freigeputzt"));
        return new ModelAndView(AdminConstants.TEMPLATE_WBH_ADMIN,
                model(alleHoererTemplateDTO(), frageHoererAb(hoerernummer), result));
    }

    private Map<String, Serializable> model(final TemplateModel alle,
                                            final TemplateModel hoerer,
                                            final ArrayList<String> result) {
        final Map<String, Serializable> map = new HashMap<>();
        map.put("oauth2_baseurl", oauth2BaseUrl);
        map.put("alle", alle);
        map.put("hoerer", hoerer);
        map.put("result", result);
        return map;
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
        alleTemplateModel.setAnzahlBestellungenProAusleihzeitraum(
                hoererService.anzahlBestellungenProAusleihzeitraum(hoerernummer));
        alleTemplateModel.setAnzahlBestellungenProTag(
                hoererService.anzahlBestellungenProTag(hoerernummer));
        alleTemplateModel.setAnzahlDownloadsProHoerbuch(
                hoererService.anzahlDownloadsProHoerbuch(hoerernummer));
        return alleTemplateModel;
    }

}
