package spring.framework.stackholder.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import spring.framework.stackholder.Repositories.UserRepository;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private final UserDetailsService userDetailsService;

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
//    @Bean
//    public BCryptPasswordEncoder encoder(){
//        return new BCryptPasswordEncoder();
//    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        CustomAuthenticationFilter customAuthenticationFilter=new CustomAuthenticationFilter(authenticationManager(), userRepository);
        customAuthenticationFilter.setFilterProcessesUrl("/user/login");
//        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
//        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(encoder());
        http.csrf().disable();
        //http.apply(new CustomDSL(userRepository));
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests().antMatchers("/user/signup/**").permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.GET,"/user/get/**").permitAll();
        http.authorizeRequests().antMatchers("/user/login/**").permitAll();
        http.authorizeRequests().antMatchers("/user/verify/**").permitAll();
        http.authorizeRequests().antMatchers("/user/forgotpassword/**").permitAll();
        http.authorizeRequests().antMatchers("/user/forgotpassword/**").permitAll();

        http.authorizeRequests().antMatchers("/v2/api-docs/**").permitAll();
        http.authorizeRequests().antMatchers("/swagger-resources/**").permitAll();
        http.authorizeRequests().antMatchers("/swagger-ui.html/**").permitAll();
        http.authorizeRequests().antMatchers("/swagger-ui/**").permitAll();


        http.requiresChannel()
                .requestMatchers(r -> r.getHeader("X-Forwarded-Proto") != null)
                .requiresSecure();
        //http.authorizeRequests().antMatchers("/dashboard/**").permitAll();

        http.authorizeRequests().antMatchers(HttpMethod.GET,"/user/**").hasAnyAuthority("ADMIN");
        http.authorizeRequests().antMatchers(HttpMethod.POST,"/user/**").hasAnyAuthority("ADMIN");
//        http.authorizeRequests().antMatchers(HttpMethod.GET,"/book/**").hasAnyAuthority("Author");
//        http.authorizeRequests().antMatchers(HttpMethod.POST,"/book/**").hasAnyAuthority("Author");

        http.authorizeRequests().anyRequest().authenticated();
        http.addFilter(customAuthenticationFilter);

    }
    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }
//    public static class CustomDSL extends AbstractHttpConfigurer<CustomDSL,HttpSecurity>{
//
//        private final UserRepository userRepository;
//
//        public CustomDSL(UserRepository userRepository) {
//            this.userRepository = userRepository;
//        }
//
//        @Override
//        public void configure(HttpSecurity http) throws Exception {
//            AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
//            http.addFilter(new CustomAuthenticationFilter(authenticationManager, userRepository));
//        }
//    }

}
