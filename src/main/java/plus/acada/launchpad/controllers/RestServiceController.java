package plus.acada.launchpad.controllers;

import com.stormpath.sdk.servlet.account.AccountResolver;
import com.stormpath.sdk.servlet.client.ClientResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import plus.acada.launchpad.models.Permission;
import plus.acada.launchpad.models.User;
import plus.acada.launchpad.services.UserService;
import plus.acada.launchpad.services.PermissionService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/rest")
public class RestServiceController {

    @Autowired
    private UserService userService;

    @Autowired
    private PermissionService permissionService;

    @RequestMapping(value="/permission", method= RequestMethod.POST, headers = {"Content-type=application/json"})
    public void updatePermission(HttpServletRequest request, @RequestBody Permission permission) {
        permissionService.updateGroupPermission(AccountResolver.INSTANCE.getAccount(request).getDirectory(), permission);
    }

    @RequestMapping(value="/users", method=RequestMethod.GET)
    @ResponseBody
    public List<User> getUsers(HttpServletRequest request) {
        return userService.getAllUsers(AccountResolver.INSTANCE.getAccount(request).getDirectory());
    }

    @RequestMapping(value="/users", method=RequestMethod.POST)
    public @ResponseBody User addUser(HttpServletRequest request, @RequestBody User user) {
        return userService.convertAccount(userService.createAccount(
                ClientResolver.INSTANCE.getClient(request),
                AccountResolver.INSTANCE.getAccount(request).getDirectory(),
                user));
    }

    @RequestMapping(value="/user", method= RequestMethod.DELETE)
    public void deleteUser(HttpServletRequest request, @RequestBody User user) {
        userService.deleteAccount(ClientResolver.INSTANCE.getClient(request), user);
    }

    @RequestMapping(value="/user", method=RequestMethod.POST)
    public @ResponseBody User updateUser(HttpServletRequest request, @RequestBody User user) {
        return userService.convertAccount(userService.updateAccount(ClientResolver.INSTANCE.getClient(request), user));
    }

}
