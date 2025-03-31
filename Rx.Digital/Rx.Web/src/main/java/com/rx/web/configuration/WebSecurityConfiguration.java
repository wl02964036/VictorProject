package com.rx.web.configuration;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;

import com.rx.web.security.CustomAuthenticationProvider;
import com.rx.web.security.CustomWebAuthenticationDetailsSource;
import com.rx.web.security.SystemSecurityMetadataSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowSemicolon(true);
        return firewall;
    }

    @Bean
    public WebAuthenticationDetailsSource customWebAuthenticationDetailsSource() {
        return new CustomWebAuthenticationDetailsSource();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return new CustomAuthenticationProvider();
    }

    @Bean
    public FilterInvocationSecurityMetadataSource securityMetadataSource() {
        return new SystemSecurityMetadataSource();
    }

    @Bean
    public AccessDecisionManager accessDecisionManager() {
        return new AffirmativeBased(
                Arrays.asList(
                        new WebExpressionVoter(),
                        new RoleVoter(),
                        new AuthenticatedVoter()
                )
        );
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);

        web.httpFirewall(allowUrlEncodedSlashHttpFirewall());
        web.ignoring()
           .antMatchers("/static/**",
                   "/files/**",
                   "/wav/**",
                   "/images/**",
                   "/upload",
                   "/captchaImage",
                   "/captchaAudio",
                   "/reloadCaptcha",
                   "/captchaNumber",
                   "/callback",
                   "/callback-info");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .enableSessionUrlRewriting(false)
                .sessionFixation()
                    .migrateSession()
            .and()
                .authorizeRequests()
                    .anyRequest()
                        .authenticated()
                            .withObjectPostProcessor(
                                    new ObjectPostProcessor<FilterSecurityInterceptor>() {
                                        public <O extends FilterSecurityInterceptor> O postProcess(O fsi) {
                                            fsi.setAccessDecisionManager(accessDecisionManager());
                                            fsi.setSecurityMetadataSource(securityMetadataSource());
                                            return fsi;
                                        }
                                    }
                            )
            .and()
                .exceptionHandling()
                    .accessDeniedPage("/accessDenied")
            .and()
                .formLogin()
                    .loginPage("/login")
                    .authenticationDetailsSource(customWebAuthenticationDetailsSource())
                    .failureUrl("/login?error=true")
                    .defaultSuccessUrl("/index", true)
                    .permitAll()
            .and()
                .logout()
                    .clearAuthentication(true)
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .logoutSuccessUrl("/login?logout=true")
                    .permitAll()
            .and()
                .headers()
                    .frameOptions()
                        .sameOrigin()
            .and()
                .csrf()
                    .ignoringAntMatchers("/callback",
                                         "/message-push/push",
                                         "/upload");
    }

}
