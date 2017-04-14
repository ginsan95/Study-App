package com.sunway.averychoke.studywifidirect3.controller.connection;

import com.sunway.averychoke.studywifidirect3.controller.SWDBaseActivity;
import com.sunway.averychoke.studywifidirect3.manager.BaseManager;
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
        mSocket = new ServerSocket(BaseManager.APP_PORT_NUMBER);
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
                                sendQuiz(socket, ois);
                                break;
                            case STUDY_MATERIALS:
                                sendStudyMaterialsName(socket);
                                break;
                            case STUDY_MATERIAL:
                                break;
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                        if (socket.isConnected()) {
                            try {
                                socket.close();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
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
        oos.writeObject(ClassMaterialsRequestTask.Result.QUIZZES);
        oos.writeObject(sManager.getVisibleQuizzes());
        oos.flush();
    }

    private void sendQuiz(Socket socket, ObjectInputStream ois) throws IOException, ClassNotFoundException {
        String quizName = (String) ois.readObject();

        // might send null
        Quiz quiz = sManager.findQuiz(quizName);
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(ClassMaterialsRequestTask.Result.QUIZ);
        oos.writeObject(quiz);
        oos.flush();
    }

    private void sendStudyMaterialsName(Socket socket) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(ClassMaterialsRequestTask.Result.STUDY_MATERIALS);
        oos.writeObject(sManager.getVisibleStudyMaterialsName());
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
