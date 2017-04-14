package com.sunway.averychoke.studywifidirect3.manager;

import android.content.Context;

import com.sunway.averychoke.studywifidirect3.controller.connection.ClassMaterialsRequestTask;
import com.sunway.averychoke.studywifidirect3.model.ClassMaterial;
import com.sunway.averychoke.studywifidirect3.model.DeviceClass;
import com.sunway.averychoke.studywifidirect3.model.Quiz;
import com.sunway.averychoke.studywifidirect3.model.StudyMaterial;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AveryChoke on 2/4/2017.
 */

public class StudentManager extends BaseManager {

    private static final StudentManager sInstance = new StudentManager();

    private DeviceClass mDeviceClass;
    private String mTeacherAddress;
    private boolean mOffline;
    private List<ClassMaterialsRequestTask> mTasks;

    private StudentManager() {
        super();
        mTasks = new ArrayList<>();
    }

    public static StudentManager getInstance() {
        return sInstance;
    }

    public void initialize(String className, Context context, boolean offline) {
        super.initialize(className, context);
        mOffline = offline;
    }

    // region Quiz
    public boolean updateQuizAnswer(Quiz quiz) {
        if (getDatabase() == null || getStudyClass() == null
                || getDatabase().updateQuizAnswers(quiz) == -1) {
            return false;
        }

        int index = getStudyClass().getQuizzes().indexOf(quiz);
        getStudyClass().getQuizzes().set(index, quiz);

        return true;
    }

    public void updateQuizStatus(Quiz quiz, ClassMaterial.Status status) {
        if (getStudyClass() != null) {
            int index = getStudyClass().getQuizzes().indexOf(quiz);
            quiz.setStatus(status);
            getStudyClass().getQuizzes().set(index, quiz);
        }
    }

    public void updateQuizzes(List<Quiz> quizzes) {
        if (getStudyClass() == null || getDatabase() == null) {
            return;
        }

        // update current data
        for (Quiz quiz : quizzes) {
            int index = getQuizIndex(quiz);
            if (index == -1) {
                quiz.updateId();
                getStudyClass().getQuizzes().add(quiz);
            } else if(getStudyClass().getQuizzes().get(index).getVersion() != quiz.getVersion()) {
                getStudyClass().getQuizzes().get(index).setStatus(ClassMaterial.Status.ERROR);
            }
        }

        // update database
        getDatabase().updateClassQuizzes(getStudyClass());
    }

    // used for conflict or if user want to dl a new version
    public Quiz updateQuiz(Quiz quiz) {
        if (getStudyClass() == null || getDatabase() == null || quiz == null) {
            return null;
        }

        int index = getQuizIndex(quiz);
        if (index != -1) {
            Quiz myQuiz = getStudyClass().getQuizzes().get(index);
            myQuiz.update(quiz);
            getDatabase().updateQuiz(myQuiz);
            return myQuiz;
        }
        return quiz;
    }
    
    private int getQuizIndex(Quiz quiz) {
        if (getStudyClass() != null) {
            for (int i = 0; i < getStudyClass().getQuizzes().size(); i++) {
                if (getStudyClass().getQuizzes().get(i).getName().equalsIgnoreCase(quiz.getName())) {
                    return i;
                }
            }
        }
        return -1;
    }
    // endregion

    // region Study Material
    public void updateStudyMaterials(List<String> studyMaterialsName) {
        if (getStudyClass() == null || getDatabase() == null) {
            return;
        }

        // update current data
        for (String name : studyMaterialsName) {
            int index = getStudyMaterialIndex(name);
            if (index == -1) {
                getStudyClass().getStudyMaterials().add(new StudyMaterial(name));
            }
        }
    }

    // download a single study material
    public StudyMaterial updateStudyMaterial(StudyMaterial studyMaterial) {
        if (getStudyClass() == null || getDatabase() == null || studyMaterial == null) {
            return null;
        }

        int index = getStudyMaterialIndex(studyMaterial.getName());
        if (index != -1) {
            StudyMaterial myStudyMaterial = getStudyClass().getStudyMaterials().get(index);
            myStudyMaterial.update(studyMaterial);
            getDatabase().updateStudyMaterial(myStudyMaterial);
            return myStudyMaterial;
        }
        return studyMaterial;
    }

    public void updateStudyMaterialStatus(StudyMaterial studyMaterial, ClassMaterial.Status status) {
        if (getStudyClass() != null) {
            int index = getStudyClass().getStudyMaterials().indexOf(studyMaterial);
            studyMaterial.setStatus(status);
            getStudyClass().getStudyMaterials().set(index, studyMaterial);
        }
    }

    private int getStudyMaterialIndex(String name) {
        if (getStudyClass() != null) {
            for (int i = 0; i < getStudyClass().getStudyMaterials().size(); i++) {
                if (getStudyClass().getStudyMaterials().get(i).getName().equalsIgnoreCase(name)) {
                    return i;
                }
            }
        }
        return -1;
    }
    // endregion

    // region Others
    public synchronized void addTask(ClassMaterialsRequestTask task) {
        mTasks.add(task);
    }

    public synchronized void removeTask(ClassMaterialsRequestTask task) {
        mTasks.remove(task);
    }

    public synchronized void killAllTasks() {
        for (ClassMaterialsRequestTask mTask : mTasks) {
            mTask.disconnect();
            mTask.cancel(true);
        }
        mTasks.clear();
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

    public boolean isOffline() {
        return mOffline;
    }

    public void setOffline(boolean offline) {
        mOffline = offline;
    }
    // endregion
}
