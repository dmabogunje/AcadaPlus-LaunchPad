package plus.acada.launchpad.controllers;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.servlet.account.AccountResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import plus.acada.launchpad.models.Organization;
import plus.acada.launchpad.services.OrganizationService;
import plus.acada.launchpad.services.PermissionService;

import javax.servlet.http.HttpServletRequest;

@Controller
public class OrganizationController {

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private OrganizationService organizationService;

    @RequestMapping(value="/organization", method= RequestMethod.GET)
    String loadOrganizationPage(HttpServletRequest request, Model model) {
        Account account = AccountResolver.INSTANCE.getAccount(request);
        model.addAttribute("organization", organizationService.getMyOrganization(account.getDirectory()));
        model.addAttribute("isSysAdmin", permissionService.isSysAdmin(account));
        return "organization";
    }

    @RequestMapping(value="/organization", method= RequestMethod.POST)
    String updateOrganization(HttpServletRequest request, Model model, @ModelAttribute Organization organization) {
        Account account = AccountResolver.INSTANCE.getAccount(request);
        organizationService.updateMyOrganization(account.getDirectory(), organization);
        model.addAttribute("organization", organizationService.getMyOrganization(account.getDirectory()));
        model.addAttribute("isSysAdmin", permissionService.isSysAdmin(account));
        return "organization";
    }
}
