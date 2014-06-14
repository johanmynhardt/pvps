package za.co.johanmynhardt.pvps.service.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/file")
public class FileService {

	@GET
	public String getDefault() {
		return "pong!";
	}
}
// vim: tabstop=2
