package plus.acada.launchpad.controllers;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.servlet.account.AccountResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import plus.acada.launchpad.services.PermissionService;

import javax.servlet.http.HttpServletRequest;

@Controller
public class UserController {

    @Autowired
    private PermissionService permissionService;

    @RequestMapping("/users")
    String home(HttpServletRequest request, Model model) {
        Account account = AccountResolver.INSTANCE.getAccount(request);
        model.addAttribute("isSysAdmin", permissionService.isSysAdmin(account));
        model.addAttribute("roles", permissionService.getAllRoles(account));
        return "users";
    }
}
