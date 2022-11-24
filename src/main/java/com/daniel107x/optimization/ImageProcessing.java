package com.daniel107x.optimization;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageProcessing {
    public static final String SOURCE_FILE = "./resources/many-flowers.jpg";
    public static final String DESTINATION = "./out/many-flowers-mt.jpg";
    public static void main(String[] args) throws IOException {
        BufferedImage originalImage = ImageIO.read(new File(SOURCE_FILE));
        BufferedImage resultImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        long startTime = System.currentTimeMillis();
        int threads = 6;
        recolorMultithreaded(originalImage, resultImage, threads);
//        recolorSingleThreaded(originalImage, resultImage);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        File outFile = new File(DESTINATION);
        ImageIO.write(resultImage, "jpg", outFile);
        System.out.println("Duration: " + duration);
    }

    public static void recolorSingleThreaded(BufferedImage original, BufferedImage out){
        recolorImage(original, out, 0, 0, original.getWidth(), original.getHeight());
    }

    /**
     * The multithreaded solution will consist of partitioning the image and process each image section in a single thread
     */
    public static void recolorMultithreaded(BufferedImage original, BufferedImage result, int numberOfThreads){
        List<Thread> threadList = new ArrayList<>();
        int width = original.getWidth();
        int height = original.getHeight() / numberOfThreads;

        for(int i = 0 ; i < numberOfThreads ; i++){
            final int threadMultiplier = i;

            Thread thread = new Thread(()->{
                int leftCorner = 0;
                int topCorner = height * threadMultiplier;

                recolorImage(original, result, leftCorner, topCorner, width, height);
            });

            threadList.add(thread);
        }

        threadList.forEach(Thread::start);

        for(Thread thread : threadList){
            try{
                thread.join();
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }

    }


    public static void recolorImage(BufferedImage original, BufferedImage output, int leftCorner, int topCorner, int width, int height){
        for(int x = leftCorner ; x < leftCorner + width && x < original.getWidth() ; x++){
            for(int y = topCorner ; y < topCorner + height && y < original.getHeight() ; y++){
                recolorPixel(original, output, x, y);
            }
        }
    }

    public static void recolorPixel(BufferedImage original, BufferedImage output, int x, int y){
        int rgb = original.getRGB(x, y);
        int red = getRed(rgb);
        int green = getGreen(rgb);
        int blue = getBlue(rgb);

        int newRed;
        int newGreen;
        int newBlue;

        if(isShadeOfGray(red, green, blue)){
            newRed = Math.min(255, red + 10);
            newGreen = Math.max(0, green - 80);
            newBlue = Math.max(0, blue - 20);
        }else{
            newRed = red;
            newGreen = green;
            newBlue = blue;
        }

        int newRGB = createRGB(newRed, newGreen, newBlue);
        setRGB(output, x, y, newRGB);

    }

    public static void setRGB(BufferedImage image, int x, int y, int rgb){
        image.getRaster().setDataElements(x, y, image.getColorModel().getDataElements(rgb, null));
    }

    public static boolean isShadeOfGray(int red, int green, int blue){
        return Math.abs(red - green) < 30 && Math.abs(red - blue) < 30 && Math.abs(green - blue) < 30;
    }

    public static int createRGB(int red, int green, int blue){
        int rgb = 0;
        rgb |= blue;
        rgb |= green << 8;
        rgb |= red << 16;
        return rgb | 0xFF000000;
    }

    public static int getGreen(int rgb){
        return (rgb & 0x0000FF00) >> 8;
    }

    public static int getRed(int rgb){
        return (rgb & 0x00FF0000) >> 16;
    }

    public static int getBlue(int rgb){
        return rgb & 0x000000FF;
    }
}
