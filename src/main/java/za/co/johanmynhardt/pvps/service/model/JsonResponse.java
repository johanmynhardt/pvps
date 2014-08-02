package za.co.johanmynhardt.pvps.service.model;

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

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("JsonResponse{");
		sb.append("status=").append(status);
		sb.append(", message='").append(message).append('\'');
		sb.append(", properties=").append(properties);
		sb.append('}');
		return sb.toString();
	}
}
