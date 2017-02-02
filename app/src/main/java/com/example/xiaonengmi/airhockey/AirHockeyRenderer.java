package com.example.xiaonengmi.airhockey;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.airhockey.android.util.LoggerHelper;
import com.airhockey.android.util.MatrixHelper;
import com.airhockey.android.util.ShaderHelper;
import com.airhockey.android.util.TextResourceReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by xinchen on 2017/1/9.
 */

public class AirHockeyRenderer implements GLSurfaceView.Renderer {

    private static final int BYTES_PER_FLOAT = 4;
    private static final int POSITION_COMPONENT_COUNT = 2;
//    private static final int POSITION_COMPONENT_COUNT = 4;
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;

//    private static final String  = "u_Color";
//    private int uColorLocation;
    private static final String A_POSITION = "a_Position";
    private int aPositionLocation;
    private static final String A_COLOR = "a_Color";
    private int aColorLocation;
    private static final String U_MATRIX = "u_Matrix";
    private int uMatrixLocation;
    private final float[] projectionMatrix = new float[16];

    private final float[] modelMatrix = new float[16];

    private Context context;

    private FloatBuffer vertexData;
    private int program;

    public AirHockeyRenderer(Context context)
    {
        this.context = context;

        float[] tableVertices = {
                // triangle fan
                0f,     0f,            1f, 1f, 1f,
                -0.5f,  -0.8f,         0.7f, 0.7f, 0.7f,
                0.5f,   -0.8f,         0.7f, 0.7f, 0.7f,
                0.5f,   0.8f,          0.7f, 0.7f, 0.7f,
                -0.5f,  0.8f,          0.7f, 0.7f, 0.7f,
                -0.5f,  -0.8f,         0.7f, 0.7f, 0.7f,

                // line1
                -0.5f,  0f,            1f, 0f, 0f,
                0.5f,   0f,            1f, 0f, 0f,
                // mallets
                0f,     -0.4f,         0f, 0f, 1f,
                0f,     0.4f,          1f, 0f, 0f
        };

//        float[] tableVertices = {
//                // order of coordinates: x, y, z, w, r, g, b
//
//                // triangle fan
//                0f,     0f,     0f,     1.5f,       1f, 1f, 1f,
//                -0.5f,  -0.8f,  0f,     1f,         0.7f, 0.7f, 0.7f,
//                0.5f,   -0.8f,  0f,     1f,         0.7f, 0.7f, 0.7f,
//                0.5f,   0.8f,   0f,     2f,         0.7f, 0.7f, 0.7f,
//                -0.5f,  0.8f,   0f,     2f,         0.7f, 0.7f, 0.7f,
//                -0.5f,  -0.8f,  0f,     1f,         0.7f, 0.7f, 0.7f,
//
//                // line1
//                -0.5f,  0f,     0f,     1.5f,       1f, 0f, 0f,
//                0.5f,   0f,     0f,     1.5f,       1f, 0f, 0f,
//                // mallets
//                0f,     -0.4f,  0f,     1.25f,      0f, 0f, 1f,
//                0f,     0.4f,   0f,     1.75f,      1f, 0f, 0f
//        };

        vertexData = ByteBuffer.allocateDirect(tableVertices.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexData.put(tableVertices);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        String vertexShaderSource = TextResourceReader.readTextFileFromResource(context, R.raw.simple_vertex_shader);
        String fragmentShaderSource = TextResourceReader.readTextFileFromResource(context, R.raw.simple_fragment_shader);

        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);
        program = ShaderHelper.linkProgram(vertexShader, fragmentShader);

        if (LoggerHelper.ON)
            ShaderHelper.validateProgram(program);

        GLES20.glUseProgram(program);

//        uColorLocation = GLES20.glGetUniformLocation(program, U_COLOR);
        aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION);
        aColorLocation = GLES20.glGetAttribLocation(program, A_COLOR);
        uMatrixLocation = GLES20.glGetUniformLocation(program, U_MATRIX);

        vertexData.position(0);
        GLES20.glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GLES20.GL_FLOAT, false, STRIDE, vertexData);
        GLES20.glEnableVertexAttribArray(aPositionLocation);
        vertexData.position(POSITION_COMPONENT_COUNT);
        GLES20.glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GLES20.GL_FLOAT, false, STRIDE, vertexData);
        GLES20.glEnableVertexAttribArray(aColorLocation);
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
        Matrix.translateM(modelMatrix, 0, 0f, 0f, -2.5f);
        Matrix.rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f);

        final float[] temp = new float[16];
        Matrix.multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
        System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);

//        GLES20.glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 6);

//        GLES20.glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glDrawArrays(GLES20.GL_LINES, 6, 2);

//        GLES20.glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f);
        GLES20.glDrawArrays(GLES20.GL_POINTS, 8, 1);

//        GLES20.glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glDrawArrays(GLES20.GL_POINTS, 9, 1);
    }
}
