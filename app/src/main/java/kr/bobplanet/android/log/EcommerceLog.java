package kr.bobplanet.android.log;

import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;

import kr.bobplanet.backend.bobplanetApi.model.DailyMenu;
import kr.bobplanet.backend.bobplanetApi.model.Menu;

public class EcommerceLog extends Log {
    private DailyMenu dailyMenu;

    private EcommerceLog(DailyMenu dailyMenu) {
        this.dailyMenu = dailyMenu;
    }

    public static void measureItemList(DailyMenu dailyMenu) {
        new EcommerceLog(dailyMenu).dispatch();
    }

    @Override
    protected void dispatchFirebase(FirebaseAnalytics firebase) {
        ArrayList products = new ArrayList();
        for (Menu menu : dailyMenu.getMenu()) {
            Bundle product = new Bundle();
            product.putLong(FirebaseAnalytics.Param.ITEM_ID, menu.getId());
            product.putString(FirebaseAnalytics.Param.ITEM_NAME, menu.getItem().getName());
            product.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, menu.getWhen());
            product.putDouble(FirebaseAnalytics.Param.PRICE, menu.getCalories());
            products.add(product);
        }

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_LIST, dailyMenu.getDate());
        bundle.putParcelableArrayList("items", products);

        firebase.logEvent(FirebaseAnalytics.Event.VIEW_ITEM_LIST, bundle);
    }

}
