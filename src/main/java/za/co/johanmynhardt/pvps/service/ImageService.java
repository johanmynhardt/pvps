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
}
