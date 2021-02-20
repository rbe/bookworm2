package wbh.bookworm.hoerbuchkatalog.webservice.admin;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

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
@RequestMapping("/v1/private/admin/kontingent")
@RolesAllowed("admin")
public class AdminKontingentController {

    private final HoererService hoererService;

    @Autowired
    public AdminKontingentController(final HoererService hoererService) {
        this.hoererService = hoererService;
    }

    @GetMapping
    public String index() {
        return "redirect:/v1/private/admin";
    }

    @PostMapping
    public ModelAndView kontingent(@Valid @ModelAttribute final AdminDTO adminDTO,
                                   final BindingResult errors, final Model model) {
        final ArrayList<String> result = new ArrayList<>();
        final Hoerernummer hoerernummer = adminDTO.getHoerernummer();
        final int anzahlBestellungenProAusleihzeitraum = adminDTO.getAnzahlBestellungenProAusleihzeitraum();
        final int anzahlBestellungenProTag = adminDTO.getAnzahlBestellungenProTag();
        final int anzahlDownloadsProHoerbuch = adminDTO.getAnzahlDownloadsProHoerbuch();
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
                "admin", new TemplateDTO(adminDTO),
                "result", result
        );
        return new ModelAndView("/private/admin/index", map);
    }

}
