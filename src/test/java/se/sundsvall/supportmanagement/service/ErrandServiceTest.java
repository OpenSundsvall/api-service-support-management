package se.sundsvall.supportmanagement.service;

import static java.time.OffsetDateTime.now;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.zalando.problem.Status.NOT_FOUND;
import static se.sundsvall.supportmanagement.api.model.errand.CustomerType.PRIVATE;
import static se.sundsvall.supportmanagement.api.model.errand.Priority.HIGH;

import java.time.OffsetDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.zalando.problem.ThrowableProblem;

import com.turkraft.springfilter.boot.FilterSpecification;

import se.sundsvall.supportmanagement.api.model.errand.Customer;
import se.sundsvall.supportmanagement.api.model.errand.CustomerType;
import se.sundsvall.supportmanagement.api.model.errand.Errand;
import se.sundsvall.supportmanagement.api.model.errand.ExternalTag;
import se.sundsvall.supportmanagement.api.model.errand.Priority;
import se.sundsvall.supportmanagement.integration.db.ErrandsRepository;
import se.sundsvall.supportmanagement.integration.db.model.DbExternalTag;
import se.sundsvall.supportmanagement.integration.db.model.EmbeddableCustomer;
import se.sundsvall.supportmanagement.integration.db.model.ErrandEntity;

@ExtendWith(MockitoExtension.class)
class ErrandServiceTest {
	private static final String ASSIGNED_GROUP_ID = "assignedGroupId";
	private static final String ASSIGNED_USER_ID = "assignedUserId";
	private static final String CATEGORY_TAG = "categoryTag";
	private static final String CLIENT_ID_TAG = "clientIdTag";
	private static final OffsetDateTime CREATED = now().minusWeeks(1);
	private static final String CUSTOMER_ID = "customerId";
	private static final CustomerType CUSTOMER_TYPE = PRIVATE;
	private static final String TAG_KEY = "tagKey";
	private static final String TAG_VALUE = "tagValue";
	private static final String ID = "id";
	private static final OffsetDateTime MODIFIED = now();
	private static final String PRIORITY = HIGH.name();
	private static final String REPORTER_USER_ID = "reporterUserId";
	private static final String STATUS_TAG = "statusTag";
	private static final String TITLE = "title";
	private static final String TYPE_TAG = "typeTag";

	@Mock
	private ErrandsRepository repositoryMock;

	@InjectMocks
	private ErrandService service;

	@Test
	void createErrand() {
		// Setup
		final var errand = createErrandInstance();

		// Mock
		when(repositoryMock.save(any(ErrandEntity.class))).thenReturn(ErrandEntity.create().withId(ID));

		// Call
		final var result = service.createErrand(errand);

		// Assertions and verifications
		assertThat(result).isEqualTo(ID);

		verify(repositoryMock).save(any(ErrandEntity.class));
		verifyNoMoreInteractions(repositoryMock);
	}

	@Test
	void findErrandWithMatches() {
		// Setup
		final Specification<ErrandEntity> filter = new FilterSpecification<>("id: 'uuid'");
		final var sort = Sort.by(DESC, "attribute.1", "attribute.2");
		final Pageable pageable = PageRequest.of(1, 2, sort);

		// Mock
		when(repositoryMock.findAll(filter, pageable)).thenReturn(new PageImpl<>(List.of(createEntityInstance(), createEntityInstance()), pageable, 2L));
		when(repositoryMock.count(filter)).thenReturn(10L);

		// Call
		final var matches = service.findErrands(filter, pageable);

		// Assertions and verifications
		assertThat(matches.getContent()).isNotEmpty().hasSize(2);
		assertThat(matches.getNumberOfElements()).isEqualTo(2);
		assertThat(matches.getTotalElements()).isEqualTo(10);
		assertThat(matches.getTotalPages()).isEqualTo(5);
		assertThat(matches.getPageable()).usingRecursiveComparison().isEqualTo(pageable);
		assertThat(matches.getSort()).usingRecursiveComparison().isEqualTo(sort);

		verify(repositoryMock).findAll(filter, pageable);
		verify(repositoryMock).count(filter);
		verifyNoMoreInteractions(repositoryMock);
	}

	@Test
	void findErrandWithoutMatches() {
		// Setup
		final Specification<ErrandEntity> filter = new FilterSpecification<>("id: 'uuid'");
		final var sort = Sort.by(DESC, "attribute.1", "attribute.2");
		final Pageable pageable = PageRequest.of(3, 7, sort);

		// Mock
		when(repositoryMock.findAll(filter, pageable)).thenReturn(new PageImpl<>(emptyList()));

		// Call
		final var matches = service.findErrands(filter, pageable);

		// Assertions and verifications
		assertThat(matches.getContent()).isEmpty();
		assertThat(matches.getNumberOfElements()).isZero();
		assertThat(matches.getTotalElements()).isZero();
		assertThat(matches.getTotalPages()).isZero();
		assertThat(matches.getPageable()).usingRecursiveComparison().isEqualTo(pageable);
		assertThat(matches.getSort()).usingRecursiveComparison().isEqualTo(sort);

		verify(repositoryMock).findAll(filter, pageable);
		verify(repositoryMock).count(filter);
		verifyNoMoreInteractions(repositoryMock);
	}

