package com.akshayan.emailservice;

import com.akshayan.emailservice.model.NotificationEmail;
import com.akshayan.emailservice.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.kafka.annotation.KafkaListener;

@SpringBootApplication
@EnableEurekaClient
public class EmailServiceApplication {

    private final MailService mailService;

    public EmailServiceApplication(MailService mailService) {
        this.mailService = mailService;
    }

    public static void main(String[] args) {
        SpringApplication.run(EmailServiceApplication.class, args);
    }

    @KafkaListener(topics = "activationEmail")
    public void handleActivationEmail(ActivateAccountEvent activateAccountEvent){
        System.out.println("Received activation email event"+activateAccountEvent);
        NotificationEmail notificationEmail= new NotificationEmail(
                "Please Activate your Account",activateAccountEvent.getEmail(),
                "Thank you for signing up to Forum Falcon, " +
                        "please click on the below url to activate your account : " +
                        "http://localhost:8080/api/auth/accountVerification/" + activateAccountEvent.getToken());
        mailService.sendMail(notificationEmail);
    }
}
