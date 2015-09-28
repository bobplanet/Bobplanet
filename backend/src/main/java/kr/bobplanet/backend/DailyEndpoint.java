package kr.bobplanet.backend;

import com.google.api.server.spi.config.Api;
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

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * WARNING: This generated code is intended as a sample or starting point for using a
 * Google Cloud Endpoints RESTful API with an Objectify entity. It provides no data access
 * restrictions and no data validation.
 * <p/>
 * DO NOT deploy this code unchanged as part of a real application to real users.
 */
@Api(
        name = "dailyApi",
        version = "v1",
        resource = "daily",
        namespace = @ApiNamespace(
                ownerDomain = "backend.bobplanet.kr",
                ownerName = "backend.bobplanet.kr",
                packagePath = ""
        )
)
public class DailyEndpoint {

    private static final Logger logger = Logger.getLogger(DailyEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(Daily.class);
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
            path = "daily/{ID}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public Daily get(@Named("ID") Long ID) throws NotFoundException {
        logger.info("Getting Daily with ID: " + ID);
        Daily daily = ofy().load().type(Daily.class).id(ID).now();
        if (daily == null) {
            throw new NotFoundException("Could not find Daily with ID: " + ID);
        }
        return daily;
    }

    /**
     * Inserts a new {@code Daily}.
     */
    @ApiMethod(
            name = "insert",
            path = "daily",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Daily insert(Daily daily) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that daily.ID has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        ofy().save().entity(daily).now();
        logger.info("Created Daily with ID: " + daily.getID());

        return ofy().load().entity(daily).now();
    }

    /**
     * Updates an existing {@code Daily}.
     *
     * @param ID    the ID of the entity to be updated
     * @param daily the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code ID} does not correspond to an existing
     *                           {@code Daily}
     */
    @ApiMethod(
            name = "update",
            path = "daily/{ID}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public Daily update(@Named("ID") Long ID, Daily daily) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(ID);
        ofy().save().entity(daily).now();
        logger.info("Updated Daily: " + daily);
        return ofy().load().entity(daily).now();
    }

    /**
     * Deletes the specified {@code Daily}.
     *
     * @param ID the ID of the entity to delete
     * @throws NotFoundException if the {@code ID} does not correspond to an existing
     *                           {@code Daily}
     */
    @ApiMethod(
            name = "remove",
            path = "daily/{ID}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("ID") Long ID) throws NotFoundException {
        checkExists(ID);
        ofy().delete().type(Daily.class).id(ID).now();
        logger.info("Deleted Daily with ID: " + ID);
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