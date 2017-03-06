package util;

public class Application extends android.app.Application{
	private static Application application;
	
	@Override
	public void onCreate() {
		super.onCreate();
		this.application = this;
		initCrashHandler();
	}
	
	public static Application getApplication() {
		return application;
	}

	public void initCrashHandler() {
		CrashHandler handler = CrashHandler.getInstance();
		handler.init(this);
	}
}
