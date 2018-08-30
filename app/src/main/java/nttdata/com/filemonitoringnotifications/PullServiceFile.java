package nttdata.com.filemonitoringnotifications;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import org.apache.commons.net.ftp.FTP;

import java.io.File;
import java.util.HashSet;

import static nttdata.com.filemonitoringnotifications.MainActivity.bean;

public class PullServiceFile extends JobService{

    private static final String TAG = "PullServiceFile";
    private RetrieveFilesTask task = new RetrieveFilesTask();

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "Performing long running task in scheduled job");
        // TODO(developer): add long running task here.
        task.execute("Files");
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    class RetrieveFilesTask extends AsyncTask<String, Void, Void> {
        private Exception exception;

        @Override
        protected Void doInBackground(String... urls) {
            downloadFile();
            return null;
        }

        protected void onPostExecute() {
            if (exception == null) {
                Log.d(TAG, "Downloaded files");
                bean.setFiles(new HashSet<String>());
            }
            else {
                exception.printStackTrace();
            }

        }

        protected synchronized void downloadFile() {
            String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MyDownload";
            try {
                FTPUtil client = new FTPUtil();
                client.ftpClient.enterLocalPassiveMode();
                client.ftpClient.login("User", "user");
                client.ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                HashSet<String> files = bean.getFiles();
                for (String fileName : files) {
                    File toDownload = new File(fileName);
                    if (client.downloadFile(fileName, baseDir + toDownload.getParent())) {
                        Log.d(TAG, "downloadFile " + fileName + ": Succeeded");
                    }
                    else {
                        Log.d(TAG, "downloadFile " + fileName + ": Failed");
                    }
                }
                client.ftpClient.logout();
                bean.setFiles(new HashSet<String>());
            } catch (Exception e) {
                exception = e;
            }
        }
    }

}
