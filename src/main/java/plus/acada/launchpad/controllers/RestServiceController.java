package plus.acada.launchpad.controllers;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.servlet.account.AccountResolver;
import com.stormpath.sdk.servlet.client.ClientResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import plus.acada.launchpad.models.Permission;
import plus.acada.launchpad.models.User;
import plus.acada.launchpad.services.UserService;
import plus.acada.launchpad.services.PermissionService;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

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

    @RequestMapping(value="/profile/image", method=RequestMethod.POST)
    public @ResponseBody String updateUserIcon(HttpServletRequest request, @ModelAttribute MultipartFile file) throws Exception {

        Account account = AccountResolver.INSTANCE.getAccount(request);

        File icon = new File(file.getOriginalFilename());
        boolean created = icon.createNewFile();
        FileOutputStream fos = new FileOutputStream(icon);
        fos.write(file.getBytes());
        fos.close();

        Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "acadaplus",
                "api_key", "761728957678469",
                "api_secret", "bVRWtBpa-cPTtcsE1tzt_0Jgs8o"));
        Map options = ObjectUtils.asMap(
                "transformation", new Transformation().width(128).height(128).crop("fill"),
                "public_id", "Acada+ Platform/User Images/"+account.getDirectory().getName()+"/"+account.getEmail());
        Map result = cloudinary.uploader().upload(icon, options);

        String url = (String) result.get("url");
        userService.updateUserIcon(account, url);
        return "{\"url\":\""+url+"\"}";

    }




}
