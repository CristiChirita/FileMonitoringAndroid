package nttdata.com.filemonitoringnotifications;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FTPUtil {
    FTPClient ftpClient;
    private static final String TAG = "FTPClient";

    public FTPUtil() throws IOException {
        this.ftpClient = new FTPClient();
        ftpClient.connect("10.227.80.55", 21);
    }

    public boolean downloadFile(String remoteFilePath, String savePath) throws IOException {
        File downloadFile = new File(savePath);
        File parent = downloadFile.getParentFile();

        if (!parent.exists()) {
            parent.mkdirs();
        }
        if (!downloadFile.exists()) {
            downloadFile.createNewFile();
        }
        OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(downloadFile));
        try {
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            return ftpClient.retrieveFile(remoteFilePath, outputStream);
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    public void downloadDirectory(String parentDir, String saveDir) throws IOException {
        Log.d(TAG, "Downloading directory " + parentDir);
        FTPFile[] subFiles = ftpClient.listFiles(parentDir);

        if (subFiles != null && subFiles.length > 0) {
            for (FTPFile aFile : subFiles) {
                String currentFileName = aFile.getName();
                if (currentFileName.equals(".") || currentFileName.equals("..")) {
                    // skip parent directory and the directory itself
                    continue;
                }
                String filePath = parentDir + File.separator + currentFileName;
                if (parentDir.equals("/")) {
                    filePath = parentDir + currentFileName;
                }

                String newDirPath = saveDir + File.separator + filePath;
                if (parentDir.equals("/")) {
                    newDirPath = saveDir + filePath;
                }

                if (aFile.isDirectory()) {
                    // create the directory in saveDir
                    File newDir = new File(newDirPath);
                    boolean created = newDir.getParentFile().mkdirs();
                    if (created) {
                        Log.d(TAG, "CREATED the directory: " + newDirPath);
                    } else {
                        Log.d(TAG, "COULD NOT create the directory: " + newDirPath);
                    }

                    // download the sub directory

                    downloadDirectory(filePath, saveDir);
                } else {
                    // download the file
                    boolean success = downloadFile(filePath, newDirPath);
                    if (success) {
                        Log.d(TAG, "DOWNLOADED the file: " + filePath);
                    } else {
                        Log.d(TAG, "COULD NOT download the file: " + filePath);
                    }
                }
            }
        }
    }
}
