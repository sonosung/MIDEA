package org.astrologist.midea.controller;

import org.astrologist.midea.dto.UserDTO;
import org.astrologist.midea.entity.User;
import org.astrologist.midea.service.UserService;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

@Controller
@RequestMapping("/midea")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("user", new UserDTO()); // 새로운 사용자 DTO 객체를 모델에 추가
        model.addAttribute("formType", "signin"); // 기본 폼 유형을 로그인 폼으로 설정
        return "midea/login"; // login 뷰 반환
    }

    @PostMapping("/signup")
    public String signUpUser(@Valid @ModelAttribute("user") UserDTO userDTO, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return handleFormErrors(model, userDTO, "signup", null);
        }
        try {
            if (userService.isEmailExist(userDTO.getEmail())) {
                return handleFormErrors(model, userDTO, "signup", "이메일이 이미 존재합니다.");
            }

            if (userService.isNicknameExist(userDTO.getNickname())) {
                return handleFormErrors(model, userDTO, "signup", "닉네임이 이미 존재합니다.");
            }

            // UserDTO를 User 엔티티로 변환하여 저장
            User user = userDTO.toEntity(); // toEntity 메서드를 사용하여 DTO를 엔티티로 변환
            userService.saveUser(user);
            return "redirect:/midea/login";
        } catch (Exception e) {
            return handleFormErrors(model, userDTO, "signup", "An error occurred: " + e.getMessage());
        }
    }

    @PostMapping("/signin")
    public String signInUser(@RequestParam String email, @RequestParam String password, Model model) {
        try {
            User user = userService.findByEmail(email);

            if (user == null || !BCrypt.checkpw(password, user.getPassword())) {
                return handleFormErrors(model, new UserDTO(), "signin", "이메일 혹은 패스워드가 일치하지 않습니다.");
            }

            return "redirect:/midea/home"; // 홈 페이지로 리다이렉트
        } catch (Exception e) {
            return handleFormErrors(model, new UserDTO(), "signin", "An error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/check-nickname")
    @ResponseBody
    public boolean checkNickname(@RequestParam String nickname) {
        return userService.isNicknameExist(nickname);
    }

    @GetMapping("/check-email")
    @ResponseBody
    public boolean checkEmail(@RequestParam String email) {
        return userService.isEmailExist(email);
    }

    // 비밀번호 찾기 팝업을 위한 엔드포인트
    @GetMapping("/forgot-password-popup")
    public String showForgotPasswordPopup() {
        return "midea/forgot-password-popup"; // forgot-password-popup 뷰 반환
    }

    // 이메일과 닉네임으로 비밀번호를 재설정하는 엔드포인트
    @PostMapping("/forgot-password")
    @ResponseBody
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String nickname = payload.get("nickname");

        String newPassword = userService.resetPassword(email, nickname);
        if (newPassword != null) {
            // 새로운 비밀번호를 사용자가 확인할 수 있도록 반환 (실제 서비스에서는 이메일로 전송)
            return ResponseEntity.ok(newPassword);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No matching user found.");
        }
    }

    private String handleFormErrors(Model model, UserDTO userDTO, String formType, String errorMessage) {
        model.addAttribute("user", userDTO);
        model.addAttribute("formType", formType);
        if ("signup".equals(formType) && errorMessage != null) {
            model.addAttribute("signupError", errorMessage);
        } else if ("signin".equals(formType) && errorMessage != null) {
            model.addAttribute("signinError", errorMessage);
        }
        return "midea/login";
    }
}
