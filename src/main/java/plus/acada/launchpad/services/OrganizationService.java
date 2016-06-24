package plus.acada.launchpad.services;

import com.stormpath.sdk.directory.CustomData;
import com.stormpath.sdk.directory.Directory;
import org.springframework.stereotype.Component;
import plus.acada.launchpad.models.Organization;

@Component
public class OrganizationService {

    private static final String EMAIL = "email";
    private static final String WEBSITE = "website";
    private static final String SKYPE = "skype";
    private static final String WHATS_APP = "whatsapp";
    private static final String LAND_LINE = "landline";
    private static final String MOBILE = "mobile";
    private static final String FAX = "fax";
    private static final String FACEBOOK = "facebook";
    private static final String TWITTER = "twitter";
    private static final String GOOGLE = "google";
    private static final String TAGLINE = "tagline";
    private static final String STREET = "street";
    private static final String CITY = "city";
    private static final String STATE = "state";
    private static final String LOCALITY = "locality";

    public Organization getMyOrganization(Directory directory) {
        com.stormpath.sdk.organization.Organization org = directory.getOrganizations().iterator().next();
        Organization organization =  new Organization();
        organization.setId(org.getHref());
        organization.setKey(org.getNameKey());
        organization.setName(org.getName());
        organization.setDescription(org.getDescription());
        CustomData data = org.getCustomData();
        organization.setStreet((String) data.get(STREET));
        organization.setCity((String) data.get(CITY));
        organization.setState((String) data.get(STATE));
        organization.setTagline((String)data.get(TAGLINE));
        organization.setEmail((String)data.get(EMAIL));
        organization.setWebsite((String)data.get(WEBSITE));
        organization.setSkype((String)data.get(SKYPE));
        organization.setWhatsapp((String)data.get(WHATS_APP));
        organization.setLandline((String)data.get(LAND_LINE));
        organization.setMobile((String)data.get(MOBILE));
        organization.setFax((String)data.get(FAX));
        organization.setFacebook((String)data.get(FACEBOOK));
        organization.setTwitter((String)data.get(TWITTER));
        organization.setGoogle((String)data.get(GOOGLE));
        organization.setLocality((String)data.get(LOCALITY));
        return organization;
    }

    public void updateMyOrganization(Directory directory, Organization org) {
        com.stormpath.sdk.organization.Organization organization = directory.getOrganizations().iterator().next();
        organization.setDescription(org.getDescription());
        organization.getCustomData().put(TAGLINE, org.getTagline());
        organization.getCustomData().put(EMAIL, org.getEmail());
        organization.getCustomData().put(WEBSITE, org.getWebsite());
        organization.getCustomData().put(SKYPE, org.getSkype());
        organization.getCustomData().put(WHATS_APP, org.getWhatsapp());
        organization.getCustomData().put(LAND_LINE, org.getLandline());
        organization.getCustomData().put(MOBILE, org.getMobile());
        organization.getCustomData().put(FAX, org.getFax());
        organization.getCustomData().put(FACEBOOK, org.getFacebook());
        organization.getCustomData().put(TWITTER, org.getTwitter());
        organization.getCustomData().put(GOOGLE, org.getGoogle());
        organization.getCustomData().put(STREET, org.getStreet());
        organization.getCustomData().put(CITY, org.getCity());
        organization.getCustomData().put(STATE, org.getState());
        organization.getCustomData().put(LOCALITY, org.getLocality());
        organization.save();
    }

}
