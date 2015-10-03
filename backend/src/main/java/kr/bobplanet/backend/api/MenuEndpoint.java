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
import com.googlecode.objectify.cmd.LoadType;
import com.googlecode.objectify.cmd.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;

import kr.bobplanet.backend.BackendConstants;
import kr.bobplanet.backend.model.Menu;
import kr.bobplanet.backend.model.Item;
import kr.bobplanet.backend.model.DailyMenu;

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
        resource = "menu"
)
public class MenuEndpoint {

    private static final Logger logger = Logger.getLogger(MenuEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(Menu.class);
        ObjectifyService.register(Item.class);
    }

    @ApiMethod(
            name = "menuOfDate",
            path = "menuOfDate/{date}"
    )
    public DailyMenu menuOfDate(@Named("date") String date) {
        logger.info("Executing menuOfDate() for " + date);

        LoadType<Menu> menubase = ofy().load().type(Menu.class);
        List<Menu> menu = menubase.filter("date", date).list();

        List<Menu> previous = menubase.filter("date <", date).order("-date").limit(1).list();
        String previous_date = previous.size() > 0 ? previous.get(0).getDate() : null;

        List<Menu> next = menubase.filter("date >", date).order("date").limit(1).list();
        String next_date = next.size() > 0 ? next.get(0).getDate() : null;

        return new DailyMenu(date, menu, previous_date, next_date);
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
    public CollectionResponse<Menu> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<Menu> query = ofy().load().type(Menu.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<Menu> queryIterator = query.iterator();
        List<Menu> dailyList = new ArrayList<Menu>(limit);
        while (queryIterator.hasNext()) {
            dailyList.add(queryIterator.next());
        }
        return CollectionResponse.<Menu>builder().setItems(dailyList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(Long ID) throws NotFoundException {
        try {
            ofy().load().type(Menu.class).id(ID).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find Menu with ID: " + ID);
        }
    }
}