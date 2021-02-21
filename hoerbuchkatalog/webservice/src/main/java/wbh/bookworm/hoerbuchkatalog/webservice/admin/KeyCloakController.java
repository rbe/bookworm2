package wbh.bookworm.hoerbuchkatalog.webservice.admin;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/v1/private/admin")
class KeyCloakController {

    private static final Logger LOGGER = LoggerFactory.getLogger(KeyCloakController.class);

    @RequestMapping(value = "/logout", method = {GET, POST})
    public String logout(HttpServletRequest request) {
        try {
            request.logout();
            return "redirect:/v1/private/admin";
        } catch (ServletException e) {
            LOGGER.error("", e);
            return "/error";
        }
    }

}
