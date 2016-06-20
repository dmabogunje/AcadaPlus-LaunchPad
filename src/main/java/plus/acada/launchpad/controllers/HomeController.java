package plus.acada.launchpad.controllers;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.servlet.account.AccountResolver;
import com.stormpath.sdk.servlet.application.ApplicationResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import plus.acada.launchpad.services.ApplicationService;
import plus.acada.launchpad.services.PermissionService;

import javax.servlet.http.HttpServletRequest;

@Controller
class HomeController {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private PermissionService permissionService;

    @RequestMapping("/")
    String home(HttpServletRequest request, Model model) {
        Account account = AccountResolver.INSTANCE.getAccount(request);
        com.stormpath.sdk.application.Application currentApplication = ApplicationResolver.INSTANCE.getApplication(request);
        model.addAttribute("applications", applicationService.getMyApplications(account, currentApplication.getHref()));
        model.addAttribute("isSysAdmin", permissionService.isSysAdmin(account));
        return "home";
    }
}
