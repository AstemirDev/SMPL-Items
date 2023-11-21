package org.astemir.uniblend.io;



import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class FileHandle {

    private File file;
    private String content;
    private boolean loaded = false;

    public FileHandle(File file) {
        if (file == null) {
            throw new NullPointerException("File cannot be null");
        }
        this.file = file;
    }

    public void load() {
        load(Charset.defaultCharset());
    }

    public void load(Charset charset) {
        try {
            if (charset == null) {
                throw new NullPointerException("Charset cannot be null");
            }
            try {
                content = FileUtils.readText(file, charset);
                loaded = true;
            } catch (IOException e) {
                loaded = false;
                throw e;
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public String content() {
        return loaded ? content : null;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void save(Charset charset) {
        if (charset == null) {
            throw new NullPointerException("Charset cannot be null");
        }
        if (!loaded) {
            throw new IllegalStateException("File has not been loaded yet");
        }
        try {
            FileUtils.writeText(file, charset, content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            save(Charset.defaultCharset());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean isLoaded() {
        return loaded;
    }

    public File getFile() {
        return file;
    }
}