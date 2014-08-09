package za.co.johanmynhardt.pvps.service.onstartup;

import java.io.File;
import java.io.FileFilter;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import za.co.johanmynhardt.pvps.service.util.FileCacheUtil;

import static java.lang.String.format;
import static za.co.johanmynhardt.pvps.service.util.FileCacheUtil.TMP_DIR;

/**
 * @author Johan Mynhardt
 */
@WebListener("FilePurge Scheduler")
public class ScheduledFilePurge extends TimerTask implements ServletContextListener {
	private ServletContext context = null;

	private Timer timer = null;

	private final long EXPIRATION_TIME = TimeUnit.MINUTES.toMillis(5);

	private final FileFilter EXPIRED_FILE_FILTER = new FileFilter() {
		@Override
		public boolean accept(File pathname) {
			return (pathname.exists()
					&& ((System.currentTimeMillis() - pathname.lastModified()) > EXPIRATION_TIME)
					&& pathname.getAbsoluteFile().getName().matches(FileCacheUtil.IMG_FILE_REGEX)
			);
		}
	};

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		if (context == null) {
			context = servletContextEvent.getServletContext();
		}
		timer = new Timer("FilePurgeTimer");
		timer.scheduleAtFixedRate(this, EXPIRATION_TIME, EXPIRATION_TIME);
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		if (timer != null) {
			timer.cancel();
		}

		if (context != null) {
			context = null;
		}
	}

	@Override
	public void run() {
		removeFiles(TMP_DIR.listFiles(EXPIRED_FILE_FILTER));
	}

	private void removeFiles(File[] filesToDelete) {
		if (filesToDelete != null && filesToDelete.length > 0) {
			Map<String, Boolean> removedMap = new TreeMap<>();
			for (File file : filesToDelete) {
				removedMap.put(file.getAbsolutePath(), file.delete());
			}
			if (context != null) {
				context.log(format("Removed %d files.", removedMap.keySet().size()));
			}
		}
	}
}
