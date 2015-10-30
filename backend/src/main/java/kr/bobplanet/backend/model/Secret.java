package kr.bobplanet.backend.model;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * 클라이언트 APK 내에 포함되면 안되는 설정값들을 담는 객체
 *
 * @author heonkyu.jin
 * @version 15. 10. 30
 */
@Entity
public class Secret {
    @Id
    private String name;

    public String facebookAppId;

    public String naverClientId;
    public String naverClientSecret;

    public String cipherKey;

    public Secret() {
    }

    public String getName() {
        return name;
    }

    public String getFacebookAppId() {
        return facebookAppId;
    }

    public void setFacebookAppId(String facebookAppId) {
        this.facebookAppId = facebookAppId;
    }

    public String getNaverClientId() {
        return naverClientId;
    }

    public void setNaverClientId(String naverClientId) {
        this.naverClientId = naverClientId;
    }

    public String getNaverClientSecret() {
        return naverClientSecret;
    }

    public void setNaverClientSecret(String naverClientSecret) {
        this.naverClientSecret = naverClientSecret;
    }
}
