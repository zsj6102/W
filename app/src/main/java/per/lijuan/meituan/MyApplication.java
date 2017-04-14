package per.lijuan.meituan;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by admin on 2017/3/22.
 */

public class MyApplication extends Application {
    private String realmName = "dk.realm";

    @Override
    public void onCreate() {
        super.onCreate();

        RealmConfiguration realmConfig = new RealmConfiguration.Builder(this).name(realmName).deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(realmConfig);

    }
}
