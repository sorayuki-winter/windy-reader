package com.wintersky.windyreader.data.source.local;

import android.support.annotation.NonNull;

import java.util.Date;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

public class Migration implements RealmMigration {

    public static int REALM_VERSION = 2;

    @Override
    public void migrate(@NonNull final DynamicRealm realm, long oldVersion, long newVersion) {
        // DynamicRealm exposes an editable schema
        RealmSchema schema = realm.getSchema();

        /* book:
         * + private Date lastRead;
         * + private boolean hasNew;
         *
         * chapter:
         * + private String bookUrl;
         */
        if (oldVersion == 0) {
            schema.get("Chapter")
                    .addField("bookUrl", String.class);

            schema.get("Book")
                    .addField("lastRead", Date.class)
                    .addField("hasNew", boolean.class)
                    .transform(new RealmObjectSchema.Function() {
                        @Override
                        public void apply(@NonNull DynamicRealmObject obj) {
                            obj.setDate("lastRead", new Date());
                            obj.setBoolean("hasNew", true);
                            for (DynamicRealmObject c : obj.getList("catalog").createSnapshot()) {
                                c.setString("bookUrl", obj.getString("url"));
                            }
                        }
                    });
            oldVersion++;
        }

        /* chapter:
         * - private String bookUrl;
         * + private String catalogUrl;
         */
        if (oldVersion == 1) {
            schema.get("Chapter")
                    .renameField("bookUrl", "catalogUrl")
                    .transform(new RealmObjectSchema.Function() {
                        @Override
                        public void apply(@NonNull DynamicRealmObject obj) {
                            DynamicRealmObject bk = realm.where("Book")
                                    .equalTo("url", obj.getString("catalogUrl"))
                                    .findFirst();
                            if (bk != null) {
                                obj.setString("catalogUrl", bk.getString("catalogUrl"));
                            }
                        }
                    });
            oldVersion++;
        }
    }
}
