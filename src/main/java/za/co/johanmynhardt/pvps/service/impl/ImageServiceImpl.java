package za.co.johanmynhardt.pvps.service.impl;

import com.google.common.base.Optional;

import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static java.lang.String.format;
import static za.co.johanmynhardt.pvps.service.util.FileCacheUtil.cacheInputImage;
import static za.co.johanmynhardt.pvps.service.util.FileCacheUtil.imageIdFromFile;

import za.co.johanmynhardt.pvps.service.ImageService;
import za.co.johanmynhardt.pvps.service.util.FileCacheUtil;

/**
 * @author Johan Mynhardt
 */
public class ImageServiceImpl implements ImageService {
	private static final Logger LOG = LoggerFactory.getLogger(ImageServiceImpl.class);

	@Override
	public Optional<String> resizeImage(InputStream inputStream) throws IOException {
		return resizeImage(inputStream, DefaultConfigurations.getApa1280to800());
	}

	@Override
	public Optional<String> resizeImage(InputStream inputStream, ResizeConfiguration resizeConfiguration) throws IOException {
		File result = cacheInputImage(inputStream);

		IMOperation operation = new IMOperation();
		operation.define(format("jpeg:extent=%dkb", resizeConfiguration.getMaxKiloBytes()));
		operation.bordercolor(resizeConfiguration.getBorderColour());
		operation.border(resizeConfiguration.getBorderSize(), resizeConfiguration.getBorderSize());
		operation.addImage(result.toString());
		operation.resize(resizeConfiguration.getWidth(), resizeConfiguration.getHeight(), '>');
		operation.addImage(result.toString());

		LOG.debug("operation = {}", operation);

		try {
			new ConvertCmd().run(operation);
		} catch (InterruptedException | IM4JavaException e) {
			LOG.error("Error", e);
		}

		return imageIdFromFile(result);
	}

	@Override
	public File imageFile(InputStream inputStream) throws IOException {
		Optional<String> imageId = resizeImage(inputStream);
		return FileCacheUtil.fileFromId(imageId);
	}
}
