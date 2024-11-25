package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
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

    @GetMapping
    public String getAllUsers(Model model, Principal principal) {
        model.addAttribute("allUsers", userService.getAllUsers());
        model.addAttribute("roles", roleService.getRoles());
        model.addAttribute("currentUserEmail", principal.getName());
        model.addAttribute("currentUserRoles", userService.findByEmail(principal.getName()).getAuthorities());
        User currentUser = userService.findByEmail(principal.getName());
        model.addAttribute("user", currentUser);
        return "users";
    }

    @GetMapping("/user")
    public String getUserById(@RequestParam(value = "id") Long id, Model model) {
        model.addAttribute("user", userService.getUserById(id));
        return "user";
    }

    @GetMapping("/addNewUser")
    public String addNewUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", roleService.getRoles());
        return "add";
    }

    @PostMapping("/addNewUser")
    public String addNewUser(@ModelAttribute("user") User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", roleService.getRoles());
            return "add"; // Возвращаемся к форме, если есть ошибки
        }
        try {
            userService.addUser(user);
        } catch (DataIntegrityViolationException e) {
            bindingResult.rejectValue("email", "error.user", "Учетная запись для этого Email уже существует.");
            model.addAttribute("roles", roleService.getRoles());
            return "add"; // Возвращаемся к форме, если есть ошибка уникальности
        }
        return "redirect:/admin";
    }

    @GetMapping("/editUser")
    public String editUserForm(@RequestParam("id") Long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        model.addAttribute("roles", roleService.getRoles());
        return "edit";
    }

    @PostMapping("/edit")
    public String editeUser(@ModelAttribute("user") User user, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", roleService.getRoles());
            return "edit";
        }
        try {
            userService.updateUser(user);
        } catch (DataIntegrityViolationException e) {
            bindingResult.rejectValue("email", "error.user", "Учетная запись для Email уже существует.");
            model.addAttribute("roles", roleService.getRoles());
            return "edit";
        }
        redirectAttributes.addFlashAttribute("success", "User updated successfully!");
        return "redirect:/admin";
    }

    @GetMapping("/delete")
    public String deleteUser(@RequestParam("id") Long id) {
        userService.deleteUser(userService.getUserById(id));
        return "redirect:/admin";
    }

    @GetMapping("/users")
    public String getAllUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "users";
    }
}