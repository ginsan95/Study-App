package com.sunway.averychoke.studywifidirect3.manager;

import android.content.Context;
import android.support.annotation.Nullable;

import com.sunway.averychoke.studywifidirect3.model.ClassMaterial;
import com.sunway.averychoke.studywifidirect3.model.Quiz;
import com.sunway.averychoke.studywifidirect3.model.StudyMaterial;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by AveryChoke on 2/4/2017.
 */

public class TeacherManager extends BaseManager {

    private static final TeacherManager sInstance = new TeacherManager();

    private final Map<String, Quiz> mQuizMap;
    private final Map<String, StudyMaterial> mStudyMaterialMap;
    private final ReentrantReadWriteLock mQuizLock;
    private final ReentrantReadWriteLock mStudyMaterialLock;

    private TeacherManager() {
        super();
        mQuizMap = new HashMap<>();
        mStudyMaterialMap = new HashMap<>();
        mQuizLock = new ReentrantReadWriteLock();
        mStudyMaterialLock = new ReentrantReadWriteLock();
    }

    public static TeacherManager getInstance() {
        return sInstance;
    }

    @Override
    public void initialize(String className, Context context) {
        super.initialize(className, context);

        // clear data
        mQuizMap.clear();
        mStudyMaterialMap.clear();

        // change the list into map
        for (Quiz quiz : getStudyClass().getQuizzes()) {
            mQuizMap.put(quiz.getName().toLowerCase(), quiz);
        }
        for (StudyMaterial studyMaterial : getStudyClass().getStudyMaterials()) {
            mStudyMaterialMap.put(studyMaterial.getName().toLowerCase(), studyMaterial);
        }
    }

    // region Quiz
    public List<Quiz> getVisibleQuizzes() {
        mQuizLock.readLock().lock();
        try {
            List<Quiz> visibleQuizzes = new ArrayList<>();
            for (Quiz quiz : getQuizzes()) {
                if (quiz.isVisible()) {
                    visibleQuizzes.add(quiz);
                }
            }
            return visibleQuizzes;
        } finally {
            mQuizLock.readLock().unlock();
        }
    }

    public Quiz findQuiz(String name) {
        mQuizLock.readLock().lock();
        try {
            Quiz quiz = mQuizMap.get(name.toLowerCase());
            if (quiz != null && quiz.isVisible()) {
                return quiz;
            } else {
                return null;
            }
        } finally {
            mQuizLock.readLock().unlock();
        }
    }

    public boolean addQuiz(Quiz quiz) {
        if (getDatabase() == null || getStudyClass() == null
                // save the quiz to database
                || getDatabase().addQuiz(quiz, getStudyClass().getName()) == -1) {
            return false;
        }

        mQuizLock.writeLock().lock();
        try {
            getStudyClass().getQuizzes().add(quiz);
            mQuizMap.put(quiz.getName().toLowerCase(), quiz);
            return true;
        } finally {
            mQuizLock.writeLock().unlock();
        }
    }

    public boolean updateQuiz(Quiz quiz, String oldName) {
        quiz.setVersion(quiz.getVersion() + 1);
        if (getDatabase() == null || getStudyClass() == null
                // update the quiz in database
                || getDatabase().updateQuiz(quiz) == -1) {
            return false;
        }

        int index = -1;
        mQuizLock.readLock().lock();
        try {
            index = getStudyClass().getQuizzes().indexOf(quiz);
        } finally {
            mQuizLock.readLock().unlock();
        }

        if (index >= 0) {
            mQuizLock.writeLock().lock();
            try {
                getStudyClass().getQuizzes().set(index, quiz);
                mQuizMap.remove(oldName);
                mQuizMap.put(quiz.getName().toLowerCase(), quiz);
                return true;
            } finally {
                mQuizLock.writeLock().unlock();
            }
        } else {
            return false;
        }
    }

    public void updateQuizVisible(Quiz quiz) {
        if (getStudyClass() != null && getDatabase() != null) {
            getDatabase().updateClassMaterialVisible(quiz);

            mQuizLock.readLock().lock();
            try {
                int index = getStudyClass().getQuizzes().indexOf(quiz);
                getStudyClass().getQuizzes().set(index, quiz);
                mQuizMap.remove(quiz.getName());
                mQuizMap.put(quiz.getName().toLowerCase(), quiz);
            } finally {
                mQuizLock.readLock().unlock();
            }
        }
    }

    @Override
    public void deleteQuiz(Quiz quiz) {
        mQuizLock.writeLock().lock();
        try {
            super.deleteQuiz(quiz);
            mQuizMap.remove(quiz.getName().toLowerCase());
        } finally {
            mQuizLock.writeLock().unlock();
        }
    }

