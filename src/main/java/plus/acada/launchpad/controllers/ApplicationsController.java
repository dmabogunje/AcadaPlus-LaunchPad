package plus.acada.launchpad.controllers;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.servlet.account.AccountResolver;
import com.stormpath.sdk.servlet.application.ApplicationResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import plus.acada.launchpad.models.Application;
import plus.acada.launchpad.services.ApplicationService;
import plus.acada.launchpad.services.PermissionService;
import plus.acada.launchpad.services.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class ApplicationsController {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private UserService userService;

    @RequestMapping("/applications")
    String loadApplicationsPage(HttpServletRequest request, Model model) {

        Account account = AccountResolver.INSTANCE.getAccount(request);
        com.stormpath.sdk.application.Application currentApplication = ApplicationResolver.INSTANCE.getApplication(request);
        List<Application> applications = applicationService.getAllApplications(account, currentApplication.getHref());
        model.addAttribute("applications", applications);
        model.addAttribute("rolePermissions", permissionService.getRolePermissions(account, applications));
        model.addAttribute("isSysAdmin", permissionService.isSysAdmin(account));
        model.addAttribute("userProfileIcon", userService.convertAccount(account).getIcon());
        return "applications";

    }
}
