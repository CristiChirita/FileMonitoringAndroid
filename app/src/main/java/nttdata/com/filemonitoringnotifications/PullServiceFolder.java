package nttdata.com.filemonitoringnotifications;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import org.apache.commons.net.ftp.FTP;

import java.io.File;
import java.util.HashSet;

import static nttdata.com.filemonitoringnotifications.MainActivity.bean;
import static nttdata.com.filemonitoringnotifications.MainActivity.list;

public class PullServiceFolder extends JobService{

    private static final String TAG = "PullServiceFolder";
    private RetrieveFilesTask task = new RetrieveFilesTask();

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "Performing long running task in scheduled job");
        // TODO(developer): add long running task here.
        task.execute("Directory");
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
            downloadFolder();
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

        protected void downloadFolder() {
            String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MyDownload";
            try {
                FTPUtil client = new FTPUtil();
                client.ftpClient.enterLocalPassiveMode();
                client.ftpClient.login("User", "user");
                client.ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                client.downloadDirectory("/", baseDir);
                client.ftpClient.logout();
            } catch (Exception e) {
                exception = e;
            }
        }
    }

}
