package org.example.model;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CloudStorageManager  {
    private final Storage storage;
    private final String bucketName;
    private final DatabaseConnector dbConnector;
    PropertiesLoader loader;
    private static CloudStorageManager instance;

    private CloudStorageManager()  {
        dbConnector = new DatabaseConnector();
        loader = new PropertiesLoader("cloudStorage.properties");
        Properties properties = loader.getProperties();
        bucketName = properties.getProperty("google.cloud.storage.bucket.name");
        try {
            storage = StorageOptions.newBuilder().setCredentials(ServiceAccountCredentials.fromStream(new FileInputStream(properties.getProperty("google.cloud.storage.credentials.file")))).build().getService();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static CloudStorageManager getInstance() {
        if (instance == null) {
            instance = new CloudStorageManager();
        }
        return instance;
    }
     // generuje unikalna nazwe dla pliku poprzez polaczenie nazwy pliku i user_id
    public String generateUniqueFileName(File selectedFile) throws SQLException {
        int user_id = getUserId();
        return selectedFile.getName() + "_" + user_id;
    }

    private int getUserId() throws SQLException {
        PreparedStatement preparedStatement = null;
        String email = SharedVariables.getInstance().getEmail();
        ResultSet rs = null;
        int user_id = -1;
        try (Connection connection = dbConnector.connect()) {
            preparedStatement = connection.prepareStatement("SELECT user_id FROM users WHERE email = ?");
            preparedStatement.setString(1, email);
            rs = preparedStatement.executeQuery();
            try {
                if (rs.next()) {
                    user_id = rs.getInt("user_id");
                    System.out.println(user_id);
                }
            } catch (SQLException e) {e.printStackTrace();}
        }
        return user_id;
    }

    // wysylanie pliku do chmury
    public Blob sendFileToCloudStorage(File fileName) throws IOException, SQLException {
        String uniqueFN = generateUniqueFileName(fileName);
        byte[] content = Files.readAllBytes(fileName.toPath());
        BlobId blobId = BlobId.of(bucketName, uniqueFN);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        return storage.create(blobInfo, content);
    }
    // pobieranie plików i zapisywanie scieżek do nich w liscie
    public List<String> downloadFilesWithUserId() throws SQLException {
        int user_id = getUserId();
        List<String> downloadedFiles = new ArrayList<>();
        Bucket bucket = storage.get(bucketName);

        try {
            if (bucket != null && bucket.list().iterateAll().iterator().hasNext()) {
                for (Blob blob : bucket.list().iterateAll()) {
                    if (user_id == bucket.getName().lastIndexOf('_' + 1)) {
                        String originalFileName = blob.getName().substring(0, blob.getName().lastIndexOf('_'));
                        String destinationDir = checkIfExiOrMDir();
                        String destinationFilePath = Paths.get(destinationDir, originalFileName).toString();
                        blob.downloadTo(Paths.get(destinationFilePath));
                        downloadedFiles.add(destinationFilePath);
                    }
                }
            } else throw new Exception("Nie ma zasobnika.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return downloadedFiles;
    }
    public String checkIfExiOrMDir() {
        Path newFolder = Path.of("C:/PoczytajMILibrary");
        if (Files.notExists(newFolder)) {
            try {
                Files.createDirectories(newFolder);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return newFolder.toString();
    }

}
