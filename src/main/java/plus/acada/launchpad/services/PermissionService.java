package plus.acada.launchpad.services;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.directory.Directory;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.group.GroupList;
import com.stormpath.sdk.group.Groups;
import org.springframework.stereotype.Component;
import plus.acada.launchpad.models.Application;
import plus.acada.launchpad.models.Permission;
import plus.acada.launchpad.models.Role;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class PermissionService {

    public final static String SYS_ADMIN_ROLE = "role.sysadmin";
    public final static String ROLE_PREFIX = "role.";

    public boolean isSysAdmin(Account account) {
        GroupList groups = account.getGroups(Groups.where(Groups.name().eqIgnoreCase(SYS_ADMIN_ROLE)));
        return groups.getSize() > 0;
    }

    public void updateGroupPermission(Directory directory, Permission permission) {
        GroupList groupList = directory.getGroups(Groups.where(Groups.name().eqIgnoreCase(permission.getRoleName())));
        Group group = groupList.iterator().next();
        group.getCustomData().put(permission.getApplicationName(), permission.hasPermission());
        group.save();
    }

    public Map<Role, List<Boolean>> getRolePermissions(Account account, List<Application> applications) {
        GroupList groups = account.getDirectory().getGroups(Groups.where(Groups.name().startsWithIgnoreCase(ROLE_PREFIX)).orderByName());
        Map<Role, List<Boolean>> rolePermissions = new LinkedHashMap<Role, List<Boolean>>(groups.getSize());
        for (Group group : groups) {
            Role role = new Role();
            role.setName(group.getName());
            role.setDescription(group.getDescription());
            List<Boolean> permissions = new ArrayList<Boolean>();
            for (Application application : applications) {
                Object permission = group.getCustomData().get(application.getName());
                if (permission != null) {
                    permissions.add((Boolean)permission);
                }
                else {
                    permissions.add(Boolean.FALSE);
                }
            }
            rolePermissions.put(role, permissions);
        }
        return rolePermissions;
    }

    public List<Role> getAllRoles(Account account) {
        GroupList groups = account.getDirectory().getGroups(Groups.where(Groups.name().startsWithIgnoreCase(ROLE_PREFIX)).orderByName());
        List<Role> roles = new ArrayList<Role>(groups.getSize());
        for (Group group : groups) {
            Role role = new Role();
            role.setName(group.getName());
            role.setDescription(group.getDescription());
            roles.add(role);
        }
        return roles;
    }

    public Role getUserRole(Account account) {
        Role role = new Role();
        GroupList groups = account.getGroups(Groups.where(Groups.name().startsWithIgnoreCase(ROLE_PREFIX)));
        if (groups.getSize() == 1)
        {
            Group group = groups.single();
            role.setName(group.getName());
            role.setDescription(group.getDescription());
        }
        return role;
    }

    public boolean hasAccess(Account account, com.stormpath.sdk.application.Application application) {
        GroupList groups = account.getGroups(Groups.where(Groups.name().startsWithIgnoreCase(ROLE_PREFIX)));
        if (groups.getSize() == 1)
        {
            Group group = groups.single();
            return (Boolean) group.getCustomData().get(application.getName());
        }
        return false;
    }
}