    public boolean isQuizNameConflicting(String newName, @Nullable String oldName) {
        if (oldName != null) {
            mQuizLock.readLock().lock();
            try {
                return !newName.equalsIgnoreCase(oldName) && mQuizMap.containsKey(newName.toLowerCase());
            } finally {
                mQuizLock.readLock().unlock();
            }
        } else {
            return mQuizMap.containsKey(newName.toLowerCase());
        }
    }
    // endregion

    // region Study Material
    public List<String> getVisibleStudyMaterialsName() {
        mStudyMaterialLock.readLock().lock();
        try {
            List<String> visibleNames = new ArrayList<>();
            for (StudyMaterial studyMaterial : getStudyMaterials()) {
                if (studyMaterial.isVisible() && studyMaterial.getStatus() != ClassMaterial.Status.ERROR) {
                    visibleNames.add(studyMaterial.getName());
                }
            }
            return visibleNames;
        } finally {
            mStudyMaterialLock.readLock().unlock();
        }
    }

    public StudyMaterial findStudyMaterial(String name) {
        mStudyMaterialLock.readLock().lock();
        try {
            StudyMaterial studyMaterial = mStudyMaterialMap.get(name.toLowerCase());
            if (studyMaterial != null && studyMaterial.isVisible() && studyMaterial.getStatus() != ClassMaterial.Status.ERROR) {
                return studyMaterial;
            } else {
                return null;
            }
        } finally {
            mStudyMaterialLock.readLock().unlock();
        }
    }

    public boolean addStudyMaterial(StudyMaterial studyMaterial) {
        if (getDatabase() == null || getStudyClass() == null
                // save the study material to database
                || getDatabase().addStudyMaterial(studyMaterial, getStudyClass().getName()) == -1) {
            return false;
        }

        mStudyMaterialLock.writeLock().lock();
        try {
            getStudyClass().getStudyMaterials().add(studyMaterial);
            mStudyMaterialMap.put(studyMaterial.getName().toLowerCase(), studyMaterial);
            return true;
        } finally {
            mStudyMaterialLock.writeLock().unlock();
        }
    }

    public void updateStudyMaterialVisible(StudyMaterial studyMaterial) {
        if (getStudyClass() != null && getDatabase() != null) {
            getDatabase().updateClassMaterialVisible(studyMaterial);

            mStudyMaterialLock.readLock().lock();
            try {
                int index = getStudyClass().getStudyMaterials().indexOf(studyMaterial);
                if (index >= 0) {
                    getStudyClass().getStudyMaterials().set(index, studyMaterial);
                    mStudyMaterialMap.put(studyMaterial.getName().toLowerCase(), studyMaterial);
                }
            } finally {
                mStudyMaterialLock.readLock().unlock();
            }
        }
    }

    public boolean renameStudyMaterial(StudyMaterial studyMaterial, String newName) {
        if (getStudyClass() != null && getDatabase() != null) {
            // get index
            int index = -1;
            mStudyMaterialLock.readLock().lock();
            try {
                index = getStudyClass().getStudyMaterials().indexOf(studyMaterial);
            } finally {
                mStudyMaterialLock.readLock().unlock();
            }

            StudyMaterial existingStudyMaterial = mStudyMaterialMap.get(newName.toLowerCase());
            File newFile = new File(studyMaterial.getFile().getParent(), newName);
            boolean success = (existingStudyMaterial == null || studyMaterial.equals(existingStudyMaterial))
                    && studyMaterial.getFile().renameTo(newFile);

            if (index >= 0 && success) {
                // rename
                String oldName = studyMaterial.getName();
                studyMaterial.setName(newName);
                studyMaterial.setFile(newFile);

                // change data
                mStudyMaterialLock.writeLock().lock();
                try {
                    getDatabase().updateStudyMaterial(studyMaterial);
                    getStudyClass().getStudyMaterials().set(index, studyMaterial);
                    mStudyMaterialMap.remove(oldName.toLowerCase());
                    mStudyMaterialMap.put(newName.toLowerCase(), studyMaterial);
                    return true;
                } finally {
                    mStudyMaterialLock.writeLock().unlock();
                }
            }
        }
        return false;
    }

    @Override
    public void deleteStudyMaterial(StudyMaterial studyMaterial) {
        mStudyMaterialLock.writeLock().lock();
        try {
            super.deleteStudyMaterial(studyMaterial);
            mStudyMaterialMap.remove(studyMaterial.getName().toLowerCase());
        } finally {
            mStudyMaterialLock.writeLock().unlock();
        }
    }
    // endregion

    // region Get Set

    // endregion
}
