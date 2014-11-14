package za.co.johanmynhardt.pvps.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.google.common.base.Optional;

/**
 * @author Johan Mynhardt
 */
public interface ImageService {

	public File imageFile(InputStream inputStream) throws IOException;
	public Optional<String> resizeImage(InputStream inputStream) throws IOException;
	public Optional<String> resizeImage(InputStream inputStream, ResizeConfiguration resizeConfiguration) throws IOException;

	public interface ResizeConfiguration {
		public Integer getWidth();
		public Integer getHeight();
		public Integer getBorderSize();
		public Integer getMaxKiloBytes();
		public String getBorderColour();
	}

	public static class DefaultConfigurations {

		private static int defaultBorderSize = 2;
		private static String defaultBorderColour = "white";

		public static ResizeConfiguration getSmall4to3() {
			return new ResizeConfiguration() {
				@Override
				public Integer getWidth() {
					return 1024;
				}

				@Override
				public Integer getHeight() {
					return 768;
				}

				@Override
				public Integer getBorderSize() {
					return defaultBorderSize;
				}

				@Override
				public Integer getMaxKiloBytes() {
					return 500;
				}

				@Override
				public String getBorderColour() {
					return defaultBorderColour;
				}
			};
		}

		public static ResizeConfiguration getApa1280to800() {
			return new ResizeConfiguration() {
				@Override
				public Integer getWidth() {
					return 1280;
				}

				@Override
				public Integer getHeight() {
					return 800;
				}

				@Override
				public Integer getBorderSize() {
					return defaultBorderSize;
				}

				@Override
				public Integer getMaxKiloBytes() {
					return 1500;
				}

				@Override
				public String getBorderColour() {
					return defaultBorderColour;
				}
			};
		}
	}
}
