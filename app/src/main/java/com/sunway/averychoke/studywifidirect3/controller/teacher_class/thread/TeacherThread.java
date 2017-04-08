package com.sunway.averychoke.studywifidirect3.controller.teacher_class.thread;

import com.sunway.averychoke.studywifidirect3.controller.SWDBaseActivity;
import com.sunway.averychoke.studywifidirect3.manager.TeacherManager;
import com.sunway.averychoke.studywifidirect3.model.Quiz;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by AveryChoke on 8/4/2017.
 */

public class TeacherThread implements Runnable {
    public enum Request {
        QUIZZES, QUIZ, STUDY_MATERIALS, STUDY_MATERIAL;
    }

    private final ServerSocket mSocket;
    private TeacherManager sManager;

    public TeacherThread() throws IOException {
        mSocket = new ServerSocket(SWDBaseActivity.APP_PORT_NUMBER);
        sManager = TeacherManager.getInstance();
    }

    @Override
    public void run() {
        try {
            while (true) {
                try {
                    // wait for student to connect
                    Socket socket = mSocket.accept();

                    try {
                        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                        Request requestCode = (Request) ois.readObject();

                        switch (requestCode) {
                            case QUIZZES:
                                sendQuizzes(socket);
                                break;
                            case QUIZ:
                                sendQuiz(socket);
                                break;
                            case STUDY_MATERIALS:
                                break;
                            case STUDY_MATERIAL:
                                break;
                        }

                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        } finally {
            disconnect();
        }
    }

    // region request methods
    private void sendQuizzes(Socket socket) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(sManager.getQuizzes());
        oos.flush();
    }

    private void sendQuiz(Socket socket) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        String quizName = (String) ois.readObject();

        // might send null
        Quiz quiz = sManager.findQuiz(quizName);
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(quiz);
        oos.flush();
    }
    // endregion

    public void disconnect() {
        if (mSocket != null) {
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
