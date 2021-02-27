package wbh.bookworm.hoerbuchkatalog.webservice.security;

import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

/**
 * https://oidcdebugger.com
 */
@EnableGlobalMethodSecurity(jsr250Enabled = true)
@KeycloakConfiguration
public class KeyCloakSecurityConfig extends KeycloakWebSecurityConfigurerAdapter {

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        final KeycloakAuthenticationProvider keycloakAuthenticationProvider = keycloakAuthenticationProvider();
        keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
        auth.authenticationProvider(keycloakAuthenticationProvider);
    }

    @Override
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        super.configure(http);
        http
                .logout()
                .addLogoutHandler((request, response, authentication) -> {
                })
                .logoutSuccessUrl("/")
                .and()
                .authorizeRequests()
                .antMatchers("/actuator", "/actuator/**").hasRole("admin")
                .antMatchers("/sba", "/sba/**").hasRole("admin")
                .antMatchers("/**/private/**").hasRole("admin")
                //.antMatchers(HttpMethod.OPTIONS).permitAll()
                .antMatchers("/").permitAll()
                .anyRequest().permitAll()
        ;
        http.csrf().disable();
        //.ignoringAntMatchers("/actuator/**");
    }

}
