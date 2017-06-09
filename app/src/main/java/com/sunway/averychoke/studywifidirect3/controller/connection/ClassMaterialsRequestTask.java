package com.sunway.averychoke.studywifidirect3.controller.connection;

import android.os.AsyncTask;

import com.sunway.averychoke.studywifidirect3.controller.SWDBaseActivity;
import com.sunway.averychoke.studywifidirect3.manager.BaseManager;
import com.sunway.averychoke.studywifidirect3.manager.StudentManager;
import com.sunway.averychoke.studywifidirect3.model.ClassMaterial;
import com.sunway.averychoke.studywifidirect3.model.Quiz;
import com.sunway.averychoke.studywifidirect3.model.StudyMaterial;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

/**
 * Created by AveryChoke on 9/4/2017.
 */

public class ClassMaterialsRequestTask extends AsyncTask<Serializable, Long, ClassMaterialsRequestTask.Result> {

    public enum Result {
        QUIZZES, QUIZ, STUDY_MATERIALS, STUDY_MATERIAL, ERROR;
    }

    private static final int BUFFER_SIZE = 4 * 1024;

    private String mAddress;
    private ClassMaterialsUpdaterListener mListener;
    private ClassMaterialProgressListener mProgressListener;
    private ClassMaterial mDownloadClassMaterial;  // the request material
    private ClassMaterial.Status mDownloadClassMaterialStatus; // status for download exception
    private Socket mSocket;
    private StudentManager sManager;

    private Exception mError;
    private ClassMaterial mClassMaterial;

    public ClassMaterialsRequestTask(String address, ClassMaterialsUpdaterListener listener) {
        this(address, listener, null, null);
    }

    public ClassMaterialsRequestTask(String address, ClassMaterialsUpdaterListener listener, ClassMaterial downloadClassMaterial) {
        this(address, listener, downloadClassMaterial, null);
    }

    public ClassMaterialsRequestTask(String address, ClassMaterialsUpdaterListener listener, ClassMaterial downloadClassMaterial,
                                     ClassMaterialProgressListener progressListener) {
        mAddress = address;
        mListener = listener;
        mDownloadClassMaterial = downloadClassMaterial;
        if (downloadClassMaterial != null) {
            mDownloadClassMaterialStatus = downloadClassMaterial.getStatus();
        }
        sManager = StudentManager.getInstance();
        mProgressListener = progressListener;
    }

    @Override
    protected Result doInBackground(Serializable... objects) {
        if (objects.length <= 0) {
            return giveError(null);
        }
        sManager.addTask(this);

        try {
            mSocket = new Socket();
            InetSocketAddress address = new InetSocketAddress(mAddress, BaseManager.APP_PORT_NUMBER);
            // Connect to the host for 5 sec
            mSocket.connect(address, 5000);

            // Send request to teacher
            ObjectOutputStream oos = new ObjectOutputStream(mSocket.getOutputStream());
            for (Serializable object : objects) {
                oos.writeObject(object);
            }
            oos.flush();

            // Receive data from teacher
            ObjectInputStream ois = new ObjectInputStream(mSocket.getInputStream());
            Result result = (Result) ois.readObject();
            updateManager(result, ois);
            ois.close();

            return result;
        } catch (IOException | ClassNotFoundException | IllegalArgumentException | DownloadException | NullPointerException e) {
            return giveError(e);
        } finally {
            disconnect();
            sManager.removeTask(this);
        }
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
        if (mProgressListener != null && values.length == 2) {
            Double progress = values[1] * 100.0 / values[0];
            mProgressListener.onClassMaterialProgress(progress.intValue());
        }
    }

    @Override
    protected void onPostExecute(Result result) {
        super.onPostExecute(result);

        if (mListener != null) {
            switch (result) {
                case QUIZZES:
                case STUDY_MATERIALS:
                    mListener.onClassMaterialsUpdated();
                    break;
                case QUIZ:
                case STUDY_MATERIAL:
                    if (mClassMaterial != null) {
                        mListener.onClassMaterialUpdated(mClassMaterial);
                    } else if (mDownloadClassMaterial != null) {
                        mListener.onError(new DownloadException(mDownloadClassMaterial, mDownloadClassMaterialStatus));
                    } else {
                        mListener.onError(mError);
                    }
                    break;
                case ERROR:
                    mListener.onError(mError);
                    break;
            }
        }
    }

    private void updateManager(Result result, ObjectInputStream ois) throws IOException, ClassNotFoundException, DownloadException, NullPointerException {
        switch (result) {
            case QUIZZES:
                List<Quiz> quizzes = (List<Quiz>) ois.readObject();
                sManager.updateQuizzes(quizzes);
                break;
            case QUIZ:
                Quiz quiz = (Quiz) ois.readObject();
                mClassMaterial = sManager.updateQuiz(quiz);
                break;
            case STUDY_MATERIALS:
                List<String> studyMaterialsName = (List<String>) ois.readObject();
                sManager.updateStudyMaterials(studyMaterialsName);
                break;
            case STUDY_MATERIAL:
                if (mDownloadClassMaterial != null && mDownloadClassMaterial instanceof StudyMaterial) {
                    StudyMaterial studyMaterial = (StudyMaterial) mDownloadClassMaterial;
                    File baseFile = new File(BaseManager.STUDY_MATERIALS_PATH + sManager.getClassName());
                    if (!baseFile.exists()) {
                        baseFile.mkdirs();
                    }

                    // get file size
                    long fileSize = ois.readLong();

                    // download the file from teacher
                    BufferedOutputStream bos = null;
                    File file = null;
                    try {
                        byte[] buffer = new byte[BUFFER_SIZE];
                        file = new File(baseFile, studyMaterial.getName());
                        bos = new BufferedOutputStream(new FileOutputStream(file));
                        int len = 0;
                        BufferedInputStream bis = new BufferedInputStream(ois);
                        while ((len = bis.read(buffer)) > 0) {
                            bos.write(buffer, 0, len);
                            // publish progress
                            publishProgress(fileSize, file.length());
                        }
                    } finally {
                        if (bos != null) {
                            bos.close();
                        }
                    }

                    // check if downloaded the file correctly
                    if (file != null && file.length() == fileSize) {
                        publishProgress(fileSize, file.length());
                        studyMaterial.setFile(file);
                        mClassMaterial = sManager.updateStudyMaterial(studyMaterial);
                    } else {
                        if (file.exists()) {
                            file.delete();
                        }
                        throw new DownloadException(mDownloadClassMaterial, mDownloadClassMaterialStatus);
                    }
                }
                break;
            case ERROR:
                if (mDownloadClassMaterial != null) {
                    mError = new DownloadException(mDownloadClassMaterial, mDownloadClassMaterialStatus);
                }
                break;
        }
    }

    private Result giveError(Exception e) {
        mError = mDownloadClassMaterial != null ? new DownloadException(mDownloadClassMaterial, mDownloadClassMaterialStatus) : e;
        return Result.ERROR;
    }

    public void disconnect() {
        if (mSocket != null && !mSocket.isClosed()) {
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
