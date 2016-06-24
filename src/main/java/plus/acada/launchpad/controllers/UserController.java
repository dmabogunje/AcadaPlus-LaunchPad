package plus.acada.launchpad.controllers;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.servlet.account.AccountResolver;
import com.stormpath.sdk.servlet.client.ClientResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import plus.acada.launchpad.models.User;
import plus.acada.launchpad.services.PermissionService;
import plus.acada.launchpad.services.UserService;

import javax.servlet.http.HttpServletRequest;

@Controller
public class UserController {

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private UserService userService;

    @RequestMapping(value="/users", method= RequestMethod.GET)
    String loadUserPage(HttpServletRequest request, Model model) {
        Account account = AccountResolver.INSTANCE.getAccount(request);
        model.addAttribute("isSysAdmin", permissionService.isSysAdmin(account));
        model.addAttribute("roles", permissionService.getAllRoles(account));
        return "users";
    }

    @RequestMapping(value="/profile", method= RequestMethod.GET)
    String loadProfilePage(HttpServletRequest request, Model model) {
        Account account = AccountResolver.INSTANCE.getAccount(request);
        model.addAttribute("isSysAdmin", permissionService.isSysAdmin(account));
        model.addAttribute("organizationName", account.getDirectory().getOrganizations().single().getName());
        model.addAttribute("user", userService.convertAccount(account));
        return "profile";
    }

    @RequestMapping(value="/profile", method= RequestMethod.POST)
    String updateProfilePage(HttpServletRequest request, Model model, @ModelAttribute User user) {
        Account account = AccountResolver.INSTANCE.getAccount(request);
        model.addAttribute("isSysAdmin", permissionService.isSysAdmin(account));
        model.addAttribute("organizationName", account.getDirectory().getOrganizations().single().getName());
        userService.updateAccountProfile(ClientResolver.INSTANCE.getClient(request), user);
        model.addAttribute("user", userService.convertAccount(account));
        return "profile";
    }
}
