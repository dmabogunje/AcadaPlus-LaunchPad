package plus.acada.launchpad.controllers;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.servlet.account.AccountResolver;
import com.stormpath.sdk.servlet.client.ClientResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.*;
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
        model.addAttribute("userProfileIcon", userService.convertAccount(account).getIcon());
        return "users";
    }

    @RequestMapping(value="/profile", method= RequestMethod.GET)
    String loadProfilePage(HttpServletRequest request, Model model) {
        Account account = AccountResolver.INSTANCE.getAccount(request);
        model.addAttribute("isSysAdmin", permissionService.isSysAdmin(account));
        model.addAttribute("organizationName", account.getDirectory().getOrganizations().single().getName());
        User user = userService.convertAccount(account);
        model.addAttribute("user", user);
        model.addAttribute("userProfileIcon", user.getIcon());
        model.addAttribute("userPreview", false);
        return "profile";
    }

    @RequestMapping(value="/profile/{userId}", method= RequestMethod.GET)
    String getProfilePage(HttpServletRequest request, Model model, @PathVariable String userId) {
        Account account = AccountResolver.INSTANCE.getAccount(request);
        model.addAttribute("isSysAdmin", permissionService.isSysAdmin(account));
        model.addAttribute("organizationName", account.getDirectory().getOrganizations().single().getName());
        model.addAttribute("userProfileIcon", userService.convertAccount(account).getIcon());

        Account viewAccount = ClientResolver.INSTANCE.getClient(request).getResource(userService.decodeUserId(userId), Account.class);
        User viewUser = userService.convertAccount(viewAccount);
        model.addAttribute("user", viewUser);
        model.addAttribute("userPreview", !account.getHref().equalsIgnoreCase(viewAccount.getHref()));
        return "profile";
    }

    @RequestMapping(value="/profile", method= RequestMethod.POST)
    String updateProfilePage(HttpServletRequest request, Model model, @ModelAttribute User user) {
        Account account = AccountResolver.INSTANCE.getAccount(request);
        model.addAttribute("isSysAdmin", permissionService.isSysAdmin(account));
        model.addAttribute("organizationName", account.getDirectory().getOrganizations().single().getName());
        userService.updateAccountProfile(ClientResolver.INSTANCE.getClient(request), user);
        user = userService.convertAccount(account);
        model.addAttribute("user", user);
        model.addAttribute("userProfileIcon", user.getIcon());
        return "profile";
    }
}
