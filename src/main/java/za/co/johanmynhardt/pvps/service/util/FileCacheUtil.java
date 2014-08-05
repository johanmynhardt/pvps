package za.co.johanmynhardt.pvps.service.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.google.common.base.Optional;

import static java.lang.String.format;

/**
 * @author Johan Mynhardt
 */
public class FileCacheUtil {
	public static final String TEMP_PREFIX = "istmp";
	public static final String TEMP_SUFFIX = ".jpg";
	public static final File TMP_DIR = new File("/tmp");

	public static File cacheInputImage(InputStream inputStream) throws IOException {
		File result = File.createTempFile(TEMP_PREFIX, TEMP_SUFFIX, TMP_DIR);

		FileOutputStream fileOutputStream = new FileOutputStream(result);

		byte[] buff = new byte[1024];
		int len;
		while ((len = inputStream.read(buff)) > -1) {
			fileOutputStream.write(buff, 0, len);
		}

		fileOutputStream.close();
		inputStream.close();

		return result.getAbsoluteFile();
	}

	public static Optional<String> imageIdFromFile(File file) {
		final String fileName = file.getName();
		if (fileName.matches("(" + TEMP_PREFIX + ").*(" + TEMP_SUFFIX + ")")) {
			return Optional.of(fileName.substring(TEMP_PREFIX.length(), fileName.length() - TEMP_SUFFIX.length()));
		} else return Optional.absent();
	}

	public static File fileFromId(Optional<String> imageId) throws FileNotFoundException {
		if (imageId.isPresent()) {
			return new File(TMP_DIR, format(TEMP_PREFIX + "%s" + TEMP_SUFFIX, imageId.get()));
		} else throw new FileNotFoundException("Can not build file for absent imageId.");
	}


}
