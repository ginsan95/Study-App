package com.sunway.averychoke.studywifidirect3.manager;

import android.content.Context;
import android.os.Environment;

import com.sunway.averychoke.studywifidirect3.database.DatabaseHelper;
import com.sunway.averychoke.studywifidirect3.model.Quiz;
import com.sunway.averychoke.studywifidirect3.model.StudyClass;
import com.sunway.averychoke.studywifidirect3.model.StudyMaterial;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by AveryChoke on 10/4/2017.
 */

public class BaseManager {

    private static final BaseManager sInstance = new BaseManager();
    public static final String APP_ID = "swd_study_app";
    public static final int APP_PORT_NUMBER = 6767;
    public static final String STUDY_MATERIALS_PATH = Environment.getExternalStorageDirectory() + "/Study App/Study materials/";

    private DatabaseHelper mDatabase;
    private StudyClass mStudyClass;
    private Context mContext;

    protected BaseManager() {}

    public static BaseManager getInstance() {
        return sInstance;
    }

    public void initialize(String className, Context context) {
        mDatabase = new DatabaseHelper(context);
        mContext = context;

        StudyClass studyClass = mDatabase.getClass(className);
        if (studyClass != null) {
            mStudyClass = studyClass;
        } else {
            mStudyClass = new StudyClass(className);
            mDatabase.addClass(mStudyClass);
        }
    }

    public String getClassName() {
        return mStudyClass != null ? mStudyClass.getName() : "";
    }

    // region Quiz
    public List<Quiz> getQuizzes() {
        return mStudyClass != null ?  mStudyClass.getQuizzes() : new ArrayList<Quiz>();
    }

    public void deleteQuiz(Quiz quiz) {
        if (mDatabase != null && mStudyClass != null) {
            mDatabase.deleteClassMaterial(quiz);
            mStudyClass.getQuizzes().remove(quiz);
        }
    }
    // endregion

    // region Study Material
    public List<StudyMaterial> getStudyMaterials() {
        return mStudyClass != null ? mStudyClass.getStudyMaterials() : new ArrayList<StudyMaterial>();
    }

    public void deleteStudyMaterial(StudyMaterial studyMaterial) {
        if (mDatabase != null && mStudyClass != null) {
            mDatabase.deleteClassMaterial(studyMaterial);
            mStudyClass.getStudyMaterials().remove(studyMaterial);
            new File(STUDY_MATERIALS_PATH + getClassName() + File.separator + studyMaterial.getName()).delete();
        }
    }
    // endregion

    // region Get Set
    protected DatabaseHelper getDatabase() {
        return mDatabase;
    }

    protected void setDatabase(DatabaseHelper database) {
        mDatabase = database;
    }

    protected StudyClass getStudyClass() {
        return mStudyClass;
    }

    protected void setStudyClass(StudyClass studyClass) {
        mStudyClass = studyClass;
    }

    protected Context getContext() {
        return mContext;
    }

    protected void setContext(Context context) {
        mContext = context;
    }
    // endregion
}
