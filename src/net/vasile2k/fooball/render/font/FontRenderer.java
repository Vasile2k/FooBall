package net.vasile2k.fooball.render.font;

import net.vasile2k.fooball.render.Shader;
import net.vasile2k.fooball.render.Texture;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL44.*;

/**
 * Created by Vasile2k on 18.05.2019. <br/>
 * This class is basically bullshit. <br/>
 * It's just an improvisation until I'll find somethin' better
 */

public class FontRenderer {

    public static class FontRendererBuilder{

        public static FontRenderer buildFontRenderer(){
            return new FontRenderer();
        }

    }

    private Shader shader;
    private Texture bitmapFont;

    private FontRenderer(){
        this.shader = new Shader("res/shader/font");
        this.bitmapFont = new Texture("res/texture/bitmap_font.png");
    }

    public void renderText(String text, float size, float spacing, float xPos, float yPos, float red, float green, float blue){
        this.shader.bind();
        this.bitmapFont.bind(0);
        glDepthMask(false);
        glBlendFunc(GL_SRC_ALPHA, GL_DST_ALPHA);
        glColor3f(red, green, blue);
        glBegin(GL_QUADS);
        for(int i = 0; i < text.length(); ++i){
            char c = text.charAt(i);
            int row = c % 16;
            int col = c / 16;
            float normalizedRow = (float)row/16.0F;
            float normalizedCol = (float)col/16.0F;
            float one16th = 1.0F/16.0F;
            glTexCoord2f(normalizedRow, normalizedCol + one16th);
            glVertex2f(xPos, yPos);
            glTexCoord2f(normalizedRow + one16th, normalizedCol + one16th);
            glVertex2f(xPos + size, yPos);
            glTexCoord2f(normalizedRow + one16th, normalizedCol);
            glVertex2f(xPos + size, yPos + size);
            glTexCoord2f(normalizedRow, normalizedCol);
            glVertex2f(xPos, yPos + size);
            xPos += spacing;
        }
        glEnd();
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDepthMask(true);
    }

}
