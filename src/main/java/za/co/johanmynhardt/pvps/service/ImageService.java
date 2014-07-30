package za.co.johanmynhardt.pvps.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Johan Mynhardt
 */
public interface ImageService {

	public File resizeImage(InputStream inputStream) throws IOException;
}
