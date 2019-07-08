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
package io.zeebe.engine.util;

import io.zeebe.protocol.impl.record.CopiedRecord;

public class ExplicitEngineRule extends EngineRule {

  @Override
  protected void before() {
    // do nothing
  }

  public void start() {
    startProcessors();
  }

  public void writeRecordSequence(RecordSequences sequences) {
    long position = 0L;
    final RecordSequence[] recordSequences = sequences.sequences;
    for (RecordSequence record : recordSequences) {
      final CopiedRecord[] records = record.records;

      long sourceRecordPosition = -1L;
      for (CopiedRecord copiedRecord : records) {
        sourceRecordPosition =
            environmentRule.writeCompleteRecord(copiedRecord, sourceRecordPosition);
      }
      position = sourceRecordPosition;
    }
  }
}
