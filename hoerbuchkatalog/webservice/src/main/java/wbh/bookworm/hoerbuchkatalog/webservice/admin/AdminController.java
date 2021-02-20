package wbh.bookworm.hoerbuchkatalog.webservice.admin;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/v1/private/admin")
public class AdminController {

    @GetMapping
    public ModelAndView index() {
        final TemplateDTO templateDTO = new TemplateDTO();
        final Map<String, Serializable> map = Map.of(
                "admin", templateDTO,
                "result", ""
        );
        return new ModelAndView("/private/admin/index", map);
    }

    @GetMapping(path = "/logout")
    public String logout(HttpServletRequest request) throws ServletException {
        request.logout();
        return "/v1/private/admin";
    }

}
