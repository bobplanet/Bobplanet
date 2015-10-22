package kr.bobplanet.backend.api;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiClass;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.repackaged.org.codehaus.jackson.map.deser.ValueInstantiators;

import java.util.logging.Logger;

import kr.bobplanet.backend.Constants;
import kr.bobplanet.backend.model.StringHolder;

//import com.google.appengine.api.users.User;

/**
 * Google Cloud Endpoint를 이용해 서버사이드 API로 가공되는 클래스.
 *
 * - 모든 데이터는 Google DataStore를 이용하며, 객체접근 위해 Objectify 라이브러리 이용
 * - 데이터 노출을 막기 위해 API는 클라이언트ID 기반 권한관리 (따라서, 신규 클라이언트가 추가되면 여기도 수정해줘야 함)
 *
 * @author heonkyu.jin
 * @version 2015. 10. 17
 */
@ApiClass(
        resource = "misc"
)
public class MiscEndpoint extends BaseEndpoint {
    /**
     * API가 동작하는지 간단히 확인하기 위한 Hello world API
     *
     */
    @ApiMethod(
            name = "helloworld",
            httpMethod = "GET"
    )
    public StringHolder helloworld() {
        return new StringHolder("Hello, world!");
    }
}