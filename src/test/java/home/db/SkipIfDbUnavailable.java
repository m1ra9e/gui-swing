/*******************************************************************************
 * Copyright 2021-2025 Lenar Shamsutdinov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package home.db;

import static org.junit.platform.commons.util.AnnotationUtils.findAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Optional;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;

import home.Settings;
import home.db.conn.Connector;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(DbAvailabilityChecker.class)
@interface SkipIfDbUnavailable {

    String[] dbTypes();
}

final class DbAvailabilityChecker implements ExecutionCondition {

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext ctx) {
        try {
            Optional<SkipIfDbUnavailable> optional = findAnnotation(
                    ctx.getElement(), SkipIfDbUnavailable.class);
            if (optional.isPresent()) {
                SkipIfDbUnavailable annotation = optional.get();
                return getConditionEvaluationOfCurrentDb(annotation.dbTypes());
            }
        } catch (Exception e) {
            return ConditionEvaluationResult.disabled("Db is not available! "
                    + e.getMessage());
        }

        return ConditionEvaluationResult.enabled("Doesn't need to check availability of db for work.");
    }

    private ConditionEvaluationResult getConditionEvaluationOfCurrentDb(
            String[] dbTypesFromAnnotationStr) throws Exception {
        DbType currentDbType = Settings.getDatabaseType();
        for (String dbTypeFromAnnotationStr : dbTypesFromAnnotationStr) {
            if (currentDbType == DbType.getDbType(dbTypeFromAnnotationStr)) {
                return isDbAvailableForWork(currentDbType)
                        ? ConditionEvaluationResult.enabled("%s is available for work!".formatted(currentDbType))
                        : ConditionEvaluationResult.disabled("%s is not available for work!".formatted(currentDbType));
            }
        }

        return ConditionEvaluationResult.enabled(
                "Doesn't need to check availability of %s for work.".formatted(currentDbType));
    }

    private boolean isDbAvailableForWork(DbType dbType) throws Exception {
        return switch (dbType) {
            case PostgreSQL -> Connector.testCurrentConnection();
            case SQLite -> AbstractFileDatabaseTestPreparer.isAvailableWorkWithTempFile();
            default -> throw new IllegalArgumentException("Unexpected value: " + dbType);
        };
    }
}
