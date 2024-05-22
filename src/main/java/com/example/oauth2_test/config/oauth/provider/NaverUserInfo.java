package com.example.oauth2_test.config.oauth.provider;

import java.util.Map;

/*
{resultcode=00, message=success,
response={id=Yp7lh3NYMfe8_wwSsyoBw1PSNP4CVGa2jjpCWQVaWbk,
            nickname=박준철,
            profile_image=https://ssl.pstatic.net/static/pwe/address/img_profile.png,
            email=skyjoon2@naver.com,
            name=박준철}}
 */
public class NaverUserInfo implements OAuth2UserInfo{

    private Map<String, Object> attributes; //oauth2User.getAttributes()

    public NaverUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        return (String)attributes.get("id");
    }

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getEmail() {
        return (String)attributes.get("email");
    }

    @Override
    public String getName() {
        return (String)attributes.get("name");
    }

    @Override
    public String getUserImgUrl() {
        return (String)attributes.get("profile_image");
    }
}
