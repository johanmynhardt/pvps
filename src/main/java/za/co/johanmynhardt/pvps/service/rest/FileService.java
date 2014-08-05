package za.co.johanmynhardt.pvps.service.rest;

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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import za.co.johanmynhardt.pvps.service.ImageService;
import za.co.johanmynhardt.pvps.service.impl.ImageServiceImpl;
import za.co.johanmynhardt.pvps.service.model.JsonResponse;
import za.co.johanmynhardt.pvps.service.util.FileCacheUtil;

import static java.lang.String.format;

@Path("/file")
public class FileService {
	private static Logger logger = Logger.getLogger(FileService.class.getName());

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

		List<String> uploadedImages = new ArrayList<String>();
		try {

			for (InputStream inputStream : attachmentMap.values()) {
				Optional<String> imageId = imageService.resizeImage(inputStream);
				if (imageId.isPresent()) {
					System.out.println("fileId = " + imageId.get());
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

		logger.log(Level.INFO, "Returning JsonResponse: {0}", json);

		return Response.ok(json, MediaType.APPLICATION_JSON_TYPE).build();
	}

	@GET
	@Path("/{imageId}/view")
	@Produces({"image/*", MediaType.TEXT_PLAIN})
	public Response viewImage(@PathParam("imageId") String imageId) {
		try {
			File response = FileCacheUtil.fileFromId(Optional.of(imageId));
			return Response.ok(response, "image/jpg").build();
		} catch (FileNotFoundException e) {
			logger.warning(e.toString());
			return Response.status(Response.Status.NOT_FOUND).entity("The requested item is not found").build();
		}
	}

	@GET
	@Path("/{imageId}/download")
	@Produces({"image/*", MediaType.TEXT_PLAIN})
	public Response downloadImage(@PathParam("imageId") String imageId) {
		try {
			File response = FileCacheUtil.fileFromId(Optional.of(imageId));
			return Response.ok(response, "image/jpg").header("Content-Disposition", format("attachment; filename=%s.jpg", imageId)).build();
		} catch (FileNotFoundException e) {
			logger.warning(e.toString());
			return Response.status(Response.Status.NOT_FOUND).entity("The requested item is not found").build();
		}
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
								filename = value.split("filename=", 2)[1];
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
