package se.sundsvall.supportmanagement.service;

import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.zalando.problem.Status.NOT_FOUND;
import static se.sundsvall.supportmanagement.service.mapper.ErrandNoteMapper.toCreateNoteRequest;
import static se.sundsvall.supportmanagement.service.mapper.ErrandNoteMapper.toErrandNote;
import static se.sundsvall.supportmanagement.service.mapper.ErrandNoteMapper.toFindErrandNotesResponse;
import static se.sundsvall.supportmanagement.service.mapper.ErrandNoteMapper.toUpdateNoteRequest;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;

import se.sundsvall.supportmanagement.api.model.note.CreateErrandNoteRequest;
import se.sundsvall.supportmanagement.api.model.note.ErrandNote;
import se.sundsvall.supportmanagement.api.model.note.FindErrandNotesRequest;
import se.sundsvall.supportmanagement.api.model.note.FindErrandNotesResponse;
import se.sundsvall.supportmanagement.api.model.note.UpdateErrandNoteRequest;
import se.sundsvall.supportmanagement.integration.db.ErrandsRepository;
import se.sundsvall.supportmanagement.integration.notes.NotesClient;

@Service
public class ErrandNoteService {

	private static final String ERRAND_ENTITY_NOT_FOUND = "An errand with id '%s' could not be found";

	@Value("${spring.application.name:}")
	private String clientId;

	@Autowired
	private NotesClient notesClient;

	@Autowired
	private ErrandsRepository errandsRepository;

	public String createErrandNote(final String id, final CreateErrandNoteRequest createErrandNoteRequest) {
		return extractNoteIdFromLocationHeader(notesClient.createNote(toCreateNoteRequest(id, clientId, createErrandNoteRequest)));
	}

	public ErrandNote readErrandNote(final String id, final String noteId) {

		if (!errandsRepository.existsById(id)) {
			throw Problem.valueOf(NOT_FOUND, String.format(ERRAND_ENTITY_NOT_FOUND, id));
		}

		return toErrandNote(notesClient.findNoteById(noteId));
	}

	public FindErrandNotesResponse findErrandNotes(final String id, final FindErrandNotesRequest findErrandNotesRequest) {

		if (!errandsRepository.existsById(id)) {
			throw Problem.valueOf(NOT_FOUND, String.format(ERRAND_ENTITY_NOT_FOUND, id));
		}

		return toFindErrandNotesResponse(notesClient.findNotes(
			findErrandNotesRequest.getContext(),
			findErrandNotesRequest.getRole(),
			clientId,
			findErrandNotesRequest.getPage(),
			findErrandNotesRequest.getLimit()));
	}

	public ErrandNote updateErrandNote(final String id, final String noteId, final UpdateErrandNoteRequest updateErrandNoteRequest) {

		if (!errandsRepository.existsById(id)) {
			throw Problem.valueOf(NOT_FOUND, String.format(ERRAND_ENTITY_NOT_FOUND, id));
		}

		return toErrandNote(notesClient.updateNoteById(noteId, toUpdateNoteRequest(updateErrandNoteRequest)));
	}

	public void deleteErrandNote(final String id, final String noteId) {

		if (!errandsRepository.existsById(id)) {
			throw Problem.valueOf(NOT_FOUND, String.format(ERRAND_ENTITY_NOT_FOUND, id));
		}

		notesClient.deleteNoteById(noteId);
	}

	private String extractNoteIdFromLocationHeader(final ResponseEntity<Void> response) {
		final var locationValue = Optional.ofNullable(response.getHeaders().get(LOCATION)).orElse(emptyList()).stream().findFirst();
		if (locationValue.isPresent()) {
			return locationValue.get().substring(locationValue.get().lastIndexOf('/') + 1);
		}
		return EMPTY;
	}
}
