package com.alexander.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Component
public class FileUtil {
    public final Path APP_DATA_MAIN;
    public final Path APP_DATA_REFRESH_TOKENS;
    public final Path APP_DATA_SETTINGS;
    public final Path APP_DATA_PRESETS;

    public FileUtil(@Value("${app-data.dir.main}") String mainDir,
                        @Value("${app-data.dir.user.refresh-tokens}") String refreshTokensDir,
                        @Value("${app-data.dir.user.settings}") String settingsDir,
                        @Value("${app-data.dir.presets}") String presetsDir) {
        String appDataDir = getAppDataDir();
        APP_DATA_MAIN = Path.of(appDataDir, mainDir);
        APP_DATA_REFRESH_TOKENS = Path.of(appDataDir, refreshTokensDir);
        APP_DATA_SETTINGS = Path.of(appDataDir, settingsDir);
        APP_DATA_PRESETS = Path.of(appDataDir, presetsDir);
    }

    public void writeInFile(Path path, String content, boolean createIfNotFound) throws IOException {
        Files.createDirectories(path.getParent());
        if (!Files.exists(path) && createIfNotFound) {
            Files.createFile(path);
        }
        Files.write(path, content.getBytes());
    }

    public String readFromFile(Path path) {
        try {return Files.readString(path);
        } catch (IOException e) {
            return "";
        }
    }

    public Set<String> getFileNames(Path path, FileExtension extension) {
        Set<String> filenames = new HashSet<>();
        try {
            Iterator<Path> iterator = Files.walk(path).iterator();
            while (iterator.hasNext()) {
                Path file = iterator.next();
                if (extension == null || file.getFileName().toString().endsWith(extension.getValue())) {
                    String fileName = file.getFileName().toString();
                    int dotIndex = fileName.lastIndexOf('.');
                    if (dotIndex > 0) {
                        fileName = fileName.substring(0, dotIndex);
                    }
                    filenames.add(fileName);
                }
            }
        } catch (IOException e) {
            return new HashSet<>();
        }
        return filenames;
    }

    public void deleteFile(Path path) throws IOException {
        Files.deleteIfExists(path);
    }

    public boolean renameFile(Path path, String newName, FileExtension extension) throws IOException {
        Path newPath = Path.of(path.getParent().toString(), newName + extension.getValue());
        return moveFile(path, newPath, true);
    }

    public boolean moveFile(Path source, Path dest, boolean allowReplace) throws IOException {
        try {
            if (allowReplace || !Files.exists(source)) {
                Files.move(source, dest, StandardCopyOption.REPLACE_EXISTING);
                return true;
            }
        } catch (IOException e) {
           return false;
        }
        return false;
    }

    private String getAppDataDir() {
        String os = System.getProperty("os.name").toLowerCase();
        String userHome = System.getProperty("user.home");
        if (os.contains("win")) {
            String appData = System.getenv("APPDATA");
            return appData != null ? appData : userHome + "\\AppData\\Roaming";
        } else if (os.contains("mac")) {
            return userHome + "/Library/Application Support";
        } else {
            return userHome + "/.config";
        }
    }
}
