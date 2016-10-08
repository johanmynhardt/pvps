package za.co.johanmynhardt.pvps.service;

import static com.google.common.collect.ImmutableMap.of;
import static spark.Spark.get;
import static spark.Spark.post;

import com.google.common.io.ByteSink;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.Request;
import spark.Response;
import spark.servlet.SparkApplication;
import spark.utils.IOUtils;
import za.co.johanmynhardt.pvps.model.JsonResponse;
import za.co.johanmynhardt.pvps.service.impl.ImageServiceImpl;
import za.co.johanmynhardt.pvps.service.util.FileCacheUtil;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.Part;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author johan
 */
public class SparkApp implements SparkApplication {

    private static final Logger LOG = LoggerFactory.getLogger(SparkApp.class);

    private ImageService imageService = new ImageServiceImpl();

    @Override
    public void init() {

        LOG.debug("Initialising SparkApp...");

        get("/spark/ping", (request, response) -> "pong!");

        post("/spark/upload", "multipart/form-data", (request, response) -> {
            configureMultipart(request);

            final List<String> uploadedImages = processUpload(request);

            JsonResponse jresponse = new JsonResponse().setStatus(200).setMessage("success").setProperties(of("images", uploadedImages));
            response.type("application/json");
            response.body(jresponse.toString());

            return response.body();
        });

        get("/spark/:imageId/view", (request, response) -> {
            String imageId = request.params(":imageId");
            fileResponse(response, imageId, false);

            return "";
        });

        get("/spark/:imageId/download", (request, response) -> {
            String imageId = request.params(":imageId");
            fileResponse(response, imageId, true);

            return "";
        });

    }

    private List<String> processUpload(Request request) throws IOException, ServletException {
        List<FileInfo> infos = RequestUtils.fileInfosFromParts(request);
        String resizeSpec = RequestUtils.pullStringFromRequest(request, RequestUtils.FORM_KEY_RESIZE_SPEC).orElse(null);
        final Optional<String> borderSize = RequestUtils.pullStringFromRequest(request, RequestUtils.FORM_KEY_BORDER_SIZE);

        LOG.debug("resizeSpec = {}", resizeSpec);
        LOG.debug("borderSize = {}", borderSize);

        ImageService.ResizeConfigurationWithDefaults resizeConfiguration = (ImageService.ResizeConfigurationWithDefaults) ImageService.DefaultConfigurations
                .findForKey(resizeSpec)
                .orElse(ImageService.DefaultConfigurations.getApa1280to800());

        borderSize.map(Integer::parseInt).ifPresent(width -> resizeConfiguration.borderSize = width);

        LOG.debug("resizeConfiguration: {}", resizeConfiguration);

        return infos.stream()
                .map(fileInfo -> {
                    try {
                        return imageService.resizeImage(fileInfo.getInputStream(request), resizeConfiguration);
                    } catch (IOException | ServletException e) {
                        LOG.error("Error ", e);
                        return Optional.empty();
                    }
                })
                .filter(Optional::isPresent)
                .map(o -> ((String) o.get()))
                .collect(Collectors.toList());
    }

    /**
     * This is to support file-upload in Jetty and Spark Java.
     * Tomcat is configured using context.xml
     */
    private void configureMultipart(Request request) {
        MultipartConfigElement multipartConfigElement = new MultipartConfigElement("/tmp");
        //request.raw().setAttribute("org.eclipse.multipartConfig", multipartConfigElement);
        request.raw().setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement);
    }

    void fileResponse(Response response, String imageId, boolean download) throws IOException {
        File file = FileCacheUtil.fileFromId(Optional.of(imageId));
        response.type("image/jpg");
        if (download) {
            response.header("content-disposition", "attachment; filename=" + imageId + ".jpg");
        }
        new ByteSink() {
            @Override
            public OutputStream openStream() throws IOException {
                return response.raw().getOutputStream();
            }
        }.writeFrom(new FileInputStream(file));
    }

    static class RequestUtils {

        private static final String FORM_KEY_RESIZE_SPEC = "resizeSpec";
        private static final String FORM_KEY_BORDER_SIZE = "borderWidth";

        static List<FileInfo> fileInfosFromParts(Request request) throws IOException, ServletException {
            return request.raw().getParts().stream()
                    .map(RequestUtils::logPart)
                    .map(RequestUtils::contentDisposition)
                    .map(RequestUtils::logContentDisposition)
                    .map(RequestUtils::splitForOptions)
                    .filter(RequestUtils::isFormData)
                    .filter(options -> options.length == 3)
                    .map(strings -> Arrays.stream(strings)
                            .filter(s -> s.contains("="))
                            .map(s -> s.split("=")[1].replaceAll("\"", ""))
                            .collect(Collectors.toList()))
                    .map(strings -> new FileInfo(strings.get(0), strings.get(1)))
                    .collect(Collectors.toList());
        }

        static Optional<String> pullStringFromRequest(Request request, final String key) throws IOException, ServletException {
            return request.raw().getParts().stream()
                    .filter(part -> part.getName().equals(key))
                    .map(resizeSpecPart -> {
                        try {
                            return IOUtils.toString(resizeSpecPart.getInputStream());
                        } catch (IOException e) {
                            LOG.error("Error extracting key {} from request ", key, e);
                            throw new RuntimeException(e);
                        }
                    })
                    .findFirst();
        }

        static String contentDisposition(Part part) {
            return part.getHeader("content-disposition");
        }

        static String[] splitForOptions(String contentDisposition) {
            return contentDisposition.split("; ");
        }

        static String logContentDisposition(String input) {
            LOG.debug("content-disposition: {}", input);
            return input;
        }

        static boolean isFormData(String[] options) {
            return options[0].equals("form-data");
        }

        static Part logPart(Part part) {
            LOG.debug("part: {}", part.getName());
            LOG.debug("part headers: {}", part.getHeaderNames());
            return part;
        }
    }

    static class FileInfo {
        String key;
        String filename;

        public FileInfo(String key, String filename) {
            this.key = key;
            this.filename = filename;
        }

        @Override
        public String toString() {
            return "FileInfo{" + "filename='" + filename + '\'' +
                    ", key='" + key + '\'' +
                    '}';
        }

        public InputStream getInputStream(Request request) throws IOException, ServletException {
            return request.raw().getPart(key).getInputStream();
        }
    }
}
