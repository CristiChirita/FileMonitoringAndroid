package nttdata.com.filemonitoringnotifications;

import java.util.HashSet;

public class FileDownloadBean {
    private HashSet<String> files = new HashSet<>();

    public HashSet<String> getFiles() {
        return files;
    }

    public void setFiles(HashSet<String> files) {
        this.files = files;
    }

}
