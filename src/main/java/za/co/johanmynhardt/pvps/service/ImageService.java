package za.co.johanmynhardt.pvps.service;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

/**
 * @author Johan Mynhardt
 */
public interface ImageService {

	File imageFile(InputStream inputStream) throws IOException;
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

	class ResizeConfigurationWithDefaults implements ResizeConfiguration {
		String key;
		Integer width;
		Integer height;
		Integer borderSize;
		Integer maxKiloBytes;
		String borderColour;

		public ResizeConfigurationWithDefaults(String key, Integer width, Integer height, Integer borderSize, Integer maxKiloBytes, String borderColour) {
			this.key = key;
			this.width = width;
			this.height = height;
			this.borderSize = borderSize;
			this.maxKiloBytes = maxKiloBytes;
			this.borderColour = borderColour;
		}

		@Override
		public String getKey() {
			return key;
		}

		@Override
		public Integer getWidth() {
			return width;
		}

		@Override
		public Integer getHeight() {
			return height;
		}

		@Override
		public Integer getBorderSize() {
			return borderSize;
		}

		@Override
		public Integer getMaxKiloBytes() {
			return maxKiloBytes;
		}

		@Override
		public String getBorderColour() {
			return borderColour;
		}

		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this)
					.add("key", this.key)
					.add("width", this.width)
					.add("height", this.height)
					.add("borderColour", this.borderColour)
					.add("borderSize", this.borderSize)
					.add("maxKiloBytes", this.maxKiloBytes).toString();
		}
	}

	class DefaultConfigurations {

		public static final List<ResizeConfiguration> availableConfigurations = Lists.newArrayList(getSmall4to3(), getApa1280to800());

		private static final int defaultBorderSize = 2;
		private static final String defaultBorderColour = "white";

		public static ResizeConfiguration getSmall4to3() {
			return new ResizeConfigurationWithDefaults("Salon", 1024, 768, defaultBorderSize, 500, defaultBorderColour);
		}

		public static ResizeConfiguration getApa1280to800() {
			return new ResizeConfigurationWithDefaults("APA", 1280, 800, defaultBorderSize, 1500, defaultBorderColour);
		}

		public static Optional<ResizeConfiguration> findForKey(String key) {
			return DefaultConfigurations.availableConfigurations.stream()
					.filter(input -> input.getKey().equals(key))
					.findFirst();
		}
	}
}
