package com.sunway.averychoke.studywifidirect3.manager;

import android.content.Context;

import com.sunway.averychoke.studywifidirect3.database.DatabaseHelper;
import com.sunway.averychoke.studywifidirect3.model.ClassMaterial;
import com.sunway.averychoke.studywifidirect3.model.DeviceClass;
import com.sunway.averychoke.studywifidirect3.model.Quiz;
import com.sunway.averychoke.studywifidirect3.model.StudyClass;
import com.sunway.averychoke.studywifidirect3.model.StudyMaterial;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AveryChoke on 2/4/2017.
 */

public class StudentManager {

    private static final StudentManager sInstance = new StudentManager();

    private DatabaseHelper mDatabase;
    private StudyClass mStudyClass;
    private DeviceClass mDeviceClass;
    private String mTeacherAddress;

    private StudentManager() {}

    public static StudentManager getInstance() {
        return sInstance;
    }

    public void initialize(String className, Context context) {
        mDatabase = new DatabaseHelper(context);

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

    public boolean updateQuizAnswer(Quiz quiz) {
        if (mDatabase == null || mStudyClass == null
                || mDatabase.updateQuizAnswers(quiz) == -1) {
            return false;
        }

        int index = mStudyClass.getQuizzes().indexOf(quiz);
        mStudyClass.getQuizzes().set(index, quiz);

        return true;
    }

    public void deleteQuiz(Quiz quiz) {
        if (mDatabase != null && mStudyClass != null) {
            mDatabase.deleteClassMaterial(quiz);
            mStudyClass.getQuizzes().remove(quiz);
        }
    }

    public void updateQuizzes(List<Quiz> quizzes) {
        if (mStudyClass == null && mDatabase == null) {
            return;
        }

        // update current data
        for (Quiz quiz : quizzes) {
            int index = getQuizIndex(quiz);
            if (index == -1) {
                quiz.updateId();
                mStudyClass.getQuizzes().add(quiz);
            } else if(mStudyClass.getQuizzes().get(index).getVersion() != quiz.getVersion()) {
                quiz.setStatus(ClassMaterial.Status.CONFLICT);
            }
        }

        // update database
        mDatabase.updateClassQuizzes(mStudyClass);
    }

    // used for conflict or if user want to dl a new version
    public Quiz updateQuiz(Quiz quiz) {
        if (mStudyClass == null && mDatabase == null) {
            return null;
        }

        int index = getQuizIndex(quiz);
        if (index != -1) {
            quiz.updateQuestionsId();
            mStudyClass.getQuizzes().set(index, quiz);
        }

        mDatabase.updateQuiz(quiz);
        return quiz;
    }
    
    private int getQuizIndex(Quiz quiz) {
        if (mStudyClass != null) {
            for (int i = 0; i < mStudyClass.getQuizzes().size(); i++) {
                if (mStudyClass.getQuizzes().get(i).getName().equalsIgnoreCase(quiz.getName())) {
                    return i;
                }
            }
        }
        return -1;
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
        }
    }
    // endregion
    
    // region Get Set
    public DeviceClass getDeviceClass() {
        return mDeviceClass;
    }

    public void setDeviceClass(DeviceClass deviceClass) {
        mDeviceClass = deviceClass;
    }

    public String getTeacherAddress() {
        return mTeacherAddress;
    }

    public void setTeacherAddress(String teacherAddress) {
        mTeacherAddress = teacherAddress;
    }
    // endregion
}
