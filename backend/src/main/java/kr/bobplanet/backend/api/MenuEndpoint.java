package kr.bobplanet.backend.api;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiClass;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
//import com.google.appengine.api.users.User;
import com.googlecode.objectify.VoidWork;
import com.googlecode.objectify.cmd.LoadType;

import java.util.List;
import java.util.logging.Logger;

import javax.inject.Named;

import kr.bobplanet.backend.BackendConstants;
import kr.bobplanet.backend.model.Menu;
import kr.bobplanet.backend.model.Item;
import kr.bobplanet.backend.model.DailyMenu;
import kr.bobplanet.backend.model.User;
import kr.bobplanet.backend.model.Vote;

import static kr.bobplanet.backend.api.ObjectifyRegister.ofy;

@Api(
        name = "bobplanetApi",
        version = "v1",
        title = "Bobplanet Server API",
        description = "These API sets provide serverside APIs for Bobplanet project",
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

    @ApiMethod(
            name = "menuOfDate",
            path = "menuOfDate/{date}"
    )
    public DailyMenu menuOfDate(@Named("date") String date) {
        logger.info("Executing menuOfDate() : date = " + date);

        LoadType<Menu> menubase = ofy().load().type(Menu.class);
        List<Menu> menu = menubase.filter("date", date).list();

        List<Menu> previous = menubase.filter("date <", date).order("-date").limit(1).list();
        String previous_date = previous.size() > 0 ? previous.get(0).getDate() : null;

        List<Menu> next = menubase.filter("date >", date).order("date").limit(1).list();
        String next_date = next.size() > 0 ? next.get(0).getDate() : null;

        return new DailyMenu(date, menu, previous_date, next_date);
    }

    @ApiMethod(
            name = "vote"
    )
    public void vote(@Named("itemName") final String itemName, @Named("menuId") final Long menuId,
                     @Named("score") final int score) {
        logger.info("Executing vote() : { itemName, menuId, score }  = {" + new StringBuilder().append(itemName)
                .append(", ").append(menuId).append(", ").append(score).append(" }"));

        final Item item = ofy().load().entity(new Item(itemName)).now();
        item.addScore(score);

        final Vote vote = new Vote(new User("1234"), item, new Menu(menuId));
        vote.setScore(score);


        ofy().transact(new VoidWork() {
            @Override
            public void vrun() {
                ofy().save().entity(vote);
                ofy().save().entity(item);
            }
        });
    }
}