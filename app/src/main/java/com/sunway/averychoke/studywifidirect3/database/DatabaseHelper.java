package com.sunway.averychoke.studywifidirect3.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;

import com.sunway.averychoke.studywifidirect3.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AveryChoke on 29/1/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    // region All Static variables

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "study_app.db";

    // region Class Table
    private static final String TABLE_CLASS = "class";
    // columns name
    private static final String CLASS_NAME = "name";
    // create table statement
    private static final String CREATE_TABLE_CLASS =
            "CREATE TABLE " + TABLE_CLASS + "("
                + CLASS_NAME + " TEXT PRIMARY KEY)";
    // endregion Class Table

    // region Class Material Table
    private static final String TABLE_CLASS_MATERIAL = "class_material";
    // columns name
    private static final String CLASS_MATERIAL_ID = "id";
    private static final String CLASS_MATERIAL_CLASS_NAME = "class_name";
    private static final String CLASS_MATERIAL_NAME = "name";
    private static final String CLASS_MATERIAL_VISIBLE = "visible";
    // create table statement
    private static final String CREATE_TABLE_CLASS_MATERIAL =
            "CREATE TABLE " + TABLE_CLASS_MATERIAL + "("
                    + CLASS_MATERIAL_ID + " INTEGER PRIMARY KEY,"
                    + CLASS_MATERIAL_CLASS_NAME + " TEXT NOT NULL,"
                    + CLASS_MATERIAL_NAME + " TEXT,"
                    + CLASS_MATERIAL_VISIBLE + " BOOLEAN,"
                    + "FOREIGN KEY(" + CLASS_MATERIAL_CLASS_NAME + ") REFERENCES " + TABLE_CLASS + "(" + CLASS_NAME + ") ON DELETE CASCADE)";
    // endregion Class Material Table

    // region Quiz Table
    private static final String TABLE_QUIZ = "quiz";
    // columns names
    private static final String QUIZ_ID = "id";
    private static final String QUIZ_ANSWERED = "answered";
    private static final String QUIZ_VERSION = "version";
    //create table statement
    private static final String CREATE_TABLE_QUIZ =
            "CREATE TABLE " + TABLE_QUIZ + "("
                    + QUIZ_ID + " INTEGER PRIMARY KEY,"
                    + QUIZ_ANSWERED + " BOOLEAN,"
                    + QUIZ_VERSION + " INTEGER,"
                    + "FOREIGN KEY(" + QUIZ_ID + ") REFERENCES " + TABLE_CLASS_MATERIAL + "(" + CLASS_MATERIAL_ID + ") ON DELETE CASCADE)";
    // endregion Quiz Table

    // region Question Table
    private static final String TABLE_QUESTION = "question_table";
    //columns name
    private static final String QUESTION_ID = "id";
    private static final String QUESTION_QUIZ_ID = "quiz_id";
    private static final String QUESTION_QUESTION = "question";
    private static final String QUESTION_CORRECT_ANSWER = "correct_answer";
    private static final String QUESTION_MARKS = "marks";
    private static final String QUESTION_USER_ANSWER = "user_answer";
    //create table statement
    private static final String CREATE_TABLE_QUESTION =
            "CREATE TABLE " + TABLE_QUESTION + "("
                    + QUESTION_ID + " INTEGER PRIMARY KEY,"
                    + QUESTION_QUIZ_ID + " INTEGER NOT NULL,"
                    + QUESTION_QUESTION + " TEXT,"
                    + QUESTION_CORRECT_ANSWER + " TEXT,"
                    + QUESTION_MARKS + " INTEGER,"
                    + QUESTION_USER_ANSWER + " TEXT,"
                    + "FOREIGN KEY(" + QUESTION_QUIZ_ID + ") REFERENCES " + TABLE_QUIZ + "(" + QUIZ_ID + ") ON DELETE CASCADE)";
    // endregion Question Table

    // region ChoiceQuestion Table
    private static final String TABLE_CHOICE_QUESTION = "choice_question";
    //columns name
    private static final String CHOICE_QUESTION_ID = "id";
    private static final String CHOICE_QUESTION_CHOICES = "choices";
    //create table statement
    private static final String CREATE_TABLE_CHOICE_QUESTION =
            "CREATE TABLE " + TABLE_CHOICE_QUESTION + "("
                    + CHOICE_QUESTION_ID + " INTEGER PRIMARY KEY,"
                    + CHOICE_QUESTION_CHOICES + " TEXT,"
                    + "FOREIGN KEY(" + CHOICE_QUESTION_ID + ") REFERENCES " + TABLE_QUESTION + "(" + QUESTION_ID + ") ON DELETE CASCADE)";
    // endregion ChoiceQuestion table

    // region Study Material Table
    private static final String TABLE_STUDY_MATERIAL = "study_material";
    //columns names
    private static final String STUDY_MATERIAL_ID = "id";
    private static final String STUDY_MATERIAL_PATH = "path";
    //create table statement
    private static final String CREATE_TABLE_STUDY_MATERIAL =
            "CREATE TABLE " + TABLE_STUDY_MATERIAL + "("
                + STUDY_MATERIAL_ID + " INTEGER PRIMARY KEY,"
                + STUDY_MATERIAL_PATH + " TEXT,"
                + "FOREIGN KEY(" + STUDY_MATERIAL_ID + ") REFERENCES " + TABLE_CLASS_MATERIAL + "(" + CLASS_MATERIAL_ID + ") ON DELETE CASCADE)";
    // endregion Study Material Table

    // endregion All Static variables


    //Constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CLASS);
        db.execSQL(CREATE_TABLE_CLASS_MATERIAL);
        db.execSQL(CREATE_TABLE_QUIZ);
        db.execSQL(CREATE_TABLE_QUESTION);
        db.execSQL(CREATE_TABLE_CHOICE_QUESTION);
        db.execSQL(CREATE_TABLE_STUDY_MATERIAL);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASS_MATERIAL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUIZ);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHOICE_QUESTION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDY_MATERIAL);

        // Create tables again
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db){
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys=ON");
    }

    // region --------------- SQL FUNCTIONS ---------------------------------------

    // region Class Table
    public long addClass(StudyClass studyClass) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        ContentValues values = new ContentValues();
        values.put(CLASS_NAME, studyClass.getName());

        // insert row
        long id = db.insert(TABLE_CLASS, null, values);

        // insert quizzes
        for (Quiz quiz : studyClass.getQuizzes()) {
            addQuiz(quiz, studyClass.getName());
        }
        //insert study materials
        for (StudyMaterial studyMaterial : studyClass.getStudyMaterials()) {
            addStudyMaterial(studyMaterial, studyClass.getName());
        }

        db.setTransactionSuccessful();
        db.endTransaction();
        return id;
    }

    public List<String> getAllClassesName() {
        List<String> classesName = new ArrayList<>();
        String selectQuery = "SELECT " + CLASS_NAME + " FROM " + TABLE_CLASS;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = null;
        try {
            c = db.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (c.moveToFirst()) {
                do {
                    try {
                        classesName.add(c.getString(c.getColumnIndex(CLASS_NAME)));
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                } while (c.moveToNext());
            }
        }
        finally {
            if(c != null) {
                c.close();
            }
        }

        return classesName;
    }

    public StudyClass getClass(String name) {
        StudyClass studyClass = null;
        String selectQuery =
                "SELECT * FROM " + TABLE_CLASS
                        + " WHERE " + CLASS_NAME + " = ?";

        SQLiteDatabase db = this.getReadableDatabase();
        db.beginTransaction();

        Cursor c = null;
        try {
            c = db.rawQuery(selectQuery, new String[]{ name });

            if (c.moveToFirst()) {
                try {
                    String className = c.getString(c.getColumnIndex(CLASS_NAME));
                    List<Quiz> quizzes = getClassQuizzes(className);
                    List<StudyMaterial> studyMaterials = getClassStudyMaterials(className);

                    studyClass = new StudyClass(className, quizzes, studyMaterials);
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
            db.setTransactionSuccessful();
        } finally {
            if(c != null) {
                c.close();
            }
        }
        db.endTransaction();

        return studyClass;
    }

    public void updateClassQuizzes(StudyClass studyClass) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        // clear previous quizzes
        clearClassQuizzes(studyClass.getName());

        // insert new quizzes
        for (Quiz quiz : studyClass.getQuizzes()) {
            addQuiz(quiz, studyClass.getName());
        }

        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void deleteClass(String name) {
        SQLiteDatabase db = this.getWritableDatabase();

        //delete study material
        db.delete(TABLE_CLASS, CLASS_NAME + " = ?",
                new String[] { name });
    }
    // endregion Class Table

    // region Class Material Table
    private long addClassMaterial(ClassMaterial classMaterial, String className) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CLASS_MATERIAL_ID, classMaterial.getId());
        values.put(CLASS_MATERIAL_CLASS_NAME, className);
        values.put(CLASS_MATERIAL_NAME, classMaterial.getName());
        values.put(CLASS_MATERIAL_VISIBLE, classMaterial.isVisible());

        long id = db.insert(TABLE_CLASS_MATERIAL, null, values);
        return id;
    }

    private int updateClassMaterial(ClassMaterial classMaterial) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CLASS_MATERIAL_NAME, classMaterial.getName());
        values.put(CLASS_MATERIAL_VISIBLE, classMaterial.isVisible());

        // updating row
        return db.update(TABLE_CLASS_MATERIAL, values, CLASS_MATERIAL_ID + " = ?",
                new String[] { String.valueOf(classMaterial.getId()) });
    }

    public int updateClassMaterialVisible(ClassMaterial classMaterial) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CLASS_MATERIAL_VISIBLE, classMaterial.isVisible());

        // updating row
        return db.update(TABLE_CLASS_MATERIAL, values, CLASS_MATERIAL_ID + " = ?",
                new String[] { String.valueOf(classMaterial.getId()) });
    }

    public int deleteClassMaterial(ClassMaterial classMaterial) {
        SQLiteDatabase db = this.getWritableDatabase();

        //delete quiz
        return db.delete(TABLE_CLASS_MATERIAL, CLASS_MATERIAL_ID + " = ?",
                new String[] { String.valueOf(classMaterial.getId()) });
    }

    public long getClassMaterialMaxId() {
        return getMaxId(CLASS_MATERIAL_ID, TABLE_CLASS_MATERIAL);
    }
    // endregion Class Material Table

    // region Quiz Table
    public long addQuiz(Quiz quiz, String className) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        // insert superclass
        addClassMaterial(quiz, className);

        ContentValues values = new ContentValues();
        values.put(QUIZ_ID, quiz.getId());
        values.put(QUIZ_ANSWERED, quiz.isAnswered());
        values.put(QUIZ_VERSION, quiz.getVersion());

        // insert row
        long id = db.insert(TABLE_QUIZ, null, values);

        //insert questions
        for(Question question : quiz.getQuestions()) {
            if (question instanceof ChoiceQuestion) {
                addChoiceQuestion((ChoiceQuestion) question, quiz.getId());
            } else {
                addQuestion(question, quiz.getId());
            }
        }

        db.setTransactionSuccessful();
        db.endTransaction();
        return id;
    }

    // get the quizzes based on the class name
    public List<Quiz> getClassQuizzes(String className) {
        List<Quiz> quizzes = new ArrayList<>();
        String selectQuery =
                "SELECT " + TABLE_CLASS_MATERIAL + ".*," + TABLE_QUIZ + ".*"
                        + " FROM " + TABLE_CLASS_MATERIAL + "," + TABLE_QUIZ
                        + " WHERE " + TABLE_CLASS_MATERIAL + "." + CLASS_MATERIAL_ID + "=" + TABLE_QUIZ + "." + QUIZ_ID
                        + " AND " + TABLE_CLASS_MATERIAL + "." + CLASS_MATERIAL_CLASS_NAME + " =?";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = null;
        try {
            c = db.rawQuery(selectQuery,  new String[]{ className });

            // looping through all rows and adding to list
            if (c.moveToFirst()) {
                do {
                    try {
                        long classMaterialId = c.getLong(c.getColumnIndex(CLASS_MATERIAL_ID));
                        List<Question> quizQuestions = getQuizQuestions(classMaterialId);
                        Quiz quiz = new Quiz(
                                classMaterialId,
                                c.getString(c.getColumnIndex(CLASS_MATERIAL_NAME)),
                                quizQuestions,
                                c.getInt(c.getColumnIndex(QUIZ_ANSWERED)) != 0,
                                c.getInt(c.getColumnIndex(QUIZ_VERSION)),
                                c.getInt(c.getColumnIndex(CLASS_MATERIAL_VISIBLE)) != 0
                        );

                        quizzes.add(quiz);
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                } while (c.moveToNext());
            }
            return quizzes;
        }
        finally {
            if(c != null) {
                c.close();
            }
        }
    }

    public int updateQuiz(Quiz quiz) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        // update superclass
        updateClassMaterial(quiz);

        ContentValues values = new ContentValues();
        values.put(QUIZ_ANSWERED, quiz.isAnswered());
        values.put(QUIZ_VERSION, quiz.getVersion());

        //update the question list
        clearQuizQuestion(quiz.getId());
        for(Question question : quiz.getQuestions()) {
            if (question instanceof ChoiceQuestion) {
                addChoiceQuestion((ChoiceQuestion) question, quiz.getId());
            } else {
                addQuestion(question, quiz.getId());
            }
        }

        // updating row
        int id = db.update(TABLE_QUIZ, values, QUIZ_ID + " = ?",
                new String[] { String.valueOf(quiz.getId()) });

        db.setTransactionSuccessful();
        db.endTransaction();
        return id;
    }

    public int updateQuizAnswers(Quiz quiz) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        ContentValues values = new ContentValues();
        values.put(QUIZ_ANSWERED, quiz.isAnswered());

        // update questions
        for (Question question : quiz.getQuestions()) {
            updateQuestionAnswer(question);
        }

        // updating row
        int id = db.update(TABLE_QUIZ, values, QUIZ_ID + " = ?",
                new String[] { String.valueOf(quiz.getId()) });

        db.setTransactionSuccessful();
        db.endTransaction();
        return id;
    }

    // delete all quizzes that belong to the class
    private void clearClassQuizzes(String className) {
        SQLiteDatabase db = this.getWritableDatabase();

        String whereString = CLASS_MATERIAL_ID + " in ("
                + "SELECT " + TABLE_CLASS_MATERIAL + "." + CLASS_MATERIAL_ID
                + " FROM " + TABLE_CLASS_MATERIAL + "," + TABLE_QUIZ
                + " WHERE " + TABLE_CLASS_MATERIAL + "." + CLASS_MATERIAL_ID + " = " +  TABLE_QUIZ + "." + QUIZ_ID
                + " AND " + CLASS_MATERIAL_CLASS_NAME + " = ? )";

        db.delete(TABLE_CLASS_MATERIAL, whereString,
                new String[] { String.valueOf(className)});
    }
    // endregion Quiz Table

    // region Question Table
    public long addQuestion(Question question, long quizId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(QUESTION_ID, question.getId());
        values.put(QUESTION_QUIZ_ID, quizId);
        values.put(QUESTION_QUESTION, question.getQuestion());
        values.put(QUESTION_CORRECT_ANSWER, question.getCorrectAnswer());
        values.put(QUESTION_MARKS, question.getTotalMarks());
        values.put(QUESTION_USER_ANSWER, question.getUserAnswer());

        // insert row
        long id = db.insert(TABLE_QUESTION, null, values);

        return id;
    }

    public Question getQuestion(long id) {
        Question question = null;
        String selectQuery =
                "SELECT * FROM " + TABLE_QUESTION
                        + " WHERE " + QUESTION_ID + " =?";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = null;
        try {
            c = db.rawQuery(selectQuery, new String[]{String.valueOf(id)});

            if (c.moveToFirst()) {
                try {
                    question = new Question(
                            c.getLong(c.getColumnIndex(QUESTION_ID)),
                            c.getString(c.getColumnIndex(QUESTION_QUESTION)),
                            c.getString(c.getColumnIndex(QUESTION_CORRECT_ANSWER)),
                            c.getInt(c.getColumnIndex(QUESTION_MARKS)),
                            c.getString(c.getColumnIndex(QUESTION_USER_ANSWER))
                    );
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            } else {
                return null;
            }
        }
        finally {
            if(c != null) {
                c.close();
            }
        }
        return question;
    }

    // get questions based on quiz id
    public List<Question> getQuizQuestions(long quizId) {
        List<Question> questions = new ArrayList<>();

        String selectQuery =
                "SELECT " + QUESTION_ID + " FROM " + TABLE_QUESTION
                        + " WHERE " + QUESTION_QUIZ_ID + " =?";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = null;
        try {
            c = db.rawQuery(selectQuery, new String[]{String.valueOf(quizId)});

            // looping through all rows and adding to list
            if (c.moveToFirst()) {
                do {
                    try {
                        long questionID = c.getLong(c.getColumnIndex(QUESTION_ID));
                        if (existInTable(questionID, TABLE_CHOICE_QUESTION, CHOICE_QUESTION_ID)) { // check if is a choice question
                            questions.add(getChoiceQuestion(questionID));
                        } else {
                            questions.add(getQuestion(questionID));
                        }
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                } while (c.moveToNext());
            }
            return questions;
        }
        finally {
            if(c != null)
            {
                c.close();
            }
        }
    }

    public int updateQuestion(Question question) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(QUESTION_QUESTION, question.getQuestion());
        values.put(QUESTION_CORRECT_ANSWER, question.getCorrectAnswer());
        values.put(QUESTION_MARKS, question.getTotalMarks());

        // updating row
        return db.update(TABLE_QUESTION, values, QUESTION_ID + " = ?",
                new String[] { String.valueOf(question.getId()) });
    }

    public int updateQuestionAnswer(Question question) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(QUESTION_USER_ANSWER, question.getUserAnswer());

        // updating row
        return db.update(TABLE_QUESTION, values, QUESTION_ID + " = ?",
                new String[] { String.valueOf(question.getId()) });
    }

    // delete all questions that a quiz has
    public void clearQuizQuestion(long quizId) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_QUESTION, QUESTION_QUIZ_ID + " = ?",
                new String[] { String.valueOf(quizId)});
    }

    public long getQuestionMaxId()
    {
        return getMaxId(QUESTION_ID, TABLE_QUESTION);
    }
    // endregion Question Table

    // region Choice Question Table
    public long addChoiceQuestion(ChoiceQuestion choiceQuestion, long quizId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // insert superclass
        addQuestion(choiceQuestion, quizId);

        //Convert the choices to string
        JSONArray jsonArray = new JSONArray(choiceQuestion.getChoices());

        //Store data into choice question table
        ContentValues values = new ContentValues();
        values.put(CHOICE_QUESTION_ID, choiceQuestion.getId());
        values.put(CHOICE_QUESTION_CHOICES, jsonArray.toString());

        // insert row
        long id = db.insert(TABLE_CHOICE_QUESTION, null, values);

        return id;
    }

    public ChoiceQuestion getChoiceQuestion(long id) {
        String selectQuery =
                "SELECT " + TABLE_QUESTION + ".*," + TABLE_CHOICE_QUESTION + "." + CHOICE_QUESTION_CHOICES
                        + " FROM " + TABLE_QUESTION + "," + TABLE_CHOICE_QUESTION
                        + " WHERE " + TABLE_QUESTION + "." + QUESTION_ID + "=" + TABLE_CHOICE_QUESTION + "." + CHOICE_QUESTION_ID
                        + " AND " + TABLE_QUESTION + "." + QUESTION_ID + " =?";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = null;
        try {
            c = db.rawQuery(selectQuery, new String[]{String.valueOf(id)});

            if (c.moveToFirst()) {
                //Convert the string back to list
                List<String> choices = new ArrayList<>();
                try {
                    JSONArray jsonArray = new JSONArray(c.getString(c.getColumnIndex(CHOICE_QUESTION_CHOICES)));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        choices.add(jsonArray.optString(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    ChoiceQuestion choiceQuestion = new ChoiceQuestion(
                            c.getLong(c.getColumnIndex(QUESTION_ID)),
                            c.getString(c.getColumnIndex(QUESTION_QUESTION)),
                            c.getString(c.getColumnIndex(QUESTION_CORRECT_ANSWER)),
                            c.getInt(c.getColumnIndex(QUESTION_MARKS)),
                            c.getString(c.getColumnIndex(QUESTION_USER_ANSWER)),
                            choices
                    );
                    return choiceQuestion;
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
            return null;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }
    // endregion Choice Question Table

    // region Study Material Table
    public long addStudyMaterial(StudyMaterial studyMaterial, String className) {
        SQLiteDatabase db = this.getWritableDatabase();

        // insert superclass
        addClassMaterial(studyMaterial, className);

        ContentValues values = new ContentValues();
        values.put(STUDY_MATERIAL_ID, studyMaterial.getId());
        values.put(STUDY_MATERIAL_PATH, studyMaterial.getFile().getPath());

        // insert row
        return db.insert(TABLE_STUDY_MATERIAL, null, values);
    }

    // get study materials based on class name
    public List<StudyMaterial> getClassStudyMaterials(String className) {
        List<StudyMaterial> studyMaterials = new ArrayList<>();

        String selectQuery =
                "SELECT " + TABLE_CLASS_MATERIAL + ".*," + TABLE_STUDY_MATERIAL + ".*"
                        + " FROM " + TABLE_CLASS_MATERIAL + "," + TABLE_STUDY_MATERIAL
                        + " WHERE " + TABLE_CLASS_MATERIAL + "." + CLASS_MATERIAL_ID + "=" + TABLE_STUDY_MATERIAL + "." + STUDY_MATERIAL_ID
                        + " AND " + TABLE_CLASS_MATERIAL + "." + CLASS_MATERIAL_CLASS_NAME + " =?";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = null;
        try {
            c = db.rawQuery(selectQuery, new String[]{className});

            // looping through all rows and adding to list
            if (c.moveToFirst()) {
                do {
                    try {
                        StudyMaterial studyMaterial = new StudyMaterial(
                                c.getLong(c.getColumnIndex(CLASS_MATERIAL_ID)),
                                c.getString(c.getColumnIndex(CLASS_MATERIAL_NAME)),
                                c.getString(c.getColumnIndex(STUDY_MATERIAL_PATH)),
                                c.getInt(c.getColumnIndex(CLASS_MATERIAL_VISIBLE)) != 0
                        );
                        studyMaterials.add(studyMaterial);
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                } while (c.moveToNext());
            }
        } finally {
            if(c != null) {
                c.close();
            }
        }

        return studyMaterials;
    }

    public int updateStudyMaterial(StudyMaterial studyMaterial) {
        SQLiteDatabase db = this.getWritableDatabase();

        // update superclass
        updateClassMaterial(studyMaterial);

        ContentValues values = new ContentValues();
        values.put(STUDY_MATERIAL_PATH, studyMaterial.getFile().getPath());

        // updating row
        return db.update(TABLE_STUDY_MATERIAL, values, STUDY_MATERIAL_ID + " = ?",
                new String[] { String.valueOf(studyMaterial.getId()) });
    }

    public boolean existStudyMaterial(StudyMaterial studyMaterial) {
        return existInTable(studyMaterial.getId(), TABLE_STUDY_MATERIAL, STUDY_MATERIAL_ID);
    }
    // endregion Study Material Table

    // region Helper SQL
    private boolean existInTable(long id, String table, String tableId) {
        String selectQuery =
                "SELECT 1 FROM " + table
                        + " WHERE " + tableId + " =?";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = null;
        try {
            c = db.rawQuery(selectQuery, new String[]{String.valueOf(id)});
            boolean exists = c.moveToFirst();
            return exists;
        }
        finally {
            if(c != null)
            {
                c.close();
            }
        }
    }

    private long getMaxId(String columnID, String table) {
        String selectQuery = "SELECT MAX(" + columnID + ") FROM " + table;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = null;
        try {
            c = db.rawQuery(selectQuery, null);

            if (c.moveToFirst()) {
                return c.getLong(0);
            } else {
                return 0;
            }
        }
        finally {
            if(c != null) {
                c.close();
            }
        }
    }
    // endregion Helper SQL

    // endregion --------------- SQL FUNCTIONS ---------------------------------------
}
