package wbh.bookworm.hoerbuchkatalog.webservice.admin;

import javax.annotation.security.RolesAllowed;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(AdminConstants.BASE_URL)
@RolesAllowed("admin")
public class AdminController {

    @GetMapping
    public String index() {
        return "redirect:kontingent";
    }

}
