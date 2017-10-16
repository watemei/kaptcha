package com.google.code.kaptcha.impl;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;
import java.util.Random;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.util.Configurable;

public class DefaultCASKaptcha extends Configurable implements Producer {
    private int width = 200;

    private int height = 50;

    /**
     * Create an image which will have written a distorted text.
     * 
     * @param text
     *            the distorted characters
     * @return image with the text
     */
    public BufferedImage createImage(String text) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); // 设置图片大小的
        Graphics2D gra = image.createGraphics();

        Random random = new Random();
        int fontSize = getConfig().getTextProducerFontSize();
        gra.setColor(getRandColor(200, 250)); // 设置背景色
        gra.fillRect(0, 0, width, height);

        gra.setColor(Color.black); // 设置字体色
        System.setProperty("java.awt.headless", "true");
        gra.setFont(new Font("Times New Roman", Font.PLAIN, fontSize));

        // 随机产生155条干扰线，使图象中的认证码不易被其它程序探测到
        gra.setColor(getRandColor(160, 200));
        for (int i = 0; i < 155; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int xl = random.nextInt(12);
            int yl = random.nextInt(12);
            gra.drawLine(x, y, x + xl, y + yl);
        }

        // 取随机产生的认证码(4位数字)
        FontRenderContext frc = gra.getFontRenderContext();
        Font[] fonts = getConfig().getTextProducerFonts(fontSize);
        int startPosX = width / (2 + text.length());
        int startPosY = (height - fontSize) / 5 + fontSize;
        char[] wordChars = text.toCharArray();
        Font[] chosenFonts = new Font[wordChars.length];
        int [] charWidths = new int[wordChars.length];
        int widthNeeded = 0;
        for (int i = 0; i < wordChars.length; i++)
        {
            chosenFonts[i] = fonts[random.nextInt(fonts.length)];

            char[] charToDraw = new char[]{
                wordChars[i]
            };
            GlyphVector gv = chosenFonts[i].createGlyphVector(frc, charToDraw);
            charWidths[i] = (int)gv.getVisualBounds().getWidth();
            if (i > 0)
            {
                widthNeeded = widthNeeded + 2;
            }
            widthNeeded = widthNeeded + charWidths[i];
        }
        for (int i = 0; i < text.length(); i++) {
            String rand = String.valueOf(text.charAt(i));
            // 将认证码显示到图象中
            gra.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(210), 20 + random.nextInt(50))); // 调用函数出来的颜色相同，可能是因为种子太接近，所以只能直接生成
            gra.drawChars(rand.toCharArray(), 0, 1, startPosX, startPosY);
            chosenFonts[i] = fonts[random.nextInt(fonts.length)];
            GlyphVector gv =  chosenFonts[i].createGlyphVector(frc, rand);
            double charWidth = gv.getVisualBounds().getWidth();
            startPosX = startPosX + (int) charWidth + 2;
        }
        return image;
    }

    /**
     * 
     * TODO(给定范围随机获得颜色)
     * 
     * @param fc
     * @param bc
     * @return
     * @see
     */
    public static Color getRandColor(int fc, int bc) {
        Random random = new Random();
        if (fc > 255)
            fc = 255;
        if (bc > 255)
            bc = 255;
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }

    /**
     * @return the text to be drawn
     */
    public String createText() {
        return getConfig().getTextProducerImpl().getText();
    }
}
