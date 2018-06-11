package kr.bobplanet.android.log;

import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

import kr.bobplanet.backend.bobplanetApi.model.DailyMenu;
import kr.bobplanet.backend.bobplanetApi.model.Menu;

public class EcommerceLog {
    public static void measureItemList(DailyMenu dailyMenu) {
        new ItemListLog(dailyMenu).dispatch();
    }

    public static void measureItemView(Menu menu) {
        new ItemViewLog(menu).dispatch();
    }

    public static void measureItemPurchase(Menu menu) {
        new ItemPurchaseLog(menu).dispatch();
    }

    private static void addProductInfo(Menu menu, Bundle bundle) {
        bundle.putLong(FirebaseAnalytics.Param.ITEM_ID, menu.getId());
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, menu.getItem().getName());
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, menu.getWhen());
        bundle.putDouble(FirebaseAnalytics.Param.PRICE, menu.getCalories());
    }

    private static class ItemListLog extends Log {
        private DailyMenu dailyMenu;

        private ItemListLog(DailyMenu dailyMenu) {
            this.dailyMenu = dailyMenu;
        }

        @Override
        protected void dispatchFirebase(FirebaseAnalytics firebase) {
            ArrayList products = new ArrayList();
            for (Menu menu : dailyMenu.getMenu()) {
                Bundle product = new Bundle();
                addProductInfo(menu, product);
                products.add(product);
            }

            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_LIST, dailyMenu.getDate());
            bundle.putParcelableArrayList("items", products);

            firebase.logEvent(FirebaseAnalytics.Event.VIEW_ITEM_LIST, bundle);
        }
    }

    private static class ItemViewLog extends Log {
        private Menu menu;

        private ItemViewLog(Menu menu) {
            this.menu = menu;
        }

        @Override
        protected void dispatchFirebase(FirebaseAnalytics firebase) {
            Bundle product = new Bundle();
            addProductInfo(menu, product);

            Bundle bundle = new Bundle();
            bundle.putBundle("items", product);

            firebase.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);
        }
    }

    private static class ItemPurchaseLog extends Log {
        private Menu menu;

        private ItemPurchaseLog(Menu menu) {
            this.menu = menu;
        }

        @Override
        protected void dispatchFirebase(FirebaseAnalytics firebase) {
            Bundle product = new Bundle();
            addProductInfo(menu, product);
            product.putLong(FirebaseAnalytics.Param.QUANTITY, 1);

            Bundle bundle = new Bundle();
            bundle.putBundle("items", product);
            bundle.putDouble(FirebaseAnalytics.Param.VALUE, menu.getCalories());

            firebase.logEvent(FirebaseAnalytics.Event.ECOMMERCE_PURCHASE, bundle);
        }
    }
}
