package kr.bobplanet.backend.api;

import com.google.api.server.spi.config.ApiClass;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.Named;
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
            ofy().save().entities(device).now();
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
     * 사용자 등록. 사용자가 Google 로그인 등 account 정보를 등록했을 때 호출.
     *
     * @param deviceId
     * @param user
     */
    @ApiMethod(
            name = "registerUser",
            path = "user/register",
            httpMethod = "POST"
    )
    public UserDevice registerUser(@Named("deviceId") final String deviceId, final User user) {
        logger.info(String.format("registerUser(): deviceId = %s, user = %s", deviceId, user));

        // 이미 존재하는 계정?
        final User existingUser = ofy().load().type(User.class)
                .filter("accountType", user.getAccountType())
                .filter("accountId", user.getAccountId())
                .first().now();

        return ofy().transact(new Work<UserDevice>() {
            @Override
            public UserDevice run() {
                UserDevice device = new UserDevice(deviceId);
                ofy().delete().entity(device).now();

                // 디바이스도 기존 유저를 기준으로 업데이트
                if (existingUser != null) {
                    device.setUser(existingUser);
                    ofy().save().entity(device);
                } else {
                    device.setUser(user);
                    ofy().save().entities(device, user).now();
                }

                return device;
            }
        });
    }


    /**
     * 사용자 탈퇴. 사용자가 'logout'를 선택할 경우 호출
     *
     * @param deviceId
     * @param user
     */
    @ApiMethod(
            name = "unregisterUser",
            path = "user/unregister",
            httpMethod = "POST"
    )
    public UserDevice unregisterUser(@Named("deviceId") final String deviceId, final User user) {
        logger.info(String.format("unregisterUser(): deviceId = %s, user = %s", deviceId, user));

        return ofy().transact(new Work<UserDevice>() {
            @Override
            public UserDevice run() {
                UserDevice device = new UserDevice(deviceId);
                ofy().delete().entities(device, user).now();

                device.setUser(null);
                ofy().save().entity(device).now();

                return device;
            }
        });
    }
}