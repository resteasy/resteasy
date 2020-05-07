package org.jboss.resteasy.category;

/**
 * Marker interface for tests which are expected to fail because of SmallRye
 * MicroProfile OpenAPI implementation issues, see:<br>
 * - https://github.com/smallrye/smallrye-open-api/issues/290 <br>
 * - https://github.com/smallrye/smallrye-open-api/issues/248 <br>
 * and related fix:
 * https://github.com/smallrye/smallrye-open-api/pull/251
 * (there could be more issues/fixes about quite the same annotation scanning
 * topics, though)
 */
public interface ExpectedFailingBecauseOfSmallRyeMicroprofileOpenApi11 {

}
