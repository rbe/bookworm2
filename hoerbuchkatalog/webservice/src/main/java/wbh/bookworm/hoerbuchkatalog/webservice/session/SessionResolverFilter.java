package wbh.bookworm.hoerbuchkatalog.webservice.session;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import wbh.bookworm.hoerbuchkatalog.app.bestellung.BestellungService;

@WebFilter(urlPatterns = "/**")
@Component
public class SessionResolverFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionResolverFilter.class);

    private final BestellungService bestellungService;

    @Autowired
    public SessionResolverFilter(final BestellungService bestellungService) {
        this.bestellungService = bestellungService;
    }

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        final String bestellungSessionId = httpServletRequest.getHeader("X-Bookworm-BestellungSessionId");
        LOGGER.debug("X-Bookworm-BestellungSessionId: {}", bestellungSessionId);
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

}
