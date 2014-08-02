package za.co.johanmynhardt.pvps.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.google.common.base.Optional;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import za.co.johanmynhardt.pvps.service.ImageService;
import za.co.johanmynhardt.pvps.service.util.FileCacheUtil;

import static za.co.johanmynhardt.pvps.service.util.FileCacheUtil.cacheInputImage;
import static za.co.johanmynhardt.pvps.service.util.FileCacheUtil.imageIdFromFile;

/**
 * @author Johan Mynhardt
 */
public class ImageServiceImpl implements ImageService {
	@Override
	public Optional<String> resizeImage(InputStream inputStream) throws IOException {

		File result = cacheInputImage(inputStream);

		IMOperation operation = new IMOperation();
		operation.define("jpeg:extent=500kb");
		operation.bordercolor("white");
		operation.border(20, 20);
		operation.addImage(result.toString());
		operation.resize(1024, 768, '>');
		operation.addImage(result.toString());

		System.out.println("operation = " + operation);

		try {
			new ConvertCmd().run(operation);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IM4JavaException e) {
			e.printStackTrace();
		}

		return imageIdFromFile(result);
	}

	@Override
	public File imageFile(InputStream inputStream) throws IOException {
		Optional<String> imageId = resizeImage(inputStream);
		return FileCacheUtil.fileFromId(imageId);
	}
}
