package za.co.johanmynhardt.pvps.service.rest;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;

import za.co.johanmynhardt.pvps.service.ImageService;
import za.co.johanmynhardt.pvps.service.impl.ImageServiceImpl;
import za.co.johanmynhardt.pvps.model.JsonResponse;
import za.co.johanmynhardt.pvps.service.util.FileCacheUtil;

@Path("/file")
public class FileService {
	public static final String IMAGE_ID = "imageId";
	public static final String IMAGE_JPG = "image/jpg";
	public static final String CONTENT_DISPOSITION = "Content-Disposition";
	private static final Logger LOG = LoggerFactory.getLogger(FileService.class);

	private ImageService imageService = new ImageServiceImpl();
	private Gson gson = new GsonBuilder().create();

	@GET
	public String getDefault() {
		return "pong!";
	}

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/upload")
	public Response consumeUpload(MultipartFormDataInput input) {
		Map<String, InputStream> attachmentMap = FormUtil.getInputStreams(input);

		List<String> uploadedImages = new ArrayList<>();

		String resizeSpec = FormUtil.getResizeSpec(input);
		try {
			for (InputStream inputStream : attachmentMap.values()) {
				Optional<String> imageId = imageService.resizeImage(inputStream, resizeSpec);
				if (imageId.isPresent()) {
					uploadedImages.add(imageId.get());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		JsonResponse response = new JsonResponse();
		response.setStatus(200);
		response.setMessage("success");
		response.setProperties(
				new ImmutableMap.Builder<String, Object>()
						.put("images", uploadedImages)
						.build()
		);

		String json = gson.toJson(response, new TypeToken<JsonResponse>() {
		}.getType());

		LOG.debug("Returning JsonResponse: {}", json);

		return Response.ok(json, MediaType.APPLICATION_JSON_TYPE).build();
	}

	@GET
	@Path("/{imageId}/view")
	@Produces({"image/*", MediaType.TEXT_PLAIN})
	public Response viewImage(@PathParam(IMAGE_ID) String imageId) {
		try {
			File response = FileCacheUtil.fileFromId(Optional.of(imageId));
			return Response.ok(response, IMAGE_JPG).build();
		} catch (FileNotFoundException e) {
			LOG.error("Error", e);
			return Response.status(Response.Status.NOT_FOUND).entity("The requested item is not found").build();
		}
	}

	@GET
	@Path("/{imageId}/download")
	@Produces({"image/*", MediaType.TEXT_PLAIN})
	public Response downloadImage(@PathParam(IMAGE_ID) String imageId) {
		try {
			File response = FileCacheUtil.fileFromId(Optional.of(imageId));
			return Response.ok(response, IMAGE_JPG).header(CONTENT_DISPOSITION, format("attachment; filename=%s.jpg", imageId)).build();
		} catch (FileNotFoundException e) {
			LOG.error("Error", e);
			return Response.status(Response.Status.NOT_FOUND).entity("The requested item is not found").build();
		}
	}

	private static class FormUtil {
		public static Map<String, InputStream> getInputStreams(MultipartFormDataInput form) {

			Map<String, InputStream> inputMap = Collections.emptyMap();

			Map<String, List<InputPart>> formData = form.getFormDataMap();

			LOG.debug("formData keys = {}", formData.keySet());

			for (String key : formData.keySet()) {
				for (InputPart inputPart : formData.get(key)) {
					if (inputPart.getHeaders().containsKey(CONTENT_DISPOSITION)) {
						List<String> values = Arrays.asList(inputPart.getHeaders().get(CONTENT_DISPOSITION).get(0).split("; "));
						String filename = null;
						for (String value : values) {
							if (value.startsWith("filename=")) {
								filename = value.split("filename=", 2)[1];
								if (filename.contains("\"")) {
									filename = filename.replaceAll("\"", "");
								}
								break;
							}
						}

						if (filename != null) {
							if (inputMap.isEmpty()) {
								inputMap = new HashMap<>();
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

		public static String getResizeSpec(MultipartFormDataInput input) {
			if (input.getFormDataMap().containsKey("resizeSpec")) {
				final List<InputPart> resizeSpec = input.getFormDataMap().get("resizeSpec");

				try {
					return resizeSpec.get(0).getBodyAsString();
				} catch (IOException e) {
					LOG.error("Error ", e);
				}
			}
			return "APA";
		}
	}
}
// vim: tabstop=2
