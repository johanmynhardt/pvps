package za.co.johanmynhardt.pvps.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import za.co.johanmynhardt.pvps.service.ImageService;

/**
 * @author Johan Mynhardt
 */
public class ImageServiceImpl implements ImageService {
	@Override
	public File resizeImage(InputStream inputStream) throws IOException {

		File result = new File(cacheInputImage(inputStream));

		ConvertCmd convertCmd = new ConvertCmd();

		IMOperation operation = new IMOperation();
		operation.addImage(result.toString());
		operation.resize(1024,768, '>');
		operation.addImage(result.toString());

		System.out.println("operation = " + operation);

		try {
			convertCmd.run(operation);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IM4JavaException e) {
			e.printStackTrace();
		}


		return result;
	}

	private String cacheInputImage(InputStream inputStream) throws IOException {
		File result = File.createTempFile("istmp", ".jpg");

		FileOutputStream fileOutputStream = new FileOutputStream(result);

		byte[] buff = new byte[1024];
		int len = 0;
		while ((len = inputStream.read(buff)) > -1) {
			fileOutputStream.write(buff, 0, len);
		}

		fileOutputStream.close();
		inputStream.close();

		return result.getAbsoluteFile().toString();
	}
}
