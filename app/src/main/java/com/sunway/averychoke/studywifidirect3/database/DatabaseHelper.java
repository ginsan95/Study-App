package com.sunway.averychoke.studywifidirect3.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    // region Quiz Table
    private static final String TABLE_QUIZ = "quiz";
    // columns names
    private static final String QUIZ_ID = "id";
    private static final String QUIZ_CLASS_NAME = "class_name";
    private static final String QUIZ_NAME = "name";
    private static final String QUIZ_MARKS = "marks";
    private static final String QUIZ_VISIBLE = "visible";
    //create table statement
    private static final String CREATE_TABLE_QUIZ =
            "CREATE TABLE " + TABLE_QUIZ + "("
                    + QUIZ_ID + " INTEGER PRIMARY KEY,"
                    + QUIZ_CLASS_NAME + " TEXT NOT NULL,"
                    + QUIZ_NAME + " TEXT,"
                    + QUIZ_MARKS + " REAL,"
                    + QUIZ_VISIBLE + " BOOLEAN,"
                    + "FOREIGN KEY(" + QUIZ_CLASS_NAME + ") REFERENCES " + TABLE_CLASS + "(" + CLASS_NAME + ") ON DELETE CASCADE)";
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
    private static final String STUDY_MATERIAL_CLASS_NAME = "class_name";
    private static final String STUDY_MATERIAL_NAME = "name";
    private static final String STUDY_MATERIAL_PATH = "path";
    private static final String STUDY_MATERIAL_VISIBLE = "visible";
    //create table statement
    private static final String CREATE_TABLE_STUDY_MATERIAL =
            "CREATE TABLE " + TABLE_STUDY_MATERIAL + "("
                + STUDY_MATERIAL_ID + " INTEGER PRIMARY KEY,"
                + STUDY_MATERIAL_CLASS_NAME + " TEXT NOT NULL,"
                + STUDY_MATERIAL_NAME + " TEXT,"
                + STUDY_MATERIAL_PATH + " TEXT,"
                + STUDY_MATERIAL_VISIBLE + " BOOLEAN,"
                + "FOREIGN KEY(" + STUDY_MATERIAL_CLASS_NAME + ") REFERENCES " + TABLE_CLASS + "(" + CLASS_NAME + ") ON DELETE CASCADE)";
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
                    classesName.add(c.getString(c.getColumnIndex(CLASS_NAME)));
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

        Cursor c = null;
        try {
            c = db.rawQuery(selectQuery, new String[]{ name });

            if (c.moveToFirst()) {
                String className = c.getString(c.getColumnIndex(CLASS_NAME));
                List<Quiz> quizzes = getClassQuizzes(className);
                List<StudyMaterial> studyMaterials = getClassStudyMaterials(className);

                studyClass = new StudyClass(className, quizzes, studyMaterials);
            }
        } finally {
            if(c != null) {
                c.close();
            }
        }

        return studyClass;
    }

    public void deleteClass(String name) {
        SQLiteDatabase db = this.getWritableDatabase();

        //delete study material
        db.delete(TABLE_CLASS, CLASS_NAME + " = ?",
                new String[] { name });
    }
    // endregion Class Table

    // region Quiz Table
    public long addQuiz(Quiz quiz, String className)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(QUIZ_ID, quiz.getQuizId());
        values.put(QUIZ_CLASS_NAME, className);
        values.put(QUIZ_NAME, quiz.getName());
        values.put(QUIZ_MARKS, quiz.getMarks());
        values.put(QUIZ_VISIBLE, quiz.getVisible());

        // insert row
        long id = db.insert(TABLE_QUIZ, null, values);

        //insert questions
        for(Question question : quiz.getQuestions())
        {
            addQuestion(question, quiz.getQuizId());
        }

        return id;
    }

    // get the quizzes based on the class name
    public List<Quiz> getClassQuizzes(String className)
    {
        List<Quiz> quizzes = new ArrayList<>();
        String selectQuery =
                "SELECT * FROM " + TABLE_QUIZ
                        + " WHERE " + QUIZ_CLASS_NAME + " = ?";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = null;
        try {
            c = db.rawQuery(selectQuery,  new String[]{ className });

            // looping through all rows and adding to list
            if (c.moveToFirst()) {
                do {
                    long quizId = c.getLong(c.getColumnIndex(QUIZ_ID));
                    List<Question> quizQuestions = getQuizQuestions(quizId);
                    Quiz quiz = new Quiz(
                            quizId,
                            c.getString(c.getColumnIndex(QUIZ_NAME)),
                            quizQuestions,
                            c.getDouble(c.getColumnIndex(QUIZ_MARKS)),
                            c.getInt(c.getColumnIndex(QUIZ_VISIBLE)) == 1
                    );

                    quizzes.add(quiz);
                } while (c.moveToNext());
            }
            return quizzes;
        }
        finally {
            if(c != null)
            {
                c.close();
            }
        }
    }

    // might not be used
    public Quiz getQuiz(long id)
    {
        String selectQuery =
                "SELECT * FROM " + TABLE_QUIZ
                        + " WHERE " + QUIZ_ID + " = ?";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = null;
        try {
            c = db.rawQuery(selectQuery, new String[]{String.valueOf(id)});

            if (c.moveToFirst()) {
                long quizId = c.getLong(c.getColumnIndex(QUIZ_ID));
                List<Question> quizQuestions = getQuizQuestions(quizId);

                Quiz quiz = new Quiz(
                        quizId,
                        c.getString(c.getColumnIndex(QUIZ_NAME)),
                        quizQuestions,
                        c.getDouble(c.getColumnIndex(QUIZ_MARKS)),
                        c.getInt(c.getColumnIndex(QUIZ_VISIBLE)) == 1
                );

                return quiz;
            } else {
                return null;
            }
        }
        finally
        {
            if(c != null)
            {
                c.close();
            }
        }
    }

    public int updateQuiz(Quiz quiz) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(QUIZ_NAME, quiz.getName());
        values.put(QUIZ_MARKS, quiz.getMarks());
        values.put(QUIZ_VISIBLE, quiz.getVisible());

        //update the question list
        clearQuizQuestion(quiz.getQuizId());
        for(Question question : quiz.getQuestions())
        {
            addQuestion(question, quiz.getQuizId());
        }

        // updating row
        return db.update(TABLE_QUIZ, values, QUIZ_ID + " = ?",
                new String[] { String.valueOf(quiz.getQuizId()) });
    }

    public int updateQuizMarks(Quiz quiz)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(QUIZ_MARKS, quiz.getMarks());

        // updating row
        return db.update(TABLE_QUIZ, values, QUIZ_ID + " = ?",
                new String[] { String.valueOf(quiz.getQuizId()) });
    }

    public int updateQuizVisible(Quiz quiz) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(QUIZ_VISIBLE, quiz.getVisible());

        // updating row
        return db.update(TABLE_QUIZ, values, QUIZ_ID + " = ?",
                new String[] { String.valueOf(quiz.getQuizId()) });
    }

    public void deleteQuiz(Quiz quiz) {
        SQLiteDatabase db = this.getWritableDatabase();

        //delete quiz
        db.delete(TABLE_QUIZ, QUIZ_ID + " = ?",
                new String[] { String.valueOf(quiz.getQuizId()) });

    }

    public long getQuizMaxId()
    {
        return getMaxId(QUIZ_ID, TABLE_QUIZ);
    }
    // endregion Quiz Table

    // region Question Table
    public long addQuestion(Question question, long quizId)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(QUESTION_ID, question.getQuestionId());
        values.put(QUESTION_QUIZ_ID, quizId);
        values.put(QUESTION_QUESTION, question.getQuestion());
        values.put(QUESTION_CORRECT_ANSWER, question.getCorrectAnswer());
        values.put(QUESTION_MARKS, question.getTotalMarks());
        values.put(QUESTION_USER_ANSWER, question.getUserAnswer());

        // insert row
        long id = db.insert(TABLE_QUESTION, null, values);

        //insert choice question
        if(question instanceof ChoiceQuestion)
        {
            addChoiceQuestionTable((ChoiceQuestion)question);
        }

        return id;
    }

    public Question getQuestion(long id)
    {
        Question question = null;

        if(existInTable(id, TABLE_CHOICE_QUESTION, CHOICE_QUESTION_ID)) //this is a choice question
        {
            question = getChoiceQuestion(id);
        }
        else
        {
            String selectQuery =
                    "SELECT * FROM " + TABLE_QUESTION
                            + " WHERE " + QUESTION_ID + " =?";

            SQLiteDatabase db = this.getReadableDatabase();

            Cursor c = null;
            try {
                c = db.rawQuery(selectQuery, new String[]{String.valueOf(id)});

                if (c.moveToFirst()) {
                    question = new Question(
                            c.getLong(c.getColumnIndex(QUESTION_ID)),
                            c.getString(c.getColumnIndex(QUESTION_QUESTION)),
                            c.getString(c.getColumnIndex(QUESTION_CORRECT_ANSWER)),
                            c.getInt(c.getColumnIndex(QUESTION_MARKS)),
                            c.getString(c.getColumnIndex(QUESTION_USER_ANSWER))
                    );
                } else {
                    return null;
                }
            }
            finally
            {
                if(c != null)
                {
                    c.close();
                }
            }
        }
        return question;
    }

    // get questions based on quiz id
    public List<Question> getQuizQuestions(long quizId)
    {
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
                    long questionID = c.getLong(c.getColumnIndex(QUESTION_ID));
                    questions.add(getQuestion(questionID));
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

        if(existInTable(question.getQuestionId(), TABLE_CHOICE_QUESTION, CHOICE_QUESTION_ID)) //this is a choice question
        {
            updateChoiceQuestionTable((ChoiceQuestion)question);
        }

        // updating row
        return db.update(TABLE_QUESTION, values, QUESTION_ID + " = ?",
                new String[] { String.valueOf(question.getQuestionId()) });
    }

    // might not be used
    public void deleteQuestion(long id) {
        SQLiteDatabase db = this.getWritableDatabase();

        //delete question
        db.delete(TABLE_QUESTION, QUESTION_ID + " = ?",
                new String[] { String.valueOf(id) });
    }

    // delete all questions that a quiz has
    public void clearQuizQuestion(long quizId)
    {
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
    public long addChoiceQuestionTable(ChoiceQuestion choiceQuestion)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        //Convert the choices to string
        JSONArray jsonArray = new JSONArray(choiceQuestion.getChoices());

        //Store data into choice question table
        ContentValues values = new ContentValues();
        values.put(CHOICE_QUESTION_ID, choiceQuestion.getQuestionId());
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

                ChoiceQuestion choiceQuestion = new ChoiceQuestion(
                        c.getLong(c.getColumnIndex(QUESTION_ID)),
                        c.getString(c.getColumnIndex(QUESTION_QUESTION)),
                        c.getString(c.getColumnIndex(QUESTION_CORRECT_ANSWER)),
                        c.getInt(c.getColumnIndex(QUESTION_MARKS)),
                        c.getString(c.getColumnIndex(QUESTION_USER_ANSWER)),
                        choices
                );

                return choiceQuestion;
            } else {
                return null;
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    public int updateChoiceQuestionTable(ChoiceQuestion choiceQuestion) {
        SQLiteDatabase db = this.getWritableDatabase();

        //Convert the choices to string
        JSONArray jsonArray = new JSONArray(choiceQuestion.getChoices());

        ContentValues values = new ContentValues();
        values.put(CHOICE_QUESTION_CHOICES, jsonArray.toString());

        // updating row
        return db.update(TABLE_CHOICE_QUESTION, values, CHOICE_QUESTION_ID + " = ?",
                new String[] { String.valueOf(choiceQuestion.getQuestionId()) });
    }

    // might not be used cause auto delete
    public void deleteChoiceQuestionTable(long id) {
        SQLiteDatabase db = this.getWritableDatabase();

        //delete quiz
        db.delete(TABLE_CHOICE_QUESTION, CHOICE_QUESTION_ID + " = ?",
                new String[] { String.valueOf(id) });
    }
    // endregion Choice Question Table

    // region Study Material Table
    public long addStudyMaterial(StudyMaterial studyMaterial, String className) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(STUDY_MATERIAL_ID, studyMaterial.getStudyMaterialId());
        values.put(STUDY_MATERIAL_CLASS_NAME, className);
        values.put(STUDY_MATERIAL_NAME, studyMaterial.getName());
        values.put(STUDY_MATERIAL_PATH, studyMaterial.getFile().getPath());
        values.put(STUDY_MATERIAL_VISIBLE, studyMaterial.getVisible());

        // insert row
        return db.insert(TABLE_STUDY_MATERIAL, null, values);
    }

    // get study materials based on class name
    public List<StudyMaterial> getClassStudyMaterials(String className) {
        List<StudyMaterial> studyMaterials = new ArrayList<>();

        String selectQuery =
                "SELECT * FROM " + TABLE_STUDY_MATERIAL
                        + " WHERE " + STUDY_MATERIAL_CLASS_NAME + " =?";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = null;
        try {
            c = db.rawQuery(selectQuery, new String[]{className});

            // looping through all rows and adding to list
            if (c.moveToFirst()) {
                do {
                    StudyMaterial studyMaterial = new StudyMaterial(
                            c.getLong(c.getColumnIndex(STUDY_MATERIAL_ID)),
                            c.getString(c.getColumnIndex(STUDY_MATERIAL_NAME)),
                            c.getString(c.getColumnIndex(STUDY_MATERIAL_PATH)),
                            c.getInt(c.getColumnIndex(STUDY_MATERIAL_VISIBLE)) == 1
                    );
                    studyMaterials.add(studyMaterial);
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

        ContentValues values = new ContentValues();
        values.put(STUDY_MATERIAL_NAME, studyMaterial.getName());
        values.put(STUDY_MATERIAL_PATH, studyMaterial.getFile().getPath());
        values.put(STUDY_MATERIAL_VISIBLE, studyMaterial.getVisible());

        // updating row
        return db.update(TABLE_STUDY_MATERIAL, values, STUDY_MATERIAL_ID + " = ?",
                new String[] { String.valueOf(studyMaterial.getStudyMaterialId()) });
    }

    public int updateStudyMaterialVisible(StudyMaterial studyMaterial) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(STUDY_MATERIAL_VISIBLE, studyMaterial.getVisible());

        // updating row
        return db.update(TABLE_STUDY_MATERIAL, values, STUDY_MATERIAL_ID + " = ?",
                new String[] { String.valueOf(studyMaterial.getStudyMaterialId()) });
    }

    public void deleteStudyMaterial(StudyMaterial studyMaterial) {
        SQLiteDatabase db = this.getWritableDatabase();

        //delete study material
        db.delete(TABLE_STUDY_MATERIAL, STUDY_MATERIAL_ID + " = ?",
                new String[] { String.valueOf(studyMaterial.getStudyMaterialId()) });
    }

    public long getStudyMaterialMaxId() {
        return getMaxId(STUDY_MATERIAL_ID, TABLE_STUDY_MATERIAL);
    }
    // endregion Study Material Table

    // region Helper SQL
    private boolean existInTable(long id, String table, String tableId)
    {
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

    private long getMaxId(String columnID, String table)
    {
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
        finally
        {
            if(c != null)
            {
                c.close();
            }
        }
    }
    // endregion Helper SQL

    // endregion --------------- SQL FUNCTIONS ---------------------------------------
}
