package kr.bobplanet.backend.api;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiClass;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;

import kr.bobplanet.backend.BackendConstants;
import kr.bobplanet.backend.model.Daily;
import kr.bobplanet.backend.model.Menu;

import static com.googlecode.objectify.ObjectifyService.ofy;

@Api(
        name = "bobplanetApi",
        version = "v1",
        description = "Bobplanet Server API",
        namespace = @ApiNamespace(
                ownerDomain = BackendConstants.API_OWNER,
                ownerName = BackendConstants.API_OWNER,
                packagePath = ""
        ),
        scopes = {
                BackendConstants.EMAIL_SCOPE
        },
        clientIds = {
                BackendConstants.ANDROID_CLIENT_ID_RELEASE,
                BackendConstants.ANDROID_CLIENT_ID_DEV,
                BackendConstants.WEB_CLIENT_ID
        },
        audiences = {
                BackendConstants.ANDROID_AUDIENCE
        }
)
@ApiClass(
        resource = "daily"
)
public class DailyEndpoint {

    private static final Logger logger = Logger.getLogger(DailyEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(Daily.class);
        ObjectifyService.register(Menu.class);
    }

    /**
     * Returns the {@link Daily} with the corresponding ID.
     *
     * @param ID the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code Daily} with the provided ID.
     */
    @ApiMethod(
            name = "get",
//            path = "daily/{ID}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public Daily getDaily(@Named("ID") Long ID) throws NotFoundException {
        logger.info("Getting Daily with ID: " + ID);
        Daily daily = ofy().load().type(Daily.class).id(ID).now();
        if (daily == null) {
            throw new NotFoundException("Could not find Daily with ID: " + ID);
        }
        return daily;
    }

    @ApiMethod(
            name = "listDailyForDate",
            path = "listDailyForDate/{date}"
    )
    public List<Daily> listDailyForDate(@Named("date") String date) {
        logger.info("Executing listForDate() for " + date);
        List<Daily> list = ofy().load().type(Daily.class).filter("date", date).list();
        return list;
    }

    /**
     * List all entities.
     *
     * @param cursor used for pagination to determine which page to return
     * @param limit  the maximum number of entries to return
     * @return a response that encapsulates the result list and the next page token/cursor
     */
    @ApiMethod(
            name = "list",
            path = "daily",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Daily> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<Daily> query = ofy().load().type(Daily.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<Daily> queryIterator = query.iterator();
        List<Daily> dailyList = new ArrayList<Daily>(limit);
        while (queryIterator.hasNext()) {
            dailyList.add(queryIterator.next());
        }
        return CollectionResponse.<Daily>builder().setItems(dailyList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(Long ID) throws NotFoundException {
        try {
            ofy().load().type(Daily.class).id(ID).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find Daily with ID: " + ID);
        }
    }
}