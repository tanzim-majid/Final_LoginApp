package com.application.loginApp.service;

import com.application.loginApp.model.UsersModel;
import com.application.loginApp.repository.UsersRepository;
import org.owasp.encoder.Encoder;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Optional;

@Service
public class UsersService {

    private final UsersRepository usersRepository;
    Base64.Encoder encoder = Base64.getEncoder();

    public UsersService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public UsersModel registerUser(String login, String password,String email){
        if (login == null || password == null) {
            return null;
        } else {
            if(usersRepository.findFirstByLogin(login).isPresent()){
                System.out.println("Duplicate login");
                return null;
            }
            UsersModel usersModel = new UsersModel();
            usersModel.setLogin(login);
            usersModel.setPassword(encoder.encodeToString(password.getBytes()));
            usersModel.setEmail(email);
            return usersRepository.save(usersModel);
        }
    }

    public UsersModel changeUserPassword(String login, String password,String email){
        if (login == null || password == null) {
            System.out.println("Missing required information");
            return null;
        } else {
            if(usersRepository.findFirstByLogin(login).isPresent()){
                System.out.println("User found");
                UsersModel usersModel = usersRepository.findByLoginAndEmail(login, email);
                usersModel.setPassword(encoder.encodeToString(password.getBytes()));
                return usersRepository.save(usersModel);
            }
            else{
                System.out.println("No user with given username");
                return null;
            }
        }
    }

    private String sanitizeHTML(String untrustedHTML){
        PolicyFactory policy = new HtmlPolicyBuilder()
                .allowAttributes("src").onElements("img")
                .allowAttributes("href").onElements("a")
                .allowStandardUrlProtocols()
                .allowElements(
                        "a", "img"
                ).toFactory();

        return policy.sanitize(untrustedHTML);
    }
    public UsersModel submitFeedback(String login, String feedback,String email){
        if (login == null || email == null) {
            System.out.println("Missing required information");
            return null;
        } else {
            if(usersRepository.findFirstByLogin(login).isPresent()){
                System.out.println("User found");
                UsersModel usersModel = usersRepository.findByLoginAndEmail(login, email);
                usersModel.setFeedback(sanitizeHTML(feedback));
                return usersRepository.save(usersModel);
            }
            else{
                System.out.println("No user with given username");
                return null;
            }
        }
    }

    public UsersModel authenticate(String login, String password){
        return usersRepository.findByLoginAndPassword(login, encoder.encodeToString(password.getBytes())).orElse(null);
    }
}
