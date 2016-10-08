package za.co.johanmynhardt.pvps.model;

import com.google.gson.Gson;

import java.util.Map;

/**
 * @author Johan Mynhardt
 */
public class JsonResponse {
	private int status;
	private String message;
	private Map<String, Object> properties;

	public int getStatus() {
		return status;
	}

	public JsonResponse setStatus(int status) {
		this.status = status;
		return this;
	}

	public String getMessage() {
		return message;
	}

	public JsonResponse setMessage(String message) {
		this.message = message;
		return this;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	public JsonResponse setProperties(Map<String, Object> properties) {
		this.properties = properties;
		return this;
	}

	static final Gson gson = new Gson();

	@Override
	public String toString() {
		return gson.toJson(this);
	}
}
