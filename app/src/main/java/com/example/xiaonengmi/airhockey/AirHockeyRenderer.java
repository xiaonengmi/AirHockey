package com.example.xiaonengmi.airhockey;


import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.airhockey.android.objects.Mallet;
import com.airhockey.android.objects.Table;
import com.airhockey.android.programs.ColorShaderProgram;
import com.airhockey.android.programs.TextureShaderProgram;
import com.airhockey.android.util.MatrixHelper;
import com.airhockey.android.util.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by xinchen on 2017/1/9.
 */

public class AirHockeyRenderer implements GLSurfaceView.Renderer {

    private final Context context;

    private final float[] projectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];

    private Table table;
    private Mallet mallet;

    private TextureShaderProgram textureProgram;
    private ColorShaderProgram colorProgram;

    private int texture;

    public AirHockeyRenderer(Context context)
    {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glClearColor(0f, 0f, 0f, 0f);

        table = new Table();
        mallet = new Mallet();

        textureProgram = new TextureShaderProgram(context);
        colorProgram = new ColorShaderProgram(context);

        texture = TextureHelper.loadTexture(context, R.drawable.air_hockey_surface);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

//        // orthographic projection
//        final float ratio = width > height ? (float) width / (float) height : (float) height / (float) width;
//        if (width > height)
//            Matrix.orthoM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, -1f, 1f);
//        else
//            Matrix.orthoM(projectionMatrix, 0, -1f, 1f, -ratio, ratio, -1f, 1f);

        // perspective projection
        MatrixHelper.perspectiveM(projectionMatrix, 45, (float)width / (float)height, 1f, 10f);

        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, 0f, 0f, -3f);
        Matrix.rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f);

//        Matrix.translateM(modelMatrix, 0, 0.4f, 0f, 0f);
//        Matrix.rotateM(modelMatrix, 0, 90f, 0f, 0f, 1f);

//        Matrix.translateM(modelMatrix, 0, 0f, -0.8f, -4f);
//        Matrix.rotateM(modelMatrix, 0, -90f, 1f, 0f, 0f);

        final float[] temp = new float[16];
        Matrix.multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
        System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        textureProgram.useProgram();
        textureProgram.setUniforms(projectionMatrix, texture);
        table.bindData(textureProgram);
        table.draw();

        colorProgram.useProgram();
        colorProgram.setUniforms(projectionMatrix);
        mallet.bindData(colorProgram);
        mallet.draw();
    }
}
