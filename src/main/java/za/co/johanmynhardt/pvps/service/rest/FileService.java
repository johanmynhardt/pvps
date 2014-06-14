package za.co.johanmynhardt.pvps.service.rest;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import com.sun.jersey.multipart.FormDataParam;

@Path("/file")
public class FileService {

	@GET
	public String getDefault() {
		return "pong!";
	}

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Path("/upload")
	public String consumeUpload(@FormDataParam("file")InputStream body) {
			System.out.println("inputStream == null: " + body == null);
			return "success";
	}
}
// vim: tabstop=2
