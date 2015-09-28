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
        name = "menuApi",
        version = "v1",
        resource = "menu",
        namespace = @ApiNamespace(
                ownerDomain = "backend.bobplanet.kr",
                ownerName = "backend.bobplanet.kr",
                packagePath = ""
        )
)
public class MenuEndpoint {

    private static final Logger logger = Logger.getLogger(MenuEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(Menu.class);
    }

    /**
     * Returns the {@link Menu} with the corresponding ID.
     *
     * @param name the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code Menu} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "menu/{name}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public Menu get(@Named("name") String name) throws NotFoundException {
        logger.info("Getting Menu with ID: " + name);
        Menu menu = ofy().load().type(Menu.class).id(name).now();
        if (menu == null) {
            throw new NotFoundException("Could not find Menu with ID: " + name);
        }
        return menu;
    }

    /**
     * Inserts a new {@code Menu}.
     */
    @ApiMethod(
            name = "insert",
            path = "menu",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Menu insert(Menu menu) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that menu.name has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        ofy().save().entity(menu).now();
        logger.info("Created Menu with ID: " + menu.getName());

        return ofy().load().entity(menu).now();
    }

    /**
     * Updates an existing {@code Menu}.
     *
     * @param name the ID of the entity to be updated
     * @param menu the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code name} does not correspond to an existing
     *                           {@code Menu}
     */
    @ApiMethod(
            name = "update",
            path = "menu/{name}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public Menu update(@Named("name") String name, Menu menu) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(name);
        ofy().save().entity(menu).now();
        logger.info("Updated Menu: " + menu);
        return ofy().load().entity(menu).now();
    }

    /**
     * Deletes the specified {@code Menu}.
     *
     * @param name the ID of the entity to delete
     * @throws NotFoundException if the {@code name} does not correspond to an existing
     *                           {@code Menu}
     */
    @ApiMethod(
            name = "remove",
            path = "menu/{name}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("name") String name) throws NotFoundException {
        checkExists(name);
        ofy().delete().type(Menu.class).id(name).now();
        logger.info("Deleted Menu with ID: " + name);
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
            path = "menu",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Menu> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<Menu> query = ofy().load().type(Menu.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<Menu> queryIterator = query.iterator();
        List<Menu> menuList = new ArrayList<Menu>(limit);
        while (queryIterator.hasNext()) {
            menuList.add(queryIterator.next());
        }
        return CollectionResponse.<Menu>builder().setItems(menuList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(String name) throws NotFoundException {
        try {
            ofy().load().type(Menu.class).id(name).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find Menu with ID: " + name);
        }
    }
}