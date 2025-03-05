package com.castaneda.oauth2login.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.model.*;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@Service
public class GoogleContactsService {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String APPLICATION_NAME = "Google Contacts Integration";

    public PeopleService getPeopleService(OAuth2AuthorizedClient authorizedClient) throws GeneralSecurityException, IOException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        GoogleCredential credential = new GoogleCredential()
                .setAccessToken(authorizedClient.getAccessToken().getTokenValue());

        return new PeopleService.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public List<Person> getContacts(OAuth2AuthorizedClient authorizedClient) throws GeneralSecurityException, IOException {
        PeopleService peopleService = getPeopleService(authorizedClient);

        ListConnectionsResponse response = peopleService.people().connections()
                .list("people/me")
                .setPageSize(100)
                .setPersonFields("names,emailAddresses,phoneNumbers")
                .execute();

        return response.getConnections();
    }

    public Person createContact(OAuth2AuthorizedClient authorizedClient, Person contact) throws GeneralSecurityException, IOException {
        PeopleService peopleService = getPeopleService(authorizedClient);

        return peopleService.people().createContact(contact)
                .setFields("names,emailAddresses,phoneNumbers")
                .execute();
    }

    public Person updateContact(OAuth2AuthorizedClient authorizedClient, String resourceName, Person contact) throws GeneralSecurityException, IOException {
        PeopleService peopleService = getPeopleService(authorizedClient);

        return peopleService.people().updateContact(resourceName, contact)
                .setUpdatePersonFields("names,emailAddresses,phoneNumbers")
                .execute();
    }

    public void deleteContact(OAuth2AuthorizedClient authorizedClient, String resourceName) throws GeneralSecurityException, IOException {
        PeopleService peopleService = getPeopleService(authorizedClient);

        peopleService.people().deleteContact(resourceName).execute();
    }
}