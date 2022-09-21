package spring.framework.stackholder.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import spring.framework.stackholder.Repositories.UserRepository;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;


    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {


        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(encoder());
        http.csrf().disable();

        http.logout().logoutUrl("/user/logout").invalidateHttpSession(true).deleteCookies("JSESSIONID");
        http.apply(new CustomDSL(userRepository));
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests().antMatchers("/user/signup/**").permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.GET, "/user/get/**").permitAll();
        http.authorizeRequests().antMatchers("/user/login/**").permitAll();
        http.authorizeRequests().antMatchers("/user/verify/**").permitAll();
        http.authorizeRequests().antMatchers("/user/forgotpassword/**").permitAll();
        http.authorizeRequests().antMatchers("/user/updateforgotpassword/**").permitAll();
        http.authorizeRequests().antMatchers("/user/checkusername/**").permitAll();
        http.authorizeRequests().antMatchers("/user/checkemail/**").permitAll();
        http.authorizeRequests().antMatchers("/admin/get/**").permitAll();

        http.authorizeRequests().antMatchers("/v2/api-docs/**").permitAll();
        http.authorizeRequests().antMatchers("/swagger-resources/**").permitAll();
        http.authorizeRequests().antMatchers("/swagger-ui.html/**").permitAll();
        http.authorizeRequests().antMatchers("/swagger-ui/**").permitAll();

        http.authorizeRequests().antMatchers(HttpMethod.GET, "/user/**").hasAnyAuthority("ADMIN");
        http.authorizeRequests().antMatchers(HttpMethod.PUT, "/user/**").hasAnyAuthority("ADMIN");
        http.authorizeRequests().antMatchers(HttpMethod.POST, "/user/**").hasAnyAuthority("ADMIN");
        http.authorizeRequests().antMatchers(HttpMethod.DELETE, "/user/deleteaccount/**").hasAnyAuthority("ADMIN");
        http.authorizeRequests().antMatchers(HttpMethod.GET, "/admin/**").hasAnyAuthority("ADMIN");
        http.authorizeRequests().antMatchers(HttpMethod.PUT, "/admin/**").hasAnyAuthority("ADMIN");
        http.authorizeRequests().antMatchers(HttpMethod.POST, "/admin/**").hasAnyAuthority("ADMIN");
        http.authorizeRequests().antMatchers(HttpMethod.DELETE, "/admin/deleteaccount/**").hasAnyAuthority("ADMIN");

        http.authorizeRequests().antMatchers(HttpMethod.POST, "/set/addSet/**","/set/getSets").hasAnyAuthority("ADMIN");
        http.authorizeRequests().antMatchers(HttpMethod.PUT, "/set/updateSet/**").hasAnyAuthority("ADMIN");
        http.authorizeRequests().antMatchers(HttpMethod.DELETE, "/set/deleteSet/**").hasAnyAuthority("ADMIN");

        http.authorizeRequests().antMatchers(HttpMethod.POST, "/stakeholder/addStakeholder/**","/stakeholder/getStakeholders/**").hasAnyAuthority("ADMIN");
        http.authorizeRequests().antMatchers(HttpMethod.PUT, "/stakeholder/updateStakeholder/**").hasAnyAuthority("ADMIN");
        http.authorizeRequests().antMatchers(HttpMethod.DELETE, "/stakeholder/deleteStakeholder/**").hasAnyAuthority("ADMIN");

        http.authorizeRequests().antMatchers(HttpMethod.POST, "/objective/addObjective/**","/objective/getObjectives/**").hasAnyAuthority("ADMIN");
        http.authorizeRequests().antMatchers(HttpMethod.PUT, "/objective/updateObjective/**").hasAnyAuthority("ADMIN");
        http.authorizeRequests().antMatchers(HttpMethod.DELETE, "/objective/deleteObjective/**").hasAnyAuthority("ADMIN");

        http.authorizeRequests().antMatchers(HttpMethod.POST, "/priority/getPriority/**","/priority/getPriority/**").hasAnyAuthority("ADMIN");
        http.authorizeRequests().antMatchers(HttpMethod.DELETE, "/priority/deletePriority/**").hasAnyAuthority("ADMIN");
        http.authorizeRequests().antMatchers(HttpMethod.PUT, "/priority/updatePriority/**").hasAnyAuthority("ADMIN");

        http.authorizeRequests().anyRequest().authenticated();
        http.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    public static class CustomDSL extends AbstractHttpConfigurer<CustomDSL, HttpSecurity> {

        private final UserRepository userRepository;

        public CustomDSL(UserRepository userRepository) {
            this.userRepository = userRepository;
        }

        @Override
        public void configure(HttpSecurity http) {
            AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
            http.addFilter(new CustomAuthenticationFilter(authenticationManager, userRepository));
        }

        public CustomDSL customDsl() {
            return new CustomDSL(userRepository);
        }
    }

}
