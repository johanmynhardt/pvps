package za.co.johanmynhardt.pvps.service.rest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import com.sun.jersey.multipart.FormDataParam;
import za.co.johanmynhardt.pvps.service.ImageService;
import za.co.johanmynhardt.pvps.service.impl.ImageServiceImpl;

@Path("/file")
public class FileService {

	ImageService imageService = new ImageServiceImpl();

	@GET
	public String getDefault() {
		return "pong!";
	}

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Path("/upload")
	public String consumeUpload(@FormDataParam("file") InputStream body) {

		try {
			File file = imageService.resizeImage(body);
			System.out.println("file = " + file);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		return "success";
	}
}
// vim: tabstop=2
