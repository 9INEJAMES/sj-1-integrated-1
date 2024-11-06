package int221.integrated1backend.services;

import int221.integrated1backend.configs.FileStorageProperties;
import int221.integrated1backend.entities.in.Attachment;
import int221.integrated1backend.entities.in.Task;
import int221.integrated1backend.repositories.in.AttachmentRepository;
import int221.integrated1backend.repositories.in.TaskRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;



@Service
public class FileService {
    private final Path rootStorageLocation;
    @Autowired
    private AttachmentRepository attachmentRepository;
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    public FileService(FileStorageProperties fileStorageProperties,
                       AttachmentRepository attachmentRepository,
                       TaskRepository taskRepository) {
        this.attachmentRepository = attachmentRepository;
        this.taskRepository = taskRepository;
        this.rootStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();
        try {
            if (!Files.exists(this.rootStorageLocation))
                Files.createDirectories(this.rootStorageLocation);
        } catch (IOException ex) {
            throw new RuntimeException("Could not create the root directory for uploaded files", ex);
        }
    }

    public String storeAttachment(MultipartFile file, Integer taskID) throws IOException {
        String originalFileName = Objects.requireNonNull(file.getOriginalFilename());
        System.out.println(file.getOriginalFilename());
        if (originalFileName.contains("..")) {
            throw new RuntimeException("Invalid file name: " + originalFileName);
        }


        Optional<Task> taskOptional = taskRepository.findById(taskID);
        if (taskOptional.isEmpty()) {
            throw new RuntimeException("Task not found with ID: " + taskID);
        }
        Task task = taskOptional.get();
        String boardName = task.getBoard().getName();

        Path taskDirectory = rootStorageLocation.resolve( boardName  + taskID);
        if (!Files.exists(taskDirectory)) {
            Files.createDirectories(taskDirectory);
        }

        Path targetLocation = taskDirectory.resolve(originalFileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        Attachment attachment = new Attachment();
        attachment.setTask(task);
        attachment.setLocation(targetLocation.toString());
        attachment.setFileSize((int) file.getSize());
        attachment.setUploadDate(LocalDateTime.now());

        attachmentRepository.save(attachment);

        return "File uploaded successfully to task-specific directory: " + targetLocation.toString();
    }



    public ResponseEntity<Resource> loadFile(Integer attachmentId) throws Exception {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new Exception("File not found with ID: " + attachmentId));

        Path filePath = Paths.get(attachment.getLocation()).toAbsolutePath();
        Resource resource = new UrlResource(filePath.toUri());
        System.out.println( resource);

        if (resource.exists() && resource.isReadable()) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } else {
            throw new Exception("File not found or not readable: " + attachment.getLocation());
        }
    }

    public String deleteFile(Integer attachmentId) throws Exception {
        // Fetch file information from the database
        Attachment fileEntity =  attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new Exception("File not found with ID: " + attachmentId));

        Path filePath = Paths.get(fileEntity.getLocation()).toAbsolutePath();
        Path directoryPath = filePath.getParent();

        try {
            Files.deleteIfExists(filePath);
        } catch (Exception e) {
            throw new Exception("Failed to delete file: " + e.getMessage());
        }

        // Delete the file entry from the database
        attachmentRepository.deleteById(attachmentId);

        // Check if the directory is empty, and if so, delete it
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directoryPath)) {
            if (!directoryStream.iterator().hasNext()) {
                Files.deleteIfExists(directoryPath);
            }
        } catch (Exception e) {
            throw new Exception("Failed to delete directory: " + e.getMessage());
        }

        return "File and directory deleted successfully.";
    }

}
