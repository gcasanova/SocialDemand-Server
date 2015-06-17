package app.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import app.security.config.filters.StatelessAuthenticationFilter;
import app.security.config.filters.StatelessLoginFilter;
import app.security.service.TokenAuthenticationService;
import app.security.service.UserDetailsService;

/*
 * Security configuration class
 */

@Order(1)
@Configuration
@EnableWebSecurity
public class StatelessAuthenticationSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private TokenAuthenticationService tokenAuthenticationService;

	public StatelessAuthenticationSecurityConfig() {
		super(true);
	}
	
	@Override
    public void configure(WebSecurity webSecurity) throws Exception {
        webSecurity
            .ignoring()
                // All of Spring Security will ignore the requests
                .antMatchers("/resources/**")
                .antMatchers(HttpMethod.GET, "/api/**")
                .antMatchers(HttpMethod.POST, "/api/auth/signup")
        		.antMatchers(HttpMethod.POST, "/api/auth/reset")
        		.antMatchers(HttpMethod.POST, "/api/auth/change");
    }

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.exceptionHandling().and().servletApi().and().headers().cacheControl().and().and().authorizeRequests()
		
				// allow anonymous POSTs to auth
				.antMatchers(HttpMethod.POST, "/api/token/auth").permitAll()

				// defined admin only API area
				.antMatchers("/admin/**").hasRole("ADMIN")

				// all other request need to be authenticated
				.anyRequest().hasAnyRole("GUEST", "USER", "ADMIN").and()

				// custom JSON based authentication by POST of {"username":"<name>","password":"<password>"} which sets the token header upon authentication
				.addFilterBefore(new StatelessLoginFilter("/api/token/auth", tokenAuthenticationService, userDetailsService, authenticationManager()), UsernamePasswordAuthenticationFilter.class)

				// custom Token based authentication based on the header previously given to the client
				.addFilterBefore(new StatelessAuthenticationFilter(tokenAuthenticationService), UsernamePasswordAuthenticationFilter.class);
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
	}

	@Override
	protected UserDetailsService userDetailsService() {
		return userDetailsService;
	}
}
