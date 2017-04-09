package com.sunway.averychoke.studywifidirect3.controller.connection;

import android.os.AsyncTask;

import com.sunway.averychoke.studywifidirect3.controller.SWDBaseActivity;
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

public class SendReceiveTask extends AsyncTask<Serializable, Void, SendReceiveTask.Result> {

    public enum Result {
        QUIZZES, QUIZ, STUDY_MATERIALS, STUDY_MATERIAL, ERROR;
    }

    private String mAddress;
    private ClassMaterialsUpdaterListener mListener;
    private StudentManager sManager;

    private Exception mError;
    private ClassMaterial mClassMaterial;

    public SendReceiveTask(String address, ClassMaterialsUpdaterListener listener) {
        mAddress = address;
        mListener = listener;
        sManager = StudentManager.getInstance();
    }

    @Override
    protected Result doInBackground(Serializable... objects) {
        if (objects.length <= 0) {
            return giveError(null);
        }

        Socket socket = null;

        try {
            socket = new Socket();
            InetSocketAddress address = new InetSocketAddress(mAddress, SWDBaseActivity.APP_PORT_NUMBER);
            // Connect to the host for 5 sec
            socket.connect(address, 5000);

            // Send request to teacher
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            for (Serializable object : objects) {
                oos.writeObject(object);
            }
            oos.flush();

            // Receive data from teacher
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Result result = (Result) ois.readObject();
            updateManager(result, ois);
            ois.close();

            return result;
        } catch (IOException | ClassNotFoundException e) {
            return giveError(e);
        } finally {
            if (socket != null && socket.isConnected()) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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
}
