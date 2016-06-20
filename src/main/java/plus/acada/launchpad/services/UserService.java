package plus.acada.launchpad.services;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountList;
import com.stormpath.sdk.account.AccountStatus;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.directory.Directory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import plus.acada.launchpad.models.Role;
import plus.acada.launchpad.models.User;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserService {

    private final static String  DEFAULT_PROFILE_ICON = "http://res.cloudinary.com/acadaplus/image/upload/v1466301856/Acada%2B%20Platform/User%20Images/photo.gif";
    private final static String  ICON_META_DATA = "icon";
    private final static String  DEFAULT_PASSWORD = "Password1";

    @Autowired
    private PermissionService permissionService;

    public List<User> getAllUsers(Directory directory) {
        AccountList accounts = directory.getAccounts();
        List<User> users = new ArrayList<User>(accounts.getSize());
        for(Account account : accounts) {
            User user = convertAccount(account);
            users.add(user);
        }
        return users;
    }

    public Account createAccount(Client client, Directory directory, User user) {
        Account account = client.instantiate(Account.class);
        account.setGivenName(user.getFirstName());
        account.setSurname(user.getLastName());
        account.setEmail(user.getEmail());
        account.setStatus(AccountStatus.valueOf(user.getStatus()));
        account.setPassword(DEFAULT_PASSWORD);
        account = directory.createAccount(account);
        account.addGroup(user.getRole().getName());
        account.save();
        return account;
    }

    public Account updateAccount(Client client, User user) {
        Account account =  client.getResource(user.getId(), Account.class);
        setAccountRoleData(account, user);
        account.setGivenName(user.getFirstName());
        account.setSurname(user.getLastName());
        account.setStatus(AccountStatus.valueOf(user.getStatus()));
        account.save();
        return account;
    }

    public User convertAccount(Account account) {
        User user = new User();
        user.setId(account.getHref());
        user.setEmail(account.getEmail());
        user.setStatus(account.getStatus().name());
        user.setName(account.getFullName());
        user.setFirstName(account.getGivenName());
        user.setLastName(account.getSurname());
        setUserRoleData(account, user);
        setUserIconData(account, user);
        return user;
    }

    public void deleteAccount(Client client, User user) {
        Account account = client.getResource(user.getId(), Account.class);
        account.delete();
    }

    public void setAccountRoleData(Account account, User user) {
        Role role = permissionService.getUserRole(account);
        if (user.getRole().getName().equalsIgnoreCase(role.getName()))
        {
            return;
        }
        if (account.isMemberOfGroup(role.getName())) {
            account.removeGroup(role.getName());
        }
        account.addGroup(user.getRole().getName());
    }

    public void setUserRoleData(Account account, User user) {
        user.setRole(permissionService.getUserRole(account));
    }

    public void setUserIconData(Account account, User user) {
        String icon = (String)account.getCustomData().get(ICON_META_DATA);
        if (icon == null || icon.length()==0) {
            icon = DEFAULT_PROFILE_ICON;
        }
        user.setIcon(icon);
    }

}
