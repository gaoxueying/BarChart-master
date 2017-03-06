package util;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.Thread.UncaughtExceptionHandler;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
/**
 */
public class CrashHandler implements UncaughtExceptionHandler {

	private static CrashHandler INSTANCE = new CrashHandler();

	public static CrashHandler getInstance() {
		return INSTANCE;
	}

	private CrashHandler() {

	}

	public void init(Context context) {
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		LogUtil.w(ex);
		try {
			String path = Environment.getExternalStorageDirectory() + "/crash/meihao/";
			File f = new File(path);
			if (!f.exists())
				f.mkdirs();
			File file = new File(path+"crash.log");
			file.createNewFile();
			FileOutputStream out = new FileOutputStream(path+"crash.log");
			out.write(Log.getStackTraceString(ex).getBytes());
			out.close();
		} catch (Exception e) {
			LogUtil.w(e);
		}
	}

}
