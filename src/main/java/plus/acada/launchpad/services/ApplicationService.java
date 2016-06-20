package plus.acada.launchpad.services;

import com.stormpath.sdk.account.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import plus.acada.launchpad.models.Application;

import java.util.*;

@Component
public class ApplicationService {

    @Autowired
    private PermissionService permissionService;

    public List<Application> getMyApplications(Account account, String currentApplication) {
        List<Application> applications = new ArrayList<Application>();
        for (com.stormpath.sdk.application.Application app : account.getApplications()) {
            if (!app.getHref().equalsIgnoreCase(currentApplication)) {
                if (permissionService.hasAccess(account, app)) {
                    Application application = new Application();
                    application.setName(app.getName());
                    application.setIcon((String) app.getCustomData().get("icon"));
                    application.setUrl((String) app.getCustomData().get("url"));
                    application.setOrder((Integer) app.getCustomData().get("order"));
                    applications.add(application);
                }
            }
        }
        Collections.sort(applications);
        return applications;
    }

    public List<Application> getAllApplications(Account account, String currentApplication) {
        List<Application> applications = new ArrayList<Application>();
        for (com.stormpath.sdk.application.Application app : account.getApplications()) {
            if (!app.getHref().equalsIgnoreCase(currentApplication)) {
                Application application = new Application();
                application.setName(app.getName());
                application.setIcon((String) app.getCustomData().get("icon"));
                application.setUrl((String) app.getCustomData().get("url"));
                application.setOrder((Integer) app.getCustomData().get("order"));
                applications.add(application);
            }
        }
        Collections.sort(applications);
        return applications;
    }
}
