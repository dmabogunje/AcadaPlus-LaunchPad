package plus.acada.launchpad.services;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountList;
import com.stormpath.sdk.account.AccountStatus;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.directory.CustomData;
import com.stormpath.sdk.directory.Directory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import plus.acada.launchpad.models.Role;
import plus.acada.launchpad.models.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class UserService {

    private final static String  DEFAULT_PROFILE_ICON = "http://res.cloudinary.com/acadaplus/image/upload/v1466301856/Acada%2B%20Platform/User%20Images/photo.gif";
    private final static String  ICON_META_DATA = "icon";
    private final static String  DEFAULT_PASSWORD = "Password1";
    public static final String STATE = "state";
    public static final String CITY = "city";
    public static final String STREET = "street";
    public static final String FACEBOOK = "facebook";
    public static final String FAX = "fax";
    public static final String GENDER = "gender";
    public static final String GOOGLE = "google";
    public static final String LANDLINE = "landline";
    public static final String MOBILE = "mobile";
    public static final String TWITTER = "twitter";
    public static final String SKYPE = "skype";
    public static final String WEBSITE = "website";
    public static final String WHATSAPP = "whatsapp";
    public static final String DOB = "dob";

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
        Account account =  client.getResource(decodeUserId(user.getId()), Account.class);
        setAccountRoleData(account, user);
        account.setGivenName(user.getFirstName());
        account.setSurname(user.getLastName());
        account.setStatus(AccountStatus.valueOf(user.getStatus()));
        account.save();
        return account;
    }

    public Account updateAccountProfile(Client client, User user) {
        Account account =  client.getResource(decodeUserId(user.getId()), Account.class);
        account.setGivenName(user.getFirstName());
        account.setMiddleName(user.getMiddleName());
        account.setSurname(user.getLastName());
        account.getCustomData().put(GENDER, user.getGender());
        account.getCustomData().put(DOB, user.getDob());
        account.getCustomData().put(STREET, user.getStreet());
        account.getCustomData().put(CITY, user.getCity());
        account.getCustomData().put(STATE, user.getState());
        account.getCustomData().put(FACEBOOK, user.getFacebook());
        account.getCustomData().put(TWITTER, user.getTwitter());
        account.getCustomData().put(GOOGLE, user.getGoogle());
        account.getCustomData().put(LANDLINE, user.getLandline());
        account.getCustomData().put(MOBILE, user.getMobile());
        account.getCustomData().put(FAX, user.getFax());
        account.getCustomData().put(WEBSITE, user.getWebsite());
        account.getCustomData().put(SKYPE, user.getSkype());
        account.getCustomData().put(WHATSAPP, user.getWhatsapp());
        account.save();
        return account;
    }

    public User convertAccount(Account account) {
        User user = new User();
        user.setId(encodeAccountHref(account.getHref()));
        user.setEmail(account.getEmail());
        user.setStatus(account.getStatus().name());
        user.setName(account.getFullName());
        user.setFirstName(account.getGivenName());
        user.setLastName(account.getSurname());
        user.setMiddleName(account.getMiddleName());
        user.setCreated(formatDate(account.getCreatedAt()));
        user.setModified(formatDate(account.getModifiedAt()));
        setUserRoleData(account, user);
        setUserIconData(account, user);
        setCustomData(user, account.getCustomData());
        return user;
    }

    private void setCustomData(User user, CustomData customData) {
        user.setState((String) customData.get(STATE));
        user.setCity((String) customData.get(CITY));
        user.setStreet((String) customData.get(STREET));
        user.setFacebook((String) customData.get(FACEBOOK));
        user.setFax((String) customData.get(FAX));
        user.setGender((String) customData.get(GENDER));
        user.setGoogle((String) customData.get(GOOGLE));
        user.setLandline((String) customData.get(LANDLINE));
        user.setMobile((String) customData.get(MOBILE));
        user.setTwitter((String) customData.get(TWITTER));
        user.setSkype((String) customData.get(SKYPE));
        user.setWebsite((String) customData.get(WEBSITE));
        user.setWhatsapp((String) customData.get(WHATSAPP));
        user.setDob((String)customData.get(DOB));
    }

    public void deleteAccount(Client client, User user) {
        Account account = client.getResource(decodeUserId(user.getId()), Account.class);
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

    public void updateUserIcon(Account account, String icon) {
        account.getCustomData().put(ICON_META_DATA, icon);
        account.save();
    }

    private String formatDate(Date date) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy");
            return formatter.format(date);
        }
        catch (Exception e) {
            return "";
        }
    }

    public String decodeUserId(String userId) {
        return new String(Base64Utils.decodeFromUrlSafeString(userId));
    }

    private String encodeAccountHref(String href) {
        return Base64Utils.encodeToUrlSafeString(href.getBytes());
    }

}
