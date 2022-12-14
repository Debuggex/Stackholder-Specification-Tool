package spring.framework.stackholder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Properties;

@SpringBootApplication
public class StackHolderApplication {

    public static void main(String[] args) {
        SpringApplication.run(StackHolderApplication.class, args);
    }


    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JavaMailSender mailSender(){
        JavaMailSenderImpl mailSender1=new JavaMailSenderImpl();
        mailSender1.setHost("smtp.gmail.com");
        mailSender1.setPort(587);

        mailSender1.setUsername("sahbaanalam34@gmail.com");
        mailSender1.setPassword("");

        Properties props = mailSender1.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender1;
    }


}
