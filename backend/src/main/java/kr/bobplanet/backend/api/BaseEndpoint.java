package kr.bobplanet.backend.api;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiNamespace;

import java.util.logging.Logger;

import kr.bobplanet.backend.Constants;

/**
 * Google Cloud Endpoint를 이용해 서버사이드 API로 가공되는 클래스.
 * <p/>
 * - 모든 데이터는 Google DataStore를 이용하며, 객체접근 위해 Objectify 라이브러리 이용
 * - 데이터 노출을 막기 위해 API는 클라이언트ID 기반 권한관리 (따라서, 신규 클라이언트가 추가되면 여기도 수정해줘야 함)
 *
 * @author heonkyu.jin
 * @version 2015. 10. 17
 */
@Api(
        name = "bobplanetApi",
        version = "v1",
        title = "Bobplanet Server API",
        description = "Bobplanet 프로젝트에서 사용되는 메뉴조회, 평가, GCM메시지 전송 등의 API들을 제공합니다.",
        namespace = @ApiNamespace(
                ownerDomain = Constants.API_OWNER,
                ownerName = Constants.API_OWNER,
                packagePath = ""
        ),
        scopes = {
                Constants.EMAIL_SCOPE
        },
        clientIds = {
                Constants.CLIENTID_ANDROID_DEV,
                Constants.CLIENTID_ANDROID_RELEASE,
                Constants.CLIENTID_WEB
        },
        audiences = {
                Constants.ANDROID_AUDIENCE
        }
)
abstract public class BaseEndpoint {
    protected Logger logger = Logger.getLogger(BaseEndpoint.class.getName());
}