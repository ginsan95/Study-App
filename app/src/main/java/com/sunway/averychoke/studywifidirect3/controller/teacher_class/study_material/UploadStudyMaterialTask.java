package com.sunway.averychoke.studywifidirect3.controller.teacher_class.study_material;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.sunway.averychoke.studywifidirect3.manager.BaseManager;
import com.sunway.averychoke.studywifidirect3.manager.TeacherManager;
import com.sunway.averychoke.studywifidirect3.model.StudyMaterial;
import com.sunway.averychoke.studywifidirect3.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by AveryChoke on 13/4/2017.
 */

public class UploadStudyMaterialTask extends AsyncTask<Uri, Void, List<StudyMaterial>> {

    public interface UploadListener {
        void onUploadCompleted(@NonNull List<StudyMaterial> studyMaterials);
    }

    private TeacherManager sManager;
    private Context mContext;
    private UploadListener mListener;

    public UploadStudyMaterialTask(Context context, UploadListener listener) {
        mContext = context;
        mListener = listener;
        sManager = TeacherManager.getInstance();
    }

    @Override
    protected List<StudyMaterial> doInBackground(Uri... uris) {
        List<StudyMaterial> studyMaterials = new ArrayList<>();
        File baseFile = new File(BaseManager.STUDY_MATERIALS_PATH + sManager.getClassName());
        if (!baseFile.exists()) {
            baseFile.mkdirs();
        }

        for (Uri uri : uris) {
            String name = FileUtil.getFileName(mContext, uri);
            // copy the study material to my specific folder
            if (name != null) {
                File destFile = new File(getStudyMaterialClassPath(changeName(name)));
                try {
                    FileUtil.copyFile(mContext, uri, destFile);
                    StudyMaterial studyMaterial = new StudyMaterial(destFile.getName(), destFile.getPath());
                    sManager.addStudyMaterial(studyMaterial);
                    studyMaterials.add(studyMaterial);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return studyMaterials;
    }

    @Override
    protected void onPostExecute(List<StudyMaterial> studyMaterials) {
        super.onPostExecute(studyMaterials);
        mListener.onUploadCompleted(studyMaterials);
    }

    private String changeName(String name) {
        String changedName = name;
        int count = 1;
        while((new File(getStudyMaterialClassPath(changedName)).exists())) {
            int extIndex = name.lastIndexOf(".");
            if (extIndex > 0) {
                changedName = name.substring(0, extIndex) + " (" + (count++) + ")" + name.substring(extIndex);
            } else {
                changedName = name + " (" + (count++) + ")";
            }
        }
        return changedName;
    }

    private String getStudyMaterialClassPath(String name) {
        return BaseManager.STUDY_MATERIALS_PATH + sManager.getClassName() + File.separator + name;
    }
}
