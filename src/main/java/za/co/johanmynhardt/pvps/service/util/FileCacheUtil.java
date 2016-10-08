package za.co.johanmynhardt.pvps.service.util;

import static java.lang.String.format;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteSource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

/**
 * @author Johan Mynhardt
 */
public class FileCacheUtil {
	public static final String TEMP_PREFIX = "istmp";
	public static final String TEMP_SUFFIX = ".jpg";
	public static final File TMP_DIR = new File("/tmp");
	public static final String IMG_FILE_REGEX = "(" + TEMP_PREFIX + ")\\d*(" + TEMP_SUFFIX + ")";

	public static File cacheInputImage(final InputStream inputStream) throws IOException {
		return cacheInputImage(new ByteSource() {
			@Override
			public InputStream openStream() throws IOException {
				return inputStream;
			}
		});
	}

	private static File getTempFile() throws IOException {
		return File.createTempFile(TEMP_PREFIX, TEMP_SUFFIX, TMP_DIR);
	}

	public static File cacheInputImage(ByteSource byteSource) throws IOException {
		final File tempFile = getTempFile();
		byteSource.copyTo(new FileOutputStream(tempFile));
		return tempFile.getAbsoluteFile();
	}

	public static Optional<String> imageIdFromFile(File file) {
		final String fileName = file.getName();
		if (fileName.matches(IMG_FILE_REGEX)) {
			return Optional.of(fileName.substring(TEMP_PREFIX.length(), fileName.length() - TEMP_SUFFIX.length()));
		} else return Optional.empty();
	}

	public static File fileFromId(Optional<String> imageId) throws FileNotFoundException {
		if (imageId.isPresent()) {
			String fileName = format(TEMP_PREFIX + "%s" + TEMP_SUFFIX, imageId.get());
			Preconditions.checkArgument(fileName.matches(IMG_FILE_REGEX), "Incorrect format for imageId: %s (expected=%s)", imageId.get(), IMG_FILE_REGEX);

			return new File(TMP_DIR, fileName);
		} else throw new FileNotFoundException("Can not build file for absent imageId.");
	}


}
