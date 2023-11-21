package org.astemir.uniblend.io;

import java.io.*;
import java.nio.charset.Charset;

public class FileUtils {

    public static void writeText(File file, Charset charset, String text) throws IOException{
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), charset)) {
            writer.write(text);
        }
    }

    public static String readText(File file,Charset charset) throws IOException{
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            return sb.toString();
        }
    }

    public static String readText(InputStream inputStream,Charset charset) throws IOException{
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, charset))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            return sb.toString();
        }
    }
}
