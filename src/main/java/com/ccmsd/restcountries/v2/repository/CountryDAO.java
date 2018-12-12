package com.ccmsd.restcountries.v2.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ccmsd.restcountries.datasource.FireDataSource;
import com.ccmsd.restcountries.v2.model.Country;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteBatch;
import com.google.cloud.firestore.WriteResult;

@Component
public class CountryDAO<T> {

	@Autowired
	FireDataSource db;

	public List<Country> findAll() {
		List<Country> op = new ArrayList<Country>();
		ApiFuture<QuerySnapshot> query = db.getFireStore().collection("countries").get();
		// ...
		// query.get() blocks on response
		QuerySnapshot querySnapshot = null;
		try {
			querySnapshot = query.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
		for (QueryDocumentSnapshot document : documents) {
			op.add(document.toObject(Country.class));
		}
		return op;
	}

	public Country save(Country country) {
		DocumentReference docRef = db.getFireStore().collection("countries").document(country.getAlpha2Code());
		ApiFuture<WriteResult> result = docRef.set(country);
		try {
			System.out.println("Update time : " + result.get().getUpdateTime());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return country;
	}

	public List<Country> saveAll(List<Country> countries) {
		WriteBatch batch = db.getFireStore().batch();
		for (Country country : countries) {
			DocumentReference docRef = db.getFireStore().collection("countries").document(country.getAlpha2Code());
			batch.set(docRef, country);
		}
		ApiFuture<List<WriteResult>> future = batch.commit();
		try {
			future.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return countries;
	}

	public Optional<Country> findById(String id) {
		ApiFuture<DocumentSnapshot> future = db.getFireStore().collection("countries").document(id).get();
		DocumentSnapshot document = null;
		try {
			document = future.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		if (document.exists()) {
			return Optional.of(document.toObject(Country.class));
		}
		return null;

	}

	public void deleteById(String id) {
		ApiFuture<WriteResult> writeResult = db.getFireStore().collection("countries").document(id).delete();
		try {
			System.out.println("Update time : " + writeResult.get().getUpdateTime());
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

	}

	void deleteCollection(CollectionReference collection, int batchSize) {
		try {
			// retrieve a small batch of documents to avoid out-of-memory errors
			ApiFuture<QuerySnapshot> future = collection.limit(batchSize).get();
			int deleted = 0;
			// future.get() blocks on document retrieval
			List<QueryDocumentSnapshot> documents = future.get().getDocuments();
			for (QueryDocumentSnapshot document : documents) {
				document.getReference().delete();
				++deleted;
			}
			if (deleted >= batchSize) {
				// retrieve and delete another batch
				deleteCollection(collection, batchSize);
			}
		} catch (Exception e) {
			System.err.println("Error deleting collection : " + e.getMessage());
		}
	}

	public List<Country> findByQuery(String name) {

		List<Country> op = new ArrayList<Country>();
		ApiFuture<QuerySnapshot> future = db.getFireStore().collection("countries").whereEqualTo("name", name).get();
		try {
			List<QueryDocumentSnapshot> documents = future.get().getDocuments();
			for (QueryDocumentSnapshot document : documents) {
				op.add(document.toObject(Country.class));
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return op;
	}

}
