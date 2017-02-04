package com.airhockey.android.data;

import android.opengl.GLES20;

import com.airhockey.android.Constants;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by xinchen on 2017/2/3.
 */

public class VertexArray {
    private final FloatBuffer floatBuffer;

    public VertexArray(float[] vertexData)
    {
        floatBuffer = ByteBuffer.allocateDirect(vertexData.length * Constants.BYTES_PER_FLOAT)
                        .order(ByteOrder.nativeOrder()).asFloatBuffer().put(vertexData);
    }

    public void setVertexAttribPointer(int dataOffset, int attributeLocation, int componentCount, int stride)
    {
        floatBuffer.position(dataOffset);
        GLES20.glVertexAttribPointer(attributeLocation, componentCount, GLES20.GL_FLOAT, false, stride, floatBuffer);
        GLES20.glEnableVertexAttribArray(attributeLocation);

        floatBuffer.position(0);
    }
}
