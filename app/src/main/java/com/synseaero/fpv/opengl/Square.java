/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.synseaero.fpv.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

/**
 * A two-dimensional square for use as a drawn object in OpenGL ES 2.0.
 */
public class Square {


    private final String vertexShaderCode =
            // This matrix member variable provides a hook to manipulate
             "uniform mat4 uMVPMatrix;" +
             "attribute vec4 vPosition;" +
             "attribute vec4 a_Color; "+
             "varying vec4 v_Color;" +
             "attribute vec2 a_TexCoordinate;" +
             "varying vec2 v_TexCoordinate; " +
             "void main() {" +
            // The matrix must be included as a modifier of gl_Position.
            // Note that the uMVPMatrix factor *must be first* in order
            // for the matrix multiplication product to be correct.
             "  v_Color = a_Color;" +
             "  v_TexCoordinate = a_TexCoordinate;"+
             "  gl_Position = uMVPMatrix * vPosition;" +
            "}";

//    private final String fragmentShaderCode =
//            "precision mediump float;" +
//                    "uniform vec4 vColor;" +
//                    "void main() {" +
//                    "  gl_FragColor = vColor;" +
//                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
//            "uniform vec4 vColor;" +
             "uniform sampler2D u_Texture;"+   //

             "varying vec2 v_TexCoordinate;" +// triangle per fragment for texture.
             "void main() {" +

//             " gl_FragColor = (v_Color * texture2D(u_Texture, v_TexCoordinate));" +
             " gl_FragColor = texture2D(u_Texture, v_TexCoordinate);" +
            "}";


    private final FloatBuffer vertexBuffer;
    private final FloatBuffer textureBuffer;
    private final ShortBuffer drawListBuffer;

    private final int mProgram;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;
    private int mTextureUniformHandle;
    private int mTextureCoordinateHandle;


    private int mTextureDataHandle0;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static float offZ =-0.2f;
    static float squareCoords[] = {
            -0.5f,  0.5f, 0.0f+offZ,   // top left
            -0.5f, -0.5f, 0.0f+offZ,   // bottom left
             0.5f, -0.5f, 0.0f+offZ,   // bottom right
             0.5f,  0.5f, 0.0f+offZ }; // top right

//////////////////////////////////////////////////////////
    static float TextureCoordniate[]  = {
                    0.0f, 0.0f,
                    0.0f, 1.0f,
                    1.0f, 1.0f,
                    1.0f, 0.0f,	};
/////////////////////////////////////////////////////////////

    private final short drawOrder[] = { 0, 1, 2, 0, 2, 3 }; // order to draw vertices

    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    float color[] = { 0.2f, 0.709803922f, 0.898039216f, 1.0f };

    /**
     * Sets up the drawing object data for use in an OpenGL ES context.
     */


    public Square() {


        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
        // (# of coordinate values * 4 bytes per float)
                squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

///////////////////////// initialize float buffer for the texture ////////////////////////////
        ByteBuffer tb = ByteBuffer.allocateDirect( TextureCoordniate.length*4);
        tb.order(ByteOrder.nativeOrder());
        textureBuffer =tb.asFloatBuffer();
        textureBuffer.put(TextureCoordniate);
        textureBuffer.position(0);





////////////////////////////////////////////////////////////////////////////////////


        // prepare shaders and OpenGL program
        int vertexShader = MyGLRenderer.loadShader(
                GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(
                GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables

///////////////////////   loadTexture here  !!!  ///////////////////////////
        //mTextureDataHandle0 = loadTexture(R.drawable.compa_base);

    }



    private static int loadTexture(final int resourceId)
    {
        final int[] textureHandle = new int[1];


        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] != 0)
        {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;   // No pre-scaling

            // Read in the resource
            final Bitmap bitmap = BitmapFactory.decodeResource(MyGLSurfaceView.res, resourceId, options);
//            final Bitmap bitmap = BitmapFactory.decodeStream(ins);
            // Bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

            // Set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

            Log.d("OPENGL","load texture succesfully");   //bitmap: + bitmap
            Log.d("texture Height:",String.valueOf(bitmap.getHeight()));
            Log.d("texture Height:",String.valueOf(bitmap.getWidth()));

            // Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap.recycle();
        }

        if (textureHandle[0] == 0)
        {
            throw new RuntimeException("Error loading texture.");
        }

        return textureHandle[0];
    }

    /**
     * Encapsulates the OpenGL ES instructions for drawing this shape.
     *
     * @param mvpMatrix - The Model View Project matrix in which to draw
     * this shape.
     */
    public void draw(float[] mvpMatrix) {
        // Add program to OpenGL environment
        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(
                mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

//////////////////////////*   color code    *////////////////////////////
        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

//////////////////////////*   texture code    *////////////////////////////

//////////        GLES20.glEnable(GLES20.GL_TEXTURE_2D);

        mTextureUniformHandle= GLES20.glGetUniformLocation(mProgram, "u_Texture");
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgram, "a_TexCoordinate");
//
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0); // Set the active texture unit to texture unit 0.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle0); // Bind the texture to this unit.
//        GLES20.glUniform1i(mTextureUniformHandle, 0);

        GLES20.glVertexAttribPointer(mTextureCoordinateHandle, 2,
                GLES20.GL_FLOAT, false,
                2*4, textureBuffer);

        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);



////////////////////////////////////////////////////////////////////////////////////

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        MyGLRenderer.checkGlError("glGetUniformLocation");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        MyGLRenderer.checkGlError("glUniformMatrix4fv");

        // Draw the square
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);

        GLES20.glDisableVertexAttribArray(mTextureCoordinateHandle);

    }

}