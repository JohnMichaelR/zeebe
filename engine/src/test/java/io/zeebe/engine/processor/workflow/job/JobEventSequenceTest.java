/*
 * Zeebe Workflow Engine
 * Copyright © 2017 camunda services GmbH (info@camunda.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.zeebe.engine.processor.workflow.job;

import static io.zeebe.protocol.record.intent.JobBatchIntent.ACTIVATE;
import static io.zeebe.protocol.record.intent.JobIntent.ACTIVATED;
import static io.zeebe.protocol.record.intent.JobIntent.CANCEL;
import static io.zeebe.protocol.record.intent.JobIntent.CANCELED;
import static io.zeebe.protocol.record.intent.JobIntent.CREATE;
import static io.zeebe.protocol.record.intent.JobIntent.CREATED;
import static org.assertj.core.api.Assertions.assertThat;

import io.zeebe.engine.util.ExplicitEngineRule;
import io.zeebe.engine.util.RecordSequence;
import io.zeebe.engine.util.RecordSequences;
import io.zeebe.engine.util.SingleSequence;
import io.zeebe.protocol.impl.record.CopiedRecord;
import io.zeebe.protocol.impl.record.RecordMetadata;
import io.zeebe.protocol.impl.record.value.job.JobBatchRecord;
import io.zeebe.protocol.impl.record.value.job.JobRecord;
import io.zeebe.protocol.record.Record;
import io.zeebe.protocol.record.RecordType;
import io.zeebe.protocol.record.RecordValue;
import io.zeebe.protocol.record.ValueType;
import io.zeebe.protocol.record.intent.Intent;
import io.zeebe.protocol.record.intent.JobBatchIntent;
import io.zeebe.protocol.record.value.JobRecordValue;
import io.zeebe.test.util.record.RecordingExporter;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Rule;
import org.junit.Test;

public class JobEventSequenceTest {

  @Rule public final ExplicitEngineRule engine = new ExplicitEngineRule();

  @Test
  public void shouldCleanState() {
    // given
    final RecordSequences sequence =
        new RecordSequences(
            job().command(CREATE),
            job().command(CREATE),
            job().event(CREATED).refers(0),
            jobBatch(command(ACTIVATE)),
            job(command(CANCEL)));

    // when
    engine.writeRecordSequence(sequence);
    engine.start();

    // then
    final Record<JobRecordValue> canceled =
        RecordingExporter.jobRecords().withIntent(CANCELED).getFirst();

    final Record<JobRecordValue> activated =
        RecordingExporter.jobRecords().withIntent(ACTIVATED).getFirst();

    assertThat(activated.getValue().getDeadline()).isEqualTo(canceled.getValue().getDeadline());

    final List<Record<RecordValue>> records =
        RecordingExporter.records().collect(Collectors.toList());
    assertThat(records)
        .extracting(Record::getIntent)
        .containsExactly(
            CREATE, CREATED, ACTIVATE, CANCEL, ACTIVATED, JobBatchIntent.ACTIVATED, CANCELED);
  }

  private RecordMetadata command(Intent create) {
    return new RecordMetadata().intent(create).recordType(RecordType.COMMAND);
  }

  private RecordMetadata event(Intent create) {
    return new RecordMetadata().intent(create).recordType(RecordType.EVENT);
  }

  private SingleSequence job(RecordMetadata metadata) {
    metadata.valueType(ValueType.JOB);
    final JobRecord jobRecord = new JobRecord();
    jobRecord.setType("type").setRetries(3).setWorker("worker");
    return new SingleSequence(
        new CopiedRecord<>(jobRecord, metadata, 1, 1, 1024, -1, System.currentTimeMillis()));
  }

  private SingleSequence jobBatch(RecordMetadata metadata) {
    metadata.valueType(ValueType.JOB_BATCH);
    final JobBatchRecord jobBatchRecord =
        new JobBatchRecord()
            .setWorker("worker")
            .setTimeout(10_000L)
            .setType("type")
            .setMaxJobsToActivate(1);
    return new SingleSequence(
        new CopiedRecord<>(jobBatchRecord, metadata, 1, 1, 1024, -1, System.currentTimeMillis()));
  }

  public static RecordSequence sequence(RecordSequence... sequences) {
    return new RecordSequence(sequences);
  }
}
