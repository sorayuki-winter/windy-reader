package com.wintersky.windyreader.data.source.local;

import android.support.annotation.NonNull;

import java.util.Date;
import java.util.Objects;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

public class Migration implements RealmMigration {

    public static int REALM_VERSION = 3;

    @Override
    public void migrate(@NonNull final DynamicRealm realm, long oldVersion, long newVersion) {
        // DynamicRealm exposes an editable schema
        RealmSchema schema = realm.getSchema();

        /* Book:
         * + private Date lastRead;
         * + private boolean hasNew;
         *
         * Chapter:
         * + private String bookUrl;
         */
        if (oldVersion == 0) {
            Objects.requireNonNull(schema.get("Chapter"))
                    .addField("bookUrl", String.class);

            Objects.requireNonNull(schema.get("Book"))
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

        /* Chapter:
         * - private String bookUrl;
         * + private String catalogUrl;
         */
        if (oldVersion == 1) {
            Objects.requireNonNull(schema.get("Chapter"))
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

        /* Book:
         * - private int progress;
         * + private float progress;
         */
        if (oldVersion == 2) {
            Objects.requireNonNull(schema.get("Book"))
                    .addField("index_temp", float.class)
                    .transform(new RealmObjectSchema.Function() {
                        @Override
                        public void apply(@NonNull DynamicRealmObject obj) {
                            float index = obj.getInt("progress");
                            obj.setFloat("index_temp", index);
                        }
                    })
                    .removeField("progress")
                    .renameField("index_temp", "progress");
            //noinspection UnusedAssignment
            oldVersion++;
        }
    }
}
