package net.vasile2k.fooball.render;

import org.lwjgl.BufferUtils;
import org.lwjglx.debug.org.eclipse.jetty.util.BufferUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL44.*;

/**
 * Created by Vasile2k on 17.05.2019.
 *
 */

public class Texture {

    private int textureId;

    private int width;
    private int heigth;

    public Texture(String path){

        BufferedImage image;

        try {
            image = ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Can't load texture image " + path);
        }

        this.width = image.getWidth();
        this.heigth = image.getHeight();

        int[] pixels = new int[this.width * this.heigth];
        image.getRGB(0, 0, this.width, this.heigth, pixels, 0, this.width);

        ByteBuffer imageBuffer = BufferUtils.createByteBuffer(this.width * this.heigth * 4 /* bytes per pixel */);
        imageBuffer.asIntBuffer().put(pixels);

        // I guess it's required for OpenGL Image Loading
        imageBuffer.flip();
        // But this shit does not seem to work so a temporary fix is done in shader...

        this.textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, this.textureId);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, this.width, this.heigth, 0, GL_RGBA, GL_UNSIGNED_BYTE, imageBuffer);

        glBindTexture(GL_TEXTURE_2D, 0);
    }

    @Override
    public void finalize() throws Throwable{
//        glDeleteTextures(this.textureId);
    }

    public void bind(int slot){
        glActiveTexture(GL_TEXTURE0 + slot);
        glBindTexture(GL_TEXTURE_2D, this.textureId);
    }

    public void unbind(){
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public int getWidth() {
        return width;
    }

    public int getHeigth() {
        return heigth;
    }
}