	@Test
	void readExistingErrand() {
		// Setup
		final var entity = createEntityInstance();

		// Mock
		when(repositoryMock.existsById(ID)).thenReturn(true);
		when(repositoryMock.getReferenceById(ID)).thenReturn(entity);

		// Call
		final var response = service.readErrand(ID);

		// Assertions and verifications
		assertThat(response.getId()).isEqualTo(ID);

		verify(repositoryMock).existsById(ID);
		verify(repositoryMock).getReferenceById(ID);
		verifyNoMoreInteractions(repositoryMock);
	}

	@Test
	void readNonExistingErrand() {
		// Call
		final var exception = assertThrows(ThrowableProblem.class, () -> service.readErrand(ID));

		// Assertions and verifications
		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getTitle()).isEqualTo(NOT_FOUND.getReasonPhrase());
		assertThat(exception.getMessage()).isEqualTo("Not Found: An errand with id 'id' could not be found");

		verify(repositoryMock).existsById(ID);
		verifyNoMoreInteractions(repositoryMock);
	}

	@Test
	void updateExistingErrand() {
		// Setup
		final var entity = createEntityInstance();

		// Mock
		when(repositoryMock.existsById(ID)).thenReturn(true);
		when(repositoryMock.getReferenceById(ID)).thenReturn(entity);
		when(repositoryMock.save(entity)).thenReturn(entity);

		// Call
		final var response = service.updateErrand(ID, createErrandInstance());

		// Assertions and verifications
		assertThat(response.getId()).isEqualTo(ID);

		verify(repositoryMock).existsById(ID);
		verify(repositoryMock).getReferenceById(ID);
		verify(repositoryMock).save(entity);
		verifyNoMoreInteractions(repositoryMock);
	}

	@Test
	void updateNonExistingErrand() {
		// Call
		final var errand = Errand.create();
		final var exception = assertThrows(ThrowableProblem.class, () -> service.updateErrand(ID, errand));

		// Assertions and verifications
		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getTitle()).isEqualTo(NOT_FOUND.getReasonPhrase());
		assertThat(exception.getMessage()).isEqualTo("Not Found: An errand with id 'id' could not be found");

		verify(repositoryMock).existsById(ID);
		verifyNoMoreInteractions(repositoryMock);
	}

	@Test
	void deleteExistingErrand() {
		// Mock
		when(repositoryMock.existsById(ID)).thenReturn(true);

		// Call
		service.deleteErrand(ID);

		// Assertions and verifications
		verify(repositoryMock).existsById(ID);
		verify(repositoryMock).deleteById(ID);
		verifyNoMoreInteractions(repositoryMock);
	}

	@Test
	void deleteNonExistingErrand() {
		// Call
		final var exception = assertThrows(ThrowableProblem.class, () -> service.deleteErrand(ID));

		// Assertions and verifications
		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getTitle()).isEqualTo(NOT_FOUND.getReasonPhrase());
		assertThat(exception.getMessage()).isEqualTo("Not Found: An errand with id 'id' could not be found");

		verify(repositoryMock).existsById(ID);
		verifyNoMoreInteractions(repositoryMock);
	}

	private static ErrandEntity createEntityInstance() {
		return ErrandEntity.create()
			.withAssignedGroupId(ASSIGNED_GROUP_ID)
			.withAssignedUserId(ASSIGNED_USER_ID)
			.withCategoryTag(CATEGORY_TAG)
			.withClientIdTag(CLIENT_ID_TAG)
			.withCreated(CREATED)
			.withCustomer(EmbeddableCustomer.create().withId(CUSTOMER_ID).withType(CUSTOMER_TYPE.toString()))
			.withExternalTags(List.of(DbExternalTag.create().withKey(TAG_KEY).withValue(TAG_VALUE)))
			.withId(ID)
			.withPriority(PRIORITY)
			.withReporterUserId(REPORTER_USER_ID)
			.withStatusTag(STATUS_TAG)
			.withTitle(TITLE)
			.withTypeTag(TYPE_TAG)
			.withModified(MODIFIED);
	}

	private static Errand createErrandInstance() {
		return Errand.create()
			.withAssignedGroupId(ASSIGNED_GROUP_ID)
			.withAssignedUserId(ASSIGNED_USER_ID)
			.withCategoryTag(CATEGORY_TAG)
			.withClientIdTag(CLIENT_ID_TAG)
			.withCreated(CREATED)
			.withCustomer(Customer.create().withId(CUSTOMER_ID).withType(CUSTOMER_TYPE))
			.withExternalTags(List.of(ExternalTag.create().withKey(TAG_KEY).withValue(TAG_VALUE)))
			.withId(ID)
			.withModified(MODIFIED)
			.withPriority(Priority.valueOf(PRIORITY))
			.withReporterUserId(REPORTER_USER_ID)
			.withStatusTag(STATUS_TAG)
			.withTitle(TITLE)
			.withTypeTag(TYPE_TAG);
	}
}
