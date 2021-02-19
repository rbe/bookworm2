package wbh.bookworm.hoerbuchkatalog.webservice.admin;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import wbh.bookworm.hoerbuchkatalog.app.hoerer.HoererService;
import wbh.bookworm.shared.domain.Hoerernummer;

@Controller
@RequestMapping("/v1/private/admin/abfrage")
public class AdminAbfrageController {

    private final HoererService hoererService;

    @Autowired
    public AdminAbfrageController(final HoererService hoererService) {
        this.hoererService = hoererService;
    }

    @GetMapping
    public String index() {
        return "redirect:/v1/private/admin";
    }

    @PostMapping
    public ModelAndView query(@Valid @ModelAttribute final AdminDTO adminDTO) {
        final TemplateDTO templateDTO = new TemplateDTO();
        final Hoerernummer hoerernummer = adminDTO.getHoerernummer();
        templateDTO.setHoerernummer(hoerernummer);
        templateDTO.setAnzahlBestellungenProAusleihzeitraum(hoererService.anzahlBestellungenProAusleihzeitraum(hoerernummer));
        templateDTO.setAnzahlBestellungenProTag(hoererService.anzahlBestellungenProTag(hoerernummer));
        templateDTO.setAnzahlDownloadsProHoerbuch(hoererService.anzahlDownloadsProHoerbuch(hoerernummer));
        final Map<String, Serializable> map = Map.of(
                "admin", templateDTO,
                "result", ""
        );
        return new ModelAndView("/private/admin/index", map);
    }

}
