package za.co.johanmynhardt.pvps.service.rest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import za.co.johanmynhardt.pvps.service.ImageService;
import za.co.johanmynhardt.pvps.service.impl.ImageServiceImpl;

@Path("/file")
public class FileService {
	private static Logger logger = Logger.getLogger(FileService.class.getName());

	ImageService imageService = new ImageServiceImpl();

	@GET
	public String getDefault() {
		return "pong!";
	}

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Path("/upload")
	public String consumeUpload(MultipartFormDataInput input) {
		Map<String, InputStream> attachmentMap = FormUtil.getInputStreams(input);

		System.out.println("attachmentMap = " + attachmentMap.keySet());

		try {

			for (InputStream inputStream : attachmentMap.values()) {
				File file = imageService.resizeImage(inputStream);
				System.out.println("file = " + file);
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		return "success";
	}

	private static class FormUtil {
		public static Map<String, InputStream> getInputStreams(MultipartFormDataInput form) {
			Map<String, InputStream> inputMap = Collections.emptyMap();

			Map<String, List<InputPart>> formData = form.getFormDataMap();

			for (String key : formData.keySet()) {
				for (InputPart inputPart : formData.get(key)) {
					if (inputPart.getHeaders().containsKey("Content-Disposition")) {
						List<String> values = Arrays.asList(inputPart.getHeaders().get("Content-Disposition").get(0).split("; "));
						String filename = null;
						for (String value : values) {
							if (value.startsWith("filename=")) {
								filename = value.split("filename=",2)[1];
								if (filename.contains("\"")) {
									filename = filename.replaceAll("\"", "");
								}
								break;
							}
						}

						if (filename != null) {
							if (inputMap.isEmpty()) {
								inputMap = new HashMap<String, InputStream>();
							}
							try {
								inputMap.put(filename, inputPart.getBody(InputStream.class, null));
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
			return inputMap;
		}
	}
}
// vim: tabstop=2
