package com.application.loginApp.controller;

import com.application.loginApp.model.UsersModel;
import com.application.loginApp.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UsersController {

    private final UsersService usersService;
    private boolean userIsAuthenticated = false;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping("/register")
    public String getRegisterPage(Model model){
        model.addAttribute("registerRequest", new UsersModel());
        return "register_page";
    }

    @GetMapping("/changePassword")
    public String getChangePasswordPage(Model model){
        model.addAttribute("changePasswordRequest", new UsersModel());
        return "changePassword_page";
    }

    @GetMapping("/feedback")
    public String getFeedbackPage(Model model){
        model.addAttribute("feedbackRequest", new UsersModel());
        if(userIsAuthenticated){
            return "personal_page";
        }
        return "error_page";
    }

    @GetMapping("/login")
    public String getLoginPage(Model model){
        model.addAttribute("loginRequest", new UsersModel());
        return "login_page";
    }

    @GetMapping("/logout")
    public String getLogoutPage(Model model){
        model.addAttribute("logoutRequest", new UsersModel());
        userIsAuthenticated = false;
        return "redirect:/login";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute UsersModel usersModel){
        System.out.println("register request: " + usersModel);
        UsersModel registeredUser = usersService.registerUser(usersModel.getLogin(), usersModel.getPassword(), usersModel.getEmail());
        return registeredUser == null? "error_page" : "redirect:/login";
    }

    @PostMapping("/changePassword")
    public String changePassword(@ModelAttribute UsersModel usersModel){
        System.out.println("change password request: " + usersModel);
        UsersModel changedPasswordUser = usersService.changeUserPassword(usersModel.getLogin(), usersModel.getPassword(), usersModel.getEmail());
        if(changedPasswordUser != null){
            System.out.println("Successfully changed password");
            return "redirect:/login";
        } else{
            return "error_page";
        }
    }

    @PostMapping("/feedback")
    public String feedback(@ModelAttribute UsersModel usersModel){
        System.out.println("feedback submission: " + usersModel);
        UsersModel userFeedback = usersService.submitFeedback(usersModel.getLogin(), usersModel.getFeedback(), usersModel.getEmail());
        if((userFeedback != null) && userIsAuthenticated){
            System.out.println("Successfully submitted feedback");
            return "personal_page";
        }
        return "error_page";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute UsersModel usersModel, Model model){
        System.out.println("login request: " + usersModel);
        UsersModel authenticated = usersService.authenticate(usersModel.getLogin(), usersModel.getPassword());
        if(authenticated != null){
            model.addAttribute("userLogin", authenticated.getLogin());
            userIsAuthenticated = true;
            return "personal_page";
        } else{
            return "error_page";
        }
    }

}
