package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;
import ru.kata.spring.boot_security.demo.service.UserServiceImpl;

import java.security.Principal;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private UserServiceImpl userService;
    private RoleService roleService;

    @Autowired
    public AdminController(UserServiceImpl userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/user")
    public String getAllUsers(Model model, Principal principal) {
        model.addAttribute("allUsers", userService.getAllUsers());
        model.addAttribute("roles", roleService.getRoles());
        model.addAttribute("currentUserEmail", principal.getName());
        model.addAttribute("currentUserRoles", userService.findByEmail(principal.getName()).getAuthorities());
        User currentUser = userService.findByEmail(principal.getName());
        model.addAttribute("user", currentUser);
        return "users";
    }

    @GetMapping("/showAddNewUserForm")
    public String showAddNewUserForm(Model model) {
        model.addAttribute("addUser", new User());
        model.addAttribute("roles", roleService.getRoles());
        return "add-user";
    }

    @PostMapping("/addUser")
    public String addUser(@ModelAttribute("addUser") User user) {
        userService.addUser(user);
        return "redirect:/admin/user";
    }

    @GetMapping(value = "/showEditUserForm")
    public String showEditUserForm(@RequestParam("id") Long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        model.addAttribute("roles", roleService.getRoles());
        return "edit-user";
    }

    @PostMapping("/saveEditUser")
    public String saveEditUser(@ModelAttribute("user") User user) {
        userService.updateUser(user);
        return "redirect:/admin/user";
    }

    @GetMapping("/deleteUser")
    public String deleteUser(@RequestParam("id") Long id) {
        User user = userService.getUserById(id);
        userService.deleteUser(user);
        return "redirect:/admin/user";
    }
}