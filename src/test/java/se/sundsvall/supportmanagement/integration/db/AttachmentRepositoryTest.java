package se.sundsvall.supportmanagement.integration.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import se.sundsvall.supportmanagement.integration.db.model.AttachmentEntity;

import javax.transaction.Transactional;
import java.time.OffsetDateTime;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

/**
 * Attachment repository tests.
 *
 * @see src/test/resources/db/testdata.sql for data setup.
 */
@SpringBootTest
@ActiveProfiles("junit")
@Sql({
	"/db/scripts/truncate.sql",
	"/db/scripts/testdata-junit.sql"
})
class AttachmentRepositoryTest {

	@Autowired
	private AttachmentRepository attachmentRepository;

	@Autowired
	private ErrandsRepository errandsRepository;

	@Test
	@Transactional
	void create() {

		final var fileName = "TestAttachment.txt";
		final var file = "TestAttachment".getBytes();
		final var mimeType = "text/plain";
		final var attachmentEntity = AttachmentEntity.create().withFile(file).withFileName(fileName).withMimeType(mimeType);


		assertThat(attachmentRepository.findByFileNameIgnoreCase(fileName)).isNotNull().isEmpty();

		final var errandEntity = errandsRepository.findById("ERRAND_ID-2").get();
		attachmentEntity.setErrandEntity(errandEntity);

		// Execution
		attachmentRepository.save(attachmentEntity);

		// Assertions
		final var persistedEntities = attachmentRepository.findByFileNameIgnoreCase(fileName);

		assertThat(persistedEntities).isNotNull().hasSize(1);
		assertThat(persistedEntities.get(0).getFileName()).isEqualTo(fileName);
		assertThat(persistedEntities.get(0).getFile()).isEqualTo(file);
		assertThat(persistedEntities.get(0).getMimeType()).isEqualTo(mimeType);
		assertThat(persistedEntities.get(0).getErrandEntity()).isEqualTo(errandEntity);
		assertThat(persistedEntities.get(0).getCreated()).isCloseTo(OffsetDateTime.now(), within(2, SECONDS));
		assertThat(persistedEntities.get(0).getModified()).isNull();
	}

	@Test
	void update() {

		// Setup
		final var oldFileName = "test.txt";
		final var existingAttachment = attachmentRepository.findByFileNameIgnoreCase(oldFileName).get(0);
		final var newFile = "TestAttachment".getBytes();
		final var newFileName = "UpdatedAttachment.txt";

		// Execution
		existingAttachment.setFile(newFile);
		existingAttachment.setFileName(newFileName);
		attachmentRepository.save(existingAttachment);

		// Assertions
		assertThat(attachmentRepository.findByFileNameIgnoreCase(oldFileName)).isNotNull().isEmpty();
		final var updatedAttachments = attachmentRepository.findByFileNameIgnoreCase(newFileName);

		assertThat(updatedAttachments).isNotNull().hasSize(1);
		assertThat(updatedAttachments.get(0).getFileName()).isEqualTo(newFileName);
		assertThat(updatedAttachments.get(0).getFile()).isEqualTo(newFile);
		assertThat(updatedAttachments.get(0).getModified()).isCloseTo(OffsetDateTime.now(), within(2, SECONDS));
	}

	@Test
	void delete() {

		final var id = "ATTACHMENT_ID-3";
		// Setup
		final var existingAttachment = attachmentRepository.findById(id).orElseThrow();

		// Execution
		attachmentRepository.delete(existingAttachment);

		// Assertions
		assertThat(attachmentRepository.findById(id)).isNotPresent();
	}

	@Test
	void findByFileNameNull() {
		assertThat(attachmentRepository.findByFileNameIgnoreCase(null)).isEmpty();
	}

	@Test
	void findByFileNameNotFound() {
		assertThat(attachmentRepository.findByFileNameIgnoreCase("NotExistingName")).isEmpty();
	}
}
