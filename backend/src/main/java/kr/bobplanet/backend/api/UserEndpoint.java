package kr.bobplanet.backend.api;

import com.google.api.server.spi.config.ApiClass;
import com.google.api.server.spi.config.ApiMethod;
import com.googlecode.objectify.Key;

import java.util.List;

import kr.bobplanet.backend.model.User;

import static kr.bobplanet.backend.api.ObjectifyRegister.ofy;

/**
 * Google Cloud Endpoint를 이용해 서버사이드 API로 가공되는 클래스.
 * <p/>
 * - 모든 데이터는 Google DataStore를 이용하며, 객체접근 위해 Objectify 라이브러리 이용
 * - 데이터 노출을 막기 위해 API는 클라이언트ID 기반 권한관리 (따라서, 신규 클라이언트가 추가되면 여기도 수정해줘야 함)
 *
 * @author heonkyu.jin
 * @version 2015. 10. 17
 */
@ApiClass(
        resource = "user"
)
public class UserEndpoint extends BaseEndpoint {

    @ApiMethod(
            name = "registerUser",
            path = "user/register",
            httpMethod = "POST"
    )
    public User registerUser(User user) {
        logger.info("registerUser(): user = " + user.toString());

        Key<User> userKey = ofy().save().entity(user).now();
        logger.info("userKey = " + userKey);
        user.setId(userKey.getId());

        return user;
    }

    @ApiMethod(
            name = "updateUser",
            path = "user/update",
            httpMethod = "POST"
    )
    public void updateUser(User user) {
        logger.info("updateUser(): user = " + user.toString());

        ofy().save().entity(user).now();
    }
}