package wbh.bookworm.hoerbuchkatalog.webservice.admin;

import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.KeycloakDeploymentBuilder;
import org.keycloak.adapters.spi.HttpFacade;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
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
                .antMatchers("/actuator", "/actuator/**").anonymous()
                .antMatchers("/sba", "/sba/**").anonymous()
                .antMatchers("/**/private/**").hasRole("admin")
                .anyRequest().permitAll()
        ;
        http.csrf().disable();
        //.ignoringAntMatchers("/actuator/**");
    }

    /*
    @Bean
    @Primary
    public KeycloakConfigResolver keycloakConfigResolver(final KeycloakSpringBootProperties properties) {
        return new MyKeycloakSpringBootConfigResolver(properties);
    }

    public class MyKeycloakSpringBootConfigResolver extends KeycloakSpringBootConfigResolver {

        private final KeycloakDeployment keycloakDeployment;

        public MyKeycloakSpringBootConfigResolver(final KeycloakSpringBootProperties properties) {
            keycloakDeployment = KeycloakDeploymentBuilder.build(properties);
        }

        @Override
        public KeycloakDeployment resolve(final HttpFacade.Request facade) {
            return keycloakDeployment;
        }

    }
    */

    /*
    @Bean
    public KeycloakConfigResolver keycloakConfigResolver() {
        return new KeycloakConfigResolver() {

            private KeycloakDeployment keycloakDeployment;

            @Override
            public KeycloakDeployment resolve(HttpFacade.Request facade) {
                if (keycloakDeployment != null) {
                    return keycloakDeployment;
                }

                String path = "/keycloak.json";
                InputStream configInputStream = getClass().getResourceAsStream(path);

                if (configInputStream == null) {
                    throw new RuntimeException("Could not load Keycloak deployment info: " + path);
                } else {
                    keycloakDeployment = KeycloakDeploymentBuilder.build(configInputStream);
                }

                return keycloakDeployment;
            }
        };
    }
    */

}
