package BDD;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Nom de la base de données
    private static final String DATABASE_NAME = "cours.db";

    // Version de la base de données
    private static final int DATABASE_VERSION = 1;

    // Nom de la table "cours"
    private static final String TABLE_COURS_NAME = "cours";

    // Nom des colonnes de la table "cours"
    private static final String COLUMN_COURS_UID = "uid";
    private static final String COLUMN_COURS_DATE = "date";

    // Requête SQL pour créer la table "cours"
    private static final String SQL_CREATE_TABLE_COURS =
            "CREATE TABLE " + TABLE_COURS_NAME + " (" +
                    COLUMN_COURS_UID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_COURS_DATE + " TEXT)";

    // Nom de la table "checklists"
    private static final String TABLE_CHECKLIST_NAME = "checklist";

    // Nom des colonnes de la table "checklists"
    private static final String COLUMN_CHECKLISTS_UID = "uid";
    private static final String COLUMN_CHECKLISTS_ID = "id";
    private static final String COLUMN_CHECKLISTS_LABEL = "label";

    // Requête SQL pour créer la table "checklists"
    private static final String SQL_CREATE_TABLE_CHECKLISTS =
            "CREATE TABLE " + TABLE_CHECKLIST_NAME + " (" +
                    COLUMN_CHECKLISTS_UID + " INTEGER," +
                    COLUMN_CHECKLISTS_ID + " INTEGER," +
                    COLUMN_CHECKLISTS_LABEL + " TEXT," +
                    "PRIMARY KEY (" + COLUMN_CHECKLISTS_UID + "," + COLUMN_CHECKLISTS_ID + ")," +
                    "FOREIGN KEY (" + COLUMN_CHECKLISTS_UID + ") REFERENCES " + TABLE_COURS_NAME + " (" + COLUMN_COURS_UID + "))";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_COURS);
        db.execSQL(SQL_CREATE_TABLE_CHECKLISTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Cette méthode est appelée lorsque la version de la base de données change.
        // Ici, vous pouvez ajouter ou supprimer des tables ou modifier la structure de la base de données.
        // Nous allons simplement supprimer les tables et les recréer.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHECKLIST_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURS_NAME);
        onCreate(db);
    }
}

