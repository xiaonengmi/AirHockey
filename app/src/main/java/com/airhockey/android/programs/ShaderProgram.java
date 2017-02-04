package com.airhockey.android.programs;

import android.content.Context;
import android.opengl.GLES20;

import com.airhockey.android.util.ShaderHelper;
import com.airhockey.android.util.TextResourceReader;

/**
 * Created by xinchen on 2017/2/3.
 */

public class ShaderProgram {
    // uniforms
    protected static final String U_MATRIX = "u_Matrix";
    protected static final String U_TEXTURE_UNIT = "u_TextureUnit";

    // attributes
    protected static final String A_POSITION = "a_Position";
    protected static final String A_COLOR = "a_Color";
    protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";

    // shader program
    protected final int program;

    protected ShaderProgram(Context context, int vertexShaderResourceId, int fragmentShaderResourceId)
    {
        program = ShaderHelper.buildProgram(
                TextResourceReader.readTextFileFromResource(context, vertexShaderResourceId),
                TextResourceReader.readTextFileFromResource(context, fragmentShaderResourceId));
    }

    public void useProgram()
    {
        GLES20.glUseProgram(program);
    }
}
