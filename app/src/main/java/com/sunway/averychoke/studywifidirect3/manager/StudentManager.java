package com.sunway.averychoke.studywifidirect3.manager;

import android.content.Context;

import com.sunway.averychoke.studywifidirect3.controller.connection.ClassMaterialsRequestTask;
import com.sunway.averychoke.studywifidirect3.model.ClassMaterial;
import com.sunway.averychoke.studywifidirect3.model.DeviceClass;
import com.sunway.averychoke.studywifidirect3.model.Quiz;

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

    public void updateQuizzes(List<Quiz> quizzes) {
        if (getStudyClass() == null && getDatabase() == null) {
            return;
        }

        // update current data
        for (Quiz quiz : quizzes) {
            int index = getQuizIndex(quiz);
            if (index == -1) {
                quiz.updateId();
                getStudyClass().getQuizzes().add(quiz);
            } else if(getStudyClass().getQuizzes().get(index).getVersion() != quiz.getVersion()) {
                quiz.setStatus(ClassMaterial.Status.CONFLICT);
            }
        }

        // update database
        getDatabase().updateClassQuizzes(getStudyClass());
    }

    // used for conflict or if user want to dl a new version
    public Quiz updateQuiz(Quiz quiz) {
        if (getStudyClass() == null && getDatabase() == null) {
            return null;
        }

        int index = getQuizIndex(quiz);
        if (index != -1) {
            quiz.updateQuestionsId();
            getStudyClass().getQuizzes().set(index, quiz);
        }

        getDatabase().updateQuiz(quiz);
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
