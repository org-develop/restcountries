package com.ccmsd.restcountries.datasource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;


@Component
public class FireDataSource {

    private Firestore db;

    FireDataSource() throws IOException {
        initialize();
    }

    private void initialize() throws IOException {

        // ClassLoader classLoader = new CountryRestController().getClass().getClassLoader();
        // File file = new File(classLoader.getResource("zeroapp-bb183-9c576ec39170.json").getFile());
        File file = ResourceUtils.getFile("classpath:zeroapp-bb183-9c576ec39170.json");
        InputStream serviceAccount = new FileInputStream(file);
        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(credentials)
                .build();
        FirebaseApp.initializeApp(options);
        db = FirestoreClient.getFirestore();
    }

    public Firestore getFireStore() {
        return db;
    }
}
