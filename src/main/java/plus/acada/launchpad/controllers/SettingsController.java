package plus.acada.launchpad.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class SettingsController {

    @RequestMapping("/settings")
    String loadSettingsPage(HttpServletRequest request, Model model) {
        return "settings";
    }
}
