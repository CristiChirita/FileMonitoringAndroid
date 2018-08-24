package nttdata.com.filemonitoringnotifications;

import android.os.AsyncTask;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import org.apache.commons.net.ftp.FTPClient;

import java.util.HashSet;

import static nttdata.com.filemonitoringnotifications.MainActivity.bean;

public class PullService extends JobService{

    private static final String TAG = "PullService";
    private static FTPClient client = new FTPClient();
    private RetrieveFilesTask task = new RetrieveFilesTask();

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "Performing long running task in scheduled job");
        // TODO(developer): add long running task here.
        task.execute();
        task.onPostExecute();
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
            boolean login = false;
            try {
                client.connect("10.227.80.55");
                login = client.login("User", "user");
            } catch (Exception e) {
                this.exception = e;
                return null;
            }
            if (login) {
                Log.d(TAG, "Connection established");
            }
            else {
                Log.d(TAG, "Connection failed");
            }
            try {
                if (client.logout()) {
                    Log.d(TAG, "Successful logout");
                }
                else {
                    Log.d(TAG, "Logout failed");
                }
            }
            catch (Exception e) {
                this.exception = e;
            }
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
    }

}
