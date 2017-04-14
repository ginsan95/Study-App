package com.sunway.averychoke.studywifidirect3.controller.connection;

import android.os.AsyncTask;

import com.sunway.averychoke.studywifidirect3.controller.SWDBaseActivity;
import com.sunway.averychoke.studywifidirect3.manager.BaseManager;
import com.sunway.averychoke.studywifidirect3.manager.StudentManager;
import com.sunway.averychoke.studywifidirect3.model.ClassMaterial;
import com.sunway.averychoke.studywifidirect3.model.Quiz;

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

public class ClassMaterialsRequestTask extends AsyncTask<Serializable, Void, ClassMaterialsRequestTask.Result> {

    public enum Result {
        QUIZZES, QUIZ, STUDY_MATERIALS, STUDY_MATERIAL, ERROR;
    }

    private String mAddress;
    private ClassMaterialsUpdaterListener mListener;
    private ClassMaterial mDownloadClassMaterial; // used for failed download handling
    private Socket mSocket;
    private StudentManager sManager;

    private Exception mError;
    private ClassMaterial mClassMaterial;

    public ClassMaterialsRequestTask(String address, ClassMaterialsUpdaterListener listener) {
        this(address, listener, null);
    }

    public ClassMaterialsRequestTask(String address, ClassMaterialsUpdaterListener listener, ClassMaterial downloadClassMaterial) {
        mAddress = address;
        mListener = listener;
        mDownloadClassMaterial = downloadClassMaterial;
        sManager = StudentManager.getInstance();
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
        } catch (IOException | ClassNotFoundException | IllegalArgumentException e) {
            return giveError(e);
        } finally {
            disconnect();
            sManager.removeTask(this);
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
                        mListener.onError(new DownloadException(mDownloadClassMaterial));
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

    private void updateManager(Result result, ObjectInputStream ois) throws IOException, ClassNotFoundException {
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
                break;
            case STUDY_MATERIAL:
                break;
            case ERROR:
                break;
        }
    }

    private Result giveError(Exception e) {
        mError = e;
        return Result.ERROR;
    }

    public void disconnect() {
        if (mSocket != null && mSocket.isConnected()) {
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
