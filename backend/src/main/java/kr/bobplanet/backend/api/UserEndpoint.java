package kr.bobplanet.backend.api;

import com.google.api.server.spi.config.ApiClass;
import com.google.api.server.spi.config.ApiMethod;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Work;

import kr.bobplanet.backend.model.UserDevice;
import kr.bobplanet.backend.model.User;

import static kr.bobplanet.backend.api.ObjectifyRegister.ofy;

/**
 * 사용자 및 사용자의 기기를 관리하는 Endpoint.
 * <p/>
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

        final UserDevice existing =
                ofy().load().type(UserDevice.class).filter("androidId", device.getAndroidId()).first().now();
        if (existing != null) {
            logger.info("existing = " + existing);
            device.setId(existing.getId());
            device.setUser(existing.getUser());
        }

        UserDevice result = ofy().transact(
                new Work<UserDevice>() {
                    @Override
                    public UserDevice run() {
                        if (device.getUser() == null) {
                            User user = new User();
                            Key<User> userId = ofy().save().entity(user).now();
                            user.setId(userId.getId());
                            device.setUser(user);
                        }

                        Key <UserDevice> deviceKey = ofy().save().entity(device).now();
                        device.setId(deviceKey.getId());

                        return device;
                    }
                }
        );

        return result;
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
     * 
     * @param user
     */
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