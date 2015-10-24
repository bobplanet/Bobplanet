package kr.bobplanet.backend.api;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.google.api.server.spi.config.ApiClass;
import com.google.api.server.spi.config.ApiMethod;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.util.List;

import kr.bobplanet.backend.Constants;
import kr.bobplanet.backend.model.BaseMessage;
import kr.bobplanet.backend.model.UserDevice;
import kr.bobplanet.backend.model.Menu;
import kr.bobplanet.backend.model.NextMenuMessage;

import static kr.bobplanet.backend.api.ObjectifyRegister.ofy;

/**
 * 푸시메시지 발송용 API를 제공하는 Endpoint.
 * timezone에 기반한 날짜 계산을 위해 jodatime을 사용함.
 *
 * @author heonkyu.jin
 * @version 2015. 10. 22
 */
@ApiClass(
        resource = "message"
)
public class MessageEndpoint extends BaseEndpoint {
	/**
	 * "2015-10-23" 형태의 문자열을 만들어주는 포매터.
	 */
    private static final DateTimeFormatter DATETIME_FORMAT_YMD = DateTimeFormat.forPattern("yyyy-MM-dd");

    private static final String TO = "dfWaXIfP7bA:APA91bEqHJNEhl3gBTaBzIpfyePnRN5wKXr2z6KJBBPxoDhHhDqoDlwDGCl6Pv0E6REn0jluaGxQ-A-lgSUuacck1KIGkkeYWfL832IZqSWOcyYSNp-cWNljtyH2u1U1L_qmWgx8kczy";

    /**
     * 다음 메뉴 알림 메시지 발송. cron에 의해 호출됨.
     */
    @ApiMethod(
            name = "sendNextMenuMessage",
            path = "message/send/nextMenu",
            httpMethod = "GET"
    )
    public void sendNextMenuMessage() {
        logger.info("Executing sendNextMenuMessage()");

        LocalDateTime now = new LocalDateTime(DateTimeZone.forOffsetHours(9));

        String today = DATETIME_FORMAT_YMD.print(now);

        int hour = now.getHourOfDay();
        String when = hour < 12 ? "점심" : "저녁";

        List<Menu> menuList = ofy().load().type(Menu.class).filter("date", today).filter("when", when).list();
        if (menuList.size() > 0) {
            NextMenuMessage message = NextMenuMessage.fromMenuList(menuList);
            message.setTitle("오늘의 " + when + " 메뉴 알림");
            sendMessage(message);
        }
    }

    /**
     * 메시지 발송용 범용 method. API로는 공개되지 않음.
     */
    private void sendMessage(BaseMessage message) {
        logger.info("Executing sendMessage() : message = " + message);

        Sender sender = new Sender(Constants.GCM_API_KEY);
        Message msg = new Message.Builder()
                .addData("type", message.getType())
                .addData("title", message.getTitle())
                .addData("menuId", message.getExtra("menuId"))
                .addData("message", "오늘은 어떤 메뉴가 나올까요?")
                .build();
        try {
            List<UserDevice> devices = ofy().load().type(UserDevice.class)
                    .filter("gcmToken !=", null).filter("gcmEnabled", true).list();

            int errors = 0;
            for (UserDevice device : devices) {
                Result result = sender.send(msg, device.getGcmToken(), 5);
                if (result.getErrorCodeName() != null) {
                    errors++;
                }
            }

            message.setNumRecipients(devices.size());
            message.setNumErrors(errors);

            ofy().save().entity(message);
        } catch (IOException e) {
            logger.info(e.toString());
        }
    }

}