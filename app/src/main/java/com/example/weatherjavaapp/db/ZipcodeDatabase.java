package com.example.weatherjavaapp.db;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.List;

// Note version should be changed whenever database changes to ensure
// that db is recreated.  You can add a migration to say how the database
// should be changed from version to version.
// Note: If you are changing the schema of your database while debugging,
// you will get an error.  Simply uninstall the app on your phone to
// ensure that the database will be deleted, and then recreated with the
// new schema.
@Database(entities = {Zipcode.class}, version = 1, exportSchema = false)
public abstract class ZipcodeDatabase extends RoomDatabase {
    public interface ZipcodeListener {
        void onZipcodeReturned(Zipcode zipcode);
    }

    public abstract ZipcodeDAO zipcodeDAO();

    private static ZipcodeDatabase INSTANCE;

    public static ZipcodeDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ZipcodeDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ZipcodeDatabase.class, "zipcode_database")
                            .addCallback(createZipcodeDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // Note this call back will be run
    private static Callback createZipcodeDatabaseCallback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            createZipcodeTable();
        }
    };

    public static void createZipcodeTable() {
        for (int i = 0; i < DefaultContent.ZIP.length; i++) {
            Log.d("DEFAULTCONTENT", DefaultContent.CITY[i]);
            System.out.println(DefaultContent.CITY[i]);
            insert(new Zipcode(DefaultContent.ZIP[i], DefaultContent.CITY[i], DefaultContent.STATUS[i]));
        }
    }

    public static void getZipcode(int x, ZipcodeListener listener) {
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                listener.onZipcodeReturned((Zipcode) msg.obj);
            }
        };

        (new Thread(() -> {
            Message msg = handler.obtainMessage();
            msg.obj = INSTANCE.zipcodeDAO().getCurrent();
            handler.sendMessage(msg);
        })).start();
    }

//    public static void getZipcodes(ZipcodeListener listener) {
//            Handler handler = new Handler(Looper.getMainLooper()) {
//                @Override
//                public void handleMessage(Message msg) {
//                    super.handleMessage(msg);
//                    Log.d("OBJ", msg.obj.toString());
//                    listener.onZipcodesReturned((List<Zipcode>) msg.obj);
//                }
//            };
//
//            (new Thread(() -> {
//                Message msg = handler.obtainMessage();
//                msg.obj = INSTANCE.zipcodeDAO().getAll();
//                handler.sendMessage(msg);
//            })).start();
//        }

    public static void insert(Zipcode zipcode) {
        (new Thread(()-> INSTANCE.zipcodeDAO().insert(zipcode))).start();
    }

//     public static void delete(int jokeId) {
//         (new Thread(() -> INSTANCE.jokeDAO().delete(jokeId))).start();
//     }


    public static void update(Zipcode zipcode) {
        (new Thread(() -> INSTANCE.zipcodeDAO().update(zipcode))).start();
    }

    public static void deleteAll() {
        (new Thread(() -> INSTANCE.zipcodeDAO().deleteAll())).start();
    }
}