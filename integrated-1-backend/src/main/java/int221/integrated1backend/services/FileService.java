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
        if (originalFileName.contains("..")) {
            throw new RuntimeException("Invalid file name: " + originalFileName);
        }

        Optional<Task> taskOptional = taskRepository.findById(taskID);
        if (taskOptional.isEmpty()) {
            throw new RuntimeException("Task not found with ID: " + taskID);
        }
        Task task = taskOptional.get();
        String boardId = task.getBoard().getId();

        Path taskDirectory = rootStorageLocation.resolve(Paths.get(boardId, taskID.toString()));
        if (!Files.exists(taskDirectory)) {
            Files.createDirectories(taskDirectory);
        }

        Path targetLocation = taskDirectory.resolve(originalFileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        Optional<Attachment> existingAttachment = attachmentRepository.findByLocationAndTaskId(targetLocation.toString(), taskID);
        Attachment attachment;
        if (existingAttachment.isPresent()) {
            attachment = existingAttachment.get();
        } else {
            attachment = new Attachment();
        }

        attachment.setTask(task);
        attachment.setLocation(targetLocation.toString());
        attachment.setFileSize((int) file.getSize());
        attachment.setUploadDate(LocalDateTime.now());

        attachmentRepository.save(attachment);

        return "File uploaded successfully to nested task directory: " + targetLocation.toString();
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
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new Exception("File not found with ID: " + attachmentId));

        Path filePath = Paths.get(attachment.getLocation()).toAbsolutePath();
        Path taskDirectoryPath = filePath.getParent();
        Path boardDirectoryPath = taskDirectoryPath.getParent();


        try {
            Files.deleteIfExists(filePath);
        } catch (Exception e) {
            throw new Exception("Failed to delete file: " + e.getMessage());
        }

        attachmentRepository.deleteById(attachmentId);


        try (DirectoryStream<Path> taskDirStream = Files.newDirectoryStream(taskDirectoryPath)) {
            if (!taskDirStream.iterator().hasNext()) {
                Files.deleteIfExists(taskDirectoryPath);
            }
        } catch (Exception e) {
            throw new Exception("Failed to delete task directory: " + e.getMessage());
        }

        try (DirectoryStream<Path> boardDirStream = Files.newDirectoryStream(boardDirectoryPath)) {
            if (!boardDirStream.iterator().hasNext()) {
                Files.deleteIfExists(boardDirectoryPath);
            }
        } catch (Exception e) {
            throw new Exception("Failed to delete board directory: " + e.getMessage());
        }

        return "File and related directories deleted successfully if empty.";
    }


}