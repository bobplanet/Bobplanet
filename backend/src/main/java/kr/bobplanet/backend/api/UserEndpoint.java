package kr.bobplanet.backend.api;

import com.google.api.server.spi.config.ApiClass;
import com.google.api.server.spi.config.ApiMethod;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.VoidWork;
import com.googlecode.objectify.Work;

import java.util.UUID;

import kr.bobplanet.backend.model.UserDevice;
import kr.bobplanet.backend.model.User;

import static kr.bobplanet.backend.api.ObjectifyRegister.ofy;

/**
 * 사용자 및 사용자의 기기를 관리하는 Endpoint.
 * <p>
 * -
 * - 데이터 노출을 막기 위해 API는 클라이언트ID 기반 권한관리 (따라서, 신규 클라이언트가 추가되면 여기도 수정해줘야 함)
 *
 * @author heonkyu.jin
 * @version 2015. 10. 17
 */
@ApiClass(
        resource = "user"
)
public class UserEndpoint extends BaseEndpoint {

    /**
     * 사용자의 기기를 등록. 이미 등록된 기기(AndroidId 기준)인 경우에는 업데이트함.
     *
     * @param device
     * @return
     */
    @ApiMethod(
            name = "registerDevice",
            path = "device/register",
            httpMethod = "POST"
    )
    public UserDevice registerDevice(final UserDevice device) {
        logger.info("registerDevice(): device = " + device.toString());

        final UserDevice existing = ofy().load().type(UserDevice.class).
                filter("androidId", device.getAndroidId()).first().now();

        if (existing != null) {
            device.setId(existing.getId());
            device.setUser(existing.getUser());

            ofy().save().entity(device).now();
        } else {
            device.setId(UUID.randomUUID().toString());
            User user = new User();
            user.setId(UUID.randomUUID().toString());
            device.setUser(user);

            ofy().save().entities(device, user).now();
        }

        return device;
    }

    /**
     * 기기정보 업데이트.
     * GCM토큰 변경 등의 케이스에 호출됨.
     *
     * @param device
     */
    @ApiMethod(
            name = "updateDevice",
            path = "device/update",
            httpMethod = "POST"
    )
    public void updateDevice(UserDevice device) {
        logger.info("updateDevice(): device = " + device.toString());

        ofy().save().entity(device).now();
    }

    /**
     * 사용자정보 업데이트.
     * 사용자가 Google 로그인 등 account 정보를 등록했을 때 호출.
     * 이미 존재하는 Google 계정인 경우 해당 계정으로 통합하고 결과를 반환.
     *
     * @param user
     */
    @ApiMethod(
            name = "setUserAccount",
            path = "user/account",
            httpMethod = "POST"
    )
    public UserDevice setUserAccount(final User user) {
        logger.info("setUserAccount(): user = " + user.toString());

        // 이미 존재하는 계정인지 체크
        final User existingUser = ofy().load().type(User.class)
                .filter("accountType", user.getAccountType())
                .filter("accountId", user.getAccountId())
                .first().now();

        final UserDevice currentDevice = ofy().load().type(UserDevice.class).ancestor(user).first().now();

        // 기존 유저와 신규 유저 병합
        if (existingUser != null) {
            return ofy().transact(new Work<UserDevice>() {
                @Override
                public UserDevice run() {
                    ofy().delete().entities(user, currentDevice).now();
                    currentDevice.setUser(existingUser);
                    ofy().save().entity(currentDevice).now();

                    return currentDevice;
                }
            });
        } else {
            ofy().save().entity(user).now();
            return currentDevice;
        }

    }
}