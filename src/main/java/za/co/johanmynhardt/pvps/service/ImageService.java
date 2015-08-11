package za.co.johanmynhardt.pvps.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.Lists;

/**
 * @author Johan Mynhardt
 */
public interface ImageService {

	File imageFile(InputStream inputStream) throws IOException;
	Optional<String> resizeImage(InputStream inputStream, String resizeConfigurationKey) throws IOException;
	Optional<String> resizeImage(InputStream inputStream) throws IOException;
	Optional<String> resizeImage(InputStream inputStream, ResizeConfiguration resizeConfiguration) throws IOException;

	interface ResizeConfiguration {
		String getKey();
		Integer getWidth();
		Integer getHeight();
		Integer getBorderSize();
		Integer getMaxKiloBytes();
		String getBorderColour();
	}

	class DefaultConfigurations {

		public static final List<ResizeConfiguration> availableConfigurations = Lists.newArrayList(getSmall4to3(), getApa1280to800());

		private static int defaultBorderSize = 2;
		private static String defaultBorderColour = "white";

		public static ResizeConfiguration getSmall4to3() {
			return new ResizeConfiguration() {
				@Override
				public String getKey() {
					return "Salon";
				}

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
				public String getKey() {
					return "APA";
				}

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
